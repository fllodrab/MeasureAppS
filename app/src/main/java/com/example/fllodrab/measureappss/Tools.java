package com.example.fllodrab.measureappss;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.halfreal.googleplayscraper.api.GooglePlayApi;
import de.halfreal.googleplayscraper.model.App;
import rx.Observable;

import static android.system.Os.sysconf;

/**
 * Created by FllodraB
 */
public class Tools {
    private int multiCriteriaDecision = 0;

    /**
     * Función que devuelve el uso de CPU de un proceso pasando por parametro su pid.
     * @param pid
     * @return long Uso de CPU de un proceso.
     */
    public double readCPUusagePerProcess(int pid) {
        try {

            RandomAccessFile readerCPUproc = new RandomAccessFile("proc/"+ pid +"/stat", "r");
            String loadCPUproc = readerCPUproc.readLine();
            String[] columnsCPUproc = loadCPUproc.split(" +");
            long utimeCPUproc = Long.parseLong(columnsCPUproc[13]); //utime - CPU time spent in user code, measured in clock ticks
            long stimeCPUproc = Long.parseLong(columnsCPUproc[14]);   //CPU time spent in kernel code, measured in clock ticks
            long cutimeCPUproc = Long.parseLong(columnsCPUproc[17]);   //Waited-for children's CPU time spent in user code (in clock ticks)
            long cstimeCPUproc = Long.parseLong(columnsCPUproc[16]);   //Waited-for children's CPU time spent in kernel code (in clock ticks)
            long starttimeCPUproc = Long.parseLong(columnsCPUproc[21]);    //Time when the process started, measured in clock ticks
            //RandomAccessFile hertzReader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
            //String loadHertz = hertzReader.readLine();
            long hertz = 100; //Long.parseLong(loadHertz); //Number of clock ticks (HERTZ), under assumption of an unmodified Android system
            long totalTime = utimeCPUproc + stimeCPUproc;   //Tiempo total de uso de la CPU por el proceso
            //totalTime = totalTime + cutimeCPUproc + cstimeCPUproc;  //Añadimos al total el tiempo de uso de CPU de los procesos hijo del proceso analizado
            RandomAccessFile uptimeReader = new RandomAccessFile("proc/uptime", "r");
            String loadUptime = uptimeReader.readLine();
            String[] valuesUptimeReader = loadUptime.split(" +");
            double uptime = Double.parseDouble(valuesUptimeReader[0]);   //The uptime of the system (seconds)
            double seconds = (uptime - (starttimeCPUproc / hertz)); //Total elapsed time in seconds since the process started
            double cpu_usage = 100 * ((totalTime / hertz) / seconds); //Total CPU usage in percentage
            return cpu_usage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 7;
    }

    /**
     * Obtiene la toma de decisión multicriterio usando el método TOPSIS.
     *
     * @param app Lista de aplicaciones a evaluar.
     * @param weights Lista de pesos de cada criterio.
     */
    public void getTOPSIS(MyApp app, Map weights, List<MyApp> appList) {
        MyApp normalizedApp = new MyApp();
        double divisorRam = 0.0;
        double divisorCpu = app.getCpu();
        double divisorSent = app.getUpload();
        double divisorReceived = app.getDownload();
        double divisorScore = app.getRating();
        double divisorDownloads = app.getNumberOfDownloads();
        double pesoRam = (double) weights.get("ram");
        double pesoCPU = (double) weights.get("cpu");
        double pesoSent = (double) weights.get("sent");
        double pesoReceived = (double) weights.get("received");
        double pesoRating = (double) weights.get("rating");
        double pesoDownloads = (double) weights.get("numberOfDownloads");
        List<MyApp> NW = new ArrayList<>();
        ArrayList<Double> ramVector = new ArrayList<>();
        ArrayList<Double> cpuVector = new ArrayList<>();
        ArrayList<Double> sentVector = new ArrayList<>();
        ArrayList<Double> receivedVector = new ArrayList<>();
        ArrayList<Double> ratingVector = new ArrayList<>();
        ArrayList<Double> downloadsVector = new ArrayList<>();
        double [][] weightMatrix;

        //multiCriteriaDecision = (int) Math.abs(((-(ram * pesoRam) - (cpu * pesoCPU) - (sent * pesoSent) - (received * pesoReceived) + (score * pesoRating) + (downloads * pesoDownloads)) / 6));

        for (int i = 0; i < appList.size(); i++) {
            /** Calcular divisores del método de normalización */
            divisorRam = divisorRam + Math.pow(appList.get(i).getRam(), 2);
            divisorCpu = divisorCpu + Math.pow((appList.get(i).getCpu()), 2);
            divisorSent = divisorSent + Math.pow((appList.get(i).getUpload()), 2);
            divisorReceived = divisorReceived + Math.pow((appList.get(i).getDownload()), 2);
            divisorScore = divisorScore + Math.pow((appList.get(i).getRating()), 2);
            divisorDownloads = divisorDownloads + Math.pow((appList.get(i).getNumberOfDownloads()), 2);

            /**
            ramVector.add(appList.get(i).getRam());
            cpuVector.add(appList.get(i).getCpu());
            sentVector.add((double) appList.get(i).getUpload());
            receivedVector.add((double) appList.get(i).getDownload());
            ratingVector.add(appList.get(i).getRating());
            downloadsVector.add(appList.get(i).getNumberOfDownloads());
             */
        }

        /** Calcular raiz de los divisores del método de normalización */
        divisorRam = Math.sqrt(divisorRam);
        divisorCpu = Math.sqrt(divisorCpu);
        divisorSent = Math.sqrt(divisorSent);
        divisorReceived = Math.sqrt(divisorReceived);
        divisorScore = Math.sqrt(divisorScore);
        divisorDownloads = Math.sqrt(divisorDownloads);

        /** Rellenamos matriz con valores normalizados */
        for (int j = 0; j < appList.size(); j++) {
            NW.get(j).setRam(appList.get(j).getRam() / divisorRam);
            NW.get(j).setCpu(appList.get(j).getCpu() / divisorCpu);
            NW.get(j).setUpload((long) (appList.get(j).getUpload() / divisorSent));
            NW.get(j).setDownload((long) (appList.get(j).getDownload() / divisorReceived));
            NW.get(j).setRating(appList.get(j).getRating() / divisorScore);
            NW.get(j).setNumberOfDownloads(appList.get(j).getNumberOfDownloads() / divisorDownloads);

        }

        /** Rellenamos matriz con valores normalizados y ponderados */
        for (int w = 0; w < appList.size(); w++) {

        }

    }

    public void getGooglePlayInfo(final MyApp app) throws InterruptedException {
        final String packageName = app.getPackageName();

        /**
        String query = "Google Maps";
        Market.AppsRequest appsRequest = Market.AppsRequest.newBuilder().setQuery(query).setStartIndex(0).setEntriesCount(1).setWithExtendedInfo(true).build();
        session.append(appsRequest, new MarketSession.Callback() {
            @Override
            public void onResult(Market.ResponseContext responseContext, Object o) {
                // Your code here //
                // response.getApp(0).getCreator() ...
                // see AppsResponse class definition for more infos

            }
        });

        GooglePlayApi api = new GooglePlayApi();
        api.search("Whatsapp", "es", "es", 1).doOnNext();
         */

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    double score = 0.0;
                    double downloads = 0.0;
                    String urlToParse = "http://play.google.com/store/apps/details?id=" + packageName;
                    //new ConnectionToPlayStore(interfaceAux).execute(urlToParse).get();
                    Map playStoreElements = dataFromPlayStore(urlToParse);
                    if (!(playStoreElements.get("downloads") == null))
                        if (!(playStoreElements.get("score") == null)) {
                            score = (Double) playStoreElements.get("score");
                            downloads = (Double) playStoreElements.get("downloads");
                        }
                    app.setRating(score);
                    app.setNumberOfDownloads(downloads);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Ejecutamos concurrentemente una hebra para la conexión con google play store
        thread.start();
        //Esperamos a que termine la hebra y bloqueamos la principal mientras
        thread.join();
    }

    public Map dataFromPlayStore (String url) {
        Map<String, Double> playStoreElements = null;
        try {
            //"https://play.google.com/store/apps/details?id=" + name
            //URL url= new URL(urls[0]);
            Document doc = Jsoup.connect(url).get();
            String paragraph = doc.title();
            Elements ratingElement = doc.select(".score");  //Obtenemos del elemento del DOM con la clase .score la puntuación de la app.
            Elements downloads = doc.select(".reviews-num");    //Obtenemos del elemento del DOM con la clase .reviews-num las descargas de la app.
            String[] ratingValue = String.valueOf(ratingElement).split(">|</"); //Pequeña expresión regular para obtener el score de la app
            String realRating = ratingValue[1].trim();
            realRating = realRating.replaceAll(",", ".");   //Sustituimos la coma por un punto para poder hacer el casting a double
            String[] downloadsValue = String.valueOf(downloads).split(">|</");
            String downloadsVal = downloadsValue[1].trim();
            downloadsVal = downloadsVal.replace(".", "");

            playStoreElements = new HashMap<>();
            playStoreElements.put("score", Double.parseDouble(realRating));
            playStoreElements.put("downloads", Double.parseDouble(downloadsVal));   //Las descargas de usuarios que han comentado y valorado la app

            Log.d("TITLE: ", String.valueOf(paragraph));
            Log.d("RATE: ", String.valueOf(playStoreElements.get("score")));
            Log.d("DOWNLOADS: ", String.valueOf(playStoreElements.get("downloads")));

        } catch (IOException e) {
            playStoreElements.put("score", (double) 0);
            playStoreElements.put("downloads", (double) 0);
            e.printStackTrace();
        }
        return playStoreElements;

    }


    /**
     * Método TOPSIS
     *
     * @param appList Lista de apps
     * @return
     */
    public int getMultiCriteriaDecision(List<MyApp> appList) {
        //Matriz normalizada y ponderada.
        List<MyApp> NW = new ArrayList<>();



        return multiCriteriaDecision;
    }

}

