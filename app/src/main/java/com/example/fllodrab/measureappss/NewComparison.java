package com.example.fllodrab.measureappss;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;

public class NewComparison extends AppCompatActivity implements MultiSelectRecyclerViewAdapter.ViewHolder.ClickListener {

    private Toolbar toolbar;

    public RecyclerView mRecyclerView;
    private MultiSelectRecyclerViewAdapter mAdapter;
    private ArrayList<MyApp> mArrayList = new ArrayList<>();
    private ArrayList<MyApp> evaluateList = new ArrayList<>();

    Context context;
    Tools toolForApps = new Tools();
    String nameOfApp = "";
    Drawable imageOfApp = null;
    double cpuUsage;
    double ramUsage;
    long uploadData;
    long downloadData;
    double rating;
    double marketDownloads;
    int pid = 0;
    int uid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comparison);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        long[] send = {0};
        long[] received = {0};
        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Cargando los pesos...", Snackbar.LENGTH_LONG)
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                evaluateSelectedItems(evaluateList);
                                Intent intent = new Intent(NewComparison.this, Parameters.class);
                                startActivity(intent);
                            }
                        }).show();


            }
        });

        try {
            showAllRunningApps(activityManager, send, received);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra todas las apps/procesos en ejecución.
     *
     * @throws PackageManager.NameNotFoundException
     */
    private void showAllRunningApps(final ActivityManager activityManager, final long[] send, final long[] received) throws PackageManager.NameNotFoundException {
        final PackageManager pm = this.getPackageManager();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

                for (int i = 0; i < processes.size(); i++) {
                    MyApp oneAppObj = new MyApp();

                    try {
                        Log.d("NAME", processes.get(i).getPackageInfo(context, 0).applicationInfo.loadLabel(pm).toString());
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        nameOfApp = processes.get(i).getPackageInfo(context, 0).applicationInfo.loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        imageOfApp = processes.get(i).getPackageInfo(context, 0).applicationInfo.loadIcon(pm);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    pid = processes.get(i).pid;
                    uid = processes.get(i).uid;
                    cpuUsage = toolForApps.readCPUusagePerProcess(pid);
                    Debug.MemoryInfo[] memory = activityManager.getProcessMemoryInfo(new int[]{pid});
                    ramUsage = memory[0].getTotalPss();
                    //Obtenemos el tráfico de datos
                    received[0] = TrafficStats.getUidRxBytes(Integer.parseInt(String.valueOf(uid)));  //Obtenemos la cantidad de datos recibidos
                    send[0] = TrafficStats.getUidTxBytes(Integer.parseInt(String.valueOf(uid)));    //Obtenemos la cantidad de datos enviados
                    if(received[0] == -1) {
                        received[0] = 0;
                    }
                    if(send[0] == -1) {
                        send[0] = 0;
                    }

                    downloadData = received[0]/1024;
                    uploadData = send[0]/1024;

                    /** Rellenamos los objetos */
                    oneAppObj.setName(nameOfApp);
                    oneAppObj.setImgItem(imageOfApp);
                    oneAppObj.setCpu(cpuUsage);
                    oneAppObj.setRam(ramUsage);
                    oneAppObj.setUpload(uploadData);
                    oneAppObj.setDownload(downloadData);

                    mArrayList.add(oneAppObj);
                }
            }
        });
        thread.start();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Lista de aplicaciones disponibles");

        }

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /** Comprobamos que no haya procesos repetidos */
        List<MyApp> resultList = new ArrayList<MyApp>();
        Set<String> uniques = new HashSet<String>();

        for (MyApp oneApp : mArrayList) {
            if (uniques.add(oneApp.getName())) {
                resultList.add(oneApp);
            }
        }

        evaluateList.addAll(resultList);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MultiSelectRecyclerViewAdapter(NewComparison.this, resultList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void evaluateSelectedItems(List<MyApp> resultList) {
        int elementSelected;

        for (int i = 0; i < mAdapter.getSelectedItems().size(); i++) {
            elementSelected = mAdapter.getSelectedItems().get(i);
            ((MyAppList) this.getApplication()).setMeasures(resultList.get(elementSelected));
        }
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onItemClicked(int position) {
        toggleSelection(position);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
    }
}
