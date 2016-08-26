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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.system.Os.sysconf;

/**
 * Created by FllodraB
 */
public class Tools {

    /**
     * Función que devuelve el uso de CPU de un proceso pasando por parametro su pid.
     * @param pid
     * @return long Uso de CPU de un proceso.
     */
    public double readCPUusagePerProcess(int pid) {
        try {

            RandomAccessFile readerCPUproc = new RandomAccessFile("proc/"+ pid +"/stat", "r");
            String loadCPUproc = readerCPUproc.readLine();
            //Log.d("FILE with the stats: ", loadCPUproc);
            String[] columnsCPUproc = loadCPUproc.split(" +");

            long utimeCPUproc = Long.parseLong(columnsCPUproc[13]); //utime - CPU time spent in user code, measured in clock ticks
            //Log.d("utime", String.valueOf(utimeCPUproc));
            long stimeCPUproc = Long.parseLong(columnsCPUproc[14]);   //CPU time spent in kernel code, measured in clock ticks
            //Log.d("stime", String.valueOf(stimeCPUproc));
            long cutimeCPUproc = Long.parseLong(columnsCPUproc[17]);   //Waited-for children's CPU time spent in user code (in clock ticks)
            long cstimeCPUproc = Long.parseLong(columnsCPUproc[16]);   //Waited-for children's CPU time spent in kernel code (in clock ticks)
            long starttimeCPUproc = Long.parseLong(columnsCPUproc[21]);    //Time when the process started, measured in clock ticks
            //RandomAccessFile hertzReader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
            //String loadHertz = hertzReader.readLine();
            long hertz = 100; //Long.parseLong(loadHertz); //Number of clock ticks (HERTZ), under assumption of an unmodified Android system
            //Log.d("Hertz", String.valueOf(hertz));
            long totalTime = utimeCPUproc + stimeCPUproc;   //Tiempo total de uso de la CPU por el proceso
            //totalTime = totalTime + cutimeCPUproc + cstimeCPUproc;  //Añadimos al total el tiempo de uso de CPU de los procesos hijo del proceso analizado
            RandomAccessFile uptimeReader = new RandomAccessFile("proc/uptime", "r");
            String loadUptime = uptimeReader.readLine();
            //Log.d("uptime FILE: ", loadUptime);
            String[] valuesUptimeReader = loadUptime.split(" +");
            double uptime = Double.parseDouble(valuesUptimeReader[0]);   //The uptime of the system (seconds)
            //Log.d("UPTIME", String.valueOf(uptime));
            //Log.d("STARTTIME", String.valueOf(starttimeCPUproc));
            //Log.d("TOTALTIME", String.valueOf(totalTime));
            double seconds = (uptime - (starttimeCPUproc / hertz)); //Total elapsed time in seconds since the process started
            double cpu_usage = 100 * ((totalTime / hertz) / seconds); //Total CPU usage in percentage
            //Log.d("SECONDS", String.valueOf(seconds));
            //Log.d("CPU_USAGE", String.valueOf(cpu_usage));
            return cpu_usage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 7;
    }

}

