package com.example.fllodrab.measureappss;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static java.lang.Integer.parseInt;

public class NewComparison extends AppCompatActivity implements MultiSelectRecyclerViewAdapter.ViewHolder.ClickListener {

    private Toolbar toolbar;

    public RecyclerView mRecyclerView;
    private MultiSelectRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<MyApp> mArrayList = new ArrayList<>();

    Context context;
    JSONArray runningAppList = new JSONArray();   //Array de objetos appObj
    Boolean repeated = false;
    Tools tool = new Tools();
    Long receivedData;
    Long sentData;
    String nameOfApp = "";
    Drawable imageOfApp = null;
    int pid = 0;
    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comparison);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //listOfRunningApps();
        try {
            showAllRunningApps();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra todas las apps/procesos en ejecución.
     *
     * @throws PackageManager.NameNotFoundException
     */
    private void showAllRunningApps() throws PackageManager.NameNotFoundException {
        final PackageManager pm = this.getPackageManager();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

                /** Tabla de las Aplicaciones que estan siendo ejecutadas y sus PIDs correspondientes */
                List<Map> nameList = new ArrayList<>();

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
                    try {
                        startTime = processes.get(i).stat().starttime();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    oneAppObj.setName(nameOfApp);
                    oneAppObj.setImgItem(imageOfApp);

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

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MultiSelectRecyclerViewAdapter(NewComparison.this, resultList, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * Función que elimina un objeto de un array JSON de objetos JSON
     *
     * @param idx  Posición
     * @param from ArrayJSON
     * @return Nuevo array
     */
    public static JSONArray remove(final int idx, final JSONArray from) {
        final List<JSONObject> objs = asList(from);
        objs.remove(idx);

        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            ja.put(obj);
        }

        return ja;
    }

    /**
     * Cambia JSONarray por un ArrayList
     *
     * @param ja JSONArray
     * @return ArrayList
     */
    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
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
