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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static java.lang.Integer.parseInt;

public class NewComparison extends AppCompatActivity implements MultiSelectRecyclerViewAdapter.ViewHolder.ClickListener {

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private MultiSelectRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String> mArrayList  = new ArrayList<String>();

    Context context;
    JSONArray runningAppList = new JSONArray();   //Array de objetos appObj
    Boolean repeated = false;
    Tools tool = new Tools();
    Long receivedData;
    Long sentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comparison);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listOfRunningApps();
    }

    private void showAllRunningApps() {
        Log.d("LIST OF RUNNING APPS", "BEGINS");

        final ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

        /** Tabla de las Aplicaciones que estan siendo ejecutadas y sus PIDs correspondientes */
        List<Map> nameList = new ArrayList<>();
    }

    /**
     * Función que muestra las aplicaciones que hay en ejecución en el dispositivo.
     * Function that shows up those apps that are running on the device.
     */
    private void listOfRunningApps() {
        Log.d("LIST OF RUNNING APPS", "BEGINS");

        final long[] send = {0};
        final long[] received = {0};
        final ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

        /** Tabla de las Aplicaciones que estan siendo ejecutadas y sus PIDs correspondientes */
        List<Map> nameList = new ArrayList<>();
        final List<Integer> idList = new ArrayList<Integer>();
        final List<Integer> uidList = new ArrayList<Integer>();

        for (int i = 0; i < processes.size(); i++) {
            //nameList.add(pidsTask.get(i).processName);
            Log.d("PIDTASKNAME: ", processes.get(i).getPackageName());
            idList.add(processes.get(i).pid);
            uidList.add(processes.get(i).uid);
            try {
                ApplicationInfo app = this.getPackageManager().getApplicationInfo(processes.get(i).getPackageName(), 0);

                Drawable icon = this.getPackageManager().getApplicationIcon(app);
                Map application = new HashMap();
                application.put("name", (String) this.getPackageManager().getApplicationLabel(app));
                application.put("package", processes.get(i).getPackageName());
                application.put("pid", processes.get(i).pid);
                application.put("uid", processes.get(i).uid);
                nameList.add(application);
            } catch (PackageManager.NameNotFoundException e) {
                Toast toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT);
                //toast.show();
                e.printStackTrace();
            }
        }

        /** Estilos de la tabla */

        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#E5E5E5"), Color.parseColor("#E5E5E5")});
        gd.setGradientCenter(0.f, 1.f);
        //gd.setStroke(2,R.color.app_grey);
        gd.setLevel(2);

        /** Contenido de la tabla */
        TableLayout appsList = (TableLayout) findViewById(R.id.appsTable);

        appsList.setStretchAllColumns(true);
        appsList.bringToFront();
        for (int j = 0; j < nameList.size(); j++) {
            TableRow tr = new TableRow(this);
            final ToggleButton c1 = new ToggleButton(this);
            c1.setText((CharSequence) nameList.get(j).get("name"));
            c1.setBackgroundDrawable(gd);
            c1.setHeight(50);
            c1.setTextSize(15);
            c1.setTypeface(null, Typeface.BOLD);
            c1.setTextColor(getResources().getColor(R.color.black));
            c1.setGravity(Gravity.CENTER);
            c1.setTextOff((CharSequence) nameList.get(j).get("name"));
            c1.setTextOn((CharSequence) nameList.get(j).get("name"));
            final TextView c2 = new TextView(this);
            final TextView c3 = new TextView(this);
            final TextView c4 = new TextView(this);
            c2.setText(String.valueOf(idList.get(j)));
            c2.setVisibility(View.GONE);
            c3.setText(String.valueOf(uidList.get(j)));
            c3.setVisibility(View.GONE);
            c4.setText((CharSequence) nameList.get(j).get("package"));
            c4.setVisibility(View.GONE);
            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);
            tr.addView(c4);
            tr.setPadding(5, 5, 5, 5);
            appsList.addView(tr);
            c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //Comprobamos que no hemos checkeado ya la app
                        for (int y = 0; y < runningAppList.length(); y++) {
                            try {
                                if (runningAppList.getJSONObject(y).getString("name") == c1.getText()) {
                                    repeated = true;
                                    break;
                                } else {
                                    repeated = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //Si no se ha checkeado ya la app anteriormente, la añadimos a nuestro JSON
                        if (!repeated) {
                            //Objeto donde almacenar datos de una app
                            JSONObject appObj = new JSONObject();
                            try {
                                appObj.put("pid", c2.getText());
                                appObj.put("name", c1.getText());
                                appObj.put("package", c4.getText());
                                appObj.put("uid", c3.getText());    //A veces las apps comparten el mismo UID.
                                Debug.MemoryInfo[] memory = activityManager.getProcessMemoryInfo(new int[]{parseInt((String) appObj.get("pid"))});
                                /** De esta manera, obtenemos el consumo de memoria RAM según http://developer.android.com/tools/debugging/debugging-memory.html#TrackAllocations **/
                                appObj.put("ram", memory[0].getTotalPss());
                                Log.d("TOTAL RAM", String.valueOf(memory[0].getTotalPss()));
                                double cpuUsage = tool.readCPUusagePerProcess(parseInt((String) appObj.get("pid")));
                                //Obtenemos el tráfico de datos
                                received[0] = TrafficStats.getUidRxBytes(Integer.parseInt((appObj.getString("uid"))));  //Obtenemos la cantidad de datos recibidos
                                //Log.d("received BEFORE IF", String.valueOf(received[0]));
                                send[0] = TrafficStats.getUidTxBytes(Integer.parseInt(appObj.getString("uid")));    //Obtenemos la cantidad de datos enviados
                                //Log.d("sent BEFORE IF", String.valueOf(send[0]));
                                if(received[0] == -1) {
                                    received[0] = 0;
                                }
                                if(send[0] == -1) {
                                    send[0] = 0;
                                }

                                receivedData = received[0]/1024;
                                sentData = send[0]/1024;

                                //Log.d("CPU Usage", String.valueOf(cpuUsage));
                                appObj.put("cpu", cpuUsage);
                                appObj.put("received", receivedData);
                                appObj.put("send", sentData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                Log.d("NAME", appObj.getString("name"));
                                Log.d("pid", appObj.getString("pid"));
                                Log.d("package", appObj.getString("package"));
                                //Log.d("uid", appObj.getString("uid"));
                                //Log.d("sent", appObj.getString("send"));
                                //Log.d("received", appObj.getString("received"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            runningAppList.put(appObj);
                            //Log.d("OBJ CHECKED", String.valueOf(runningAppList));
                        }
                        buttonView.setBackgroundColor(R.color.app_background_color);
                    } else {
                        //Buscamos el objeto a eliminar
                        for (int i = 0; i < runningAppList.length(); i++) {
                            try {
                                if (runningAppList.getJSONObject(i).getString("name") == c1.getText()) {
                                    runningAppList = remove(i, runningAppList);
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //Log.d("OBJ NOT CHECKED", String.valueOf(runningAppList));
                        buttonView.setBackgroundColor(Color.LTGRAY);
                    }
                }
            });
        }
    }

    /**
     * Función que elimina un objeto de un array JSON de objetos JSON
     * @param idx Posición
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
