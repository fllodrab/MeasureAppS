package com.example.fllodrab.measureappss;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.models.StormpathError;
import com.stormpath.sdk.models.UserProfile;
import com.stormpath.sdk.ui.StormpathLoginActivity;
import com.stormpath.sdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    EditText mNote;
    Context context;
    private OkHttpClient okHttpClient;
    public static final String ACTION_GET_NOTES = "notes.get";
    public static final String ACTION_POST_NOTES = "notes.post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        // Initialize OkHttp library.
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Stormpath.logger().d(message);
            }
        });

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, getString(R.string.saving), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //saveNote();

            }
        });

        mNote = (EditText)findViewById(R.id.note);
        mNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mNote.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();

        IntentFilter noteGetFilter = new IntentFilter(ACTION_GET_NOTES);
        IntentFilter notePostFilter = new IntentFilter(ACTION_POST_NOTES);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNoteReceived, noteGetFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNoteReceived, notePostFilter);

        Stormpath.getUserProfile(new StormpathCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                getNotes();
                listOfRunningApps();
            }

            @Override
            public void onFailure(StormpathError error) {
                // Show login view
                startActivity(new Intent(context, StormpathLoginActivity.class));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNoteReceived);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            mNote.setText(""); //clears edit text, could alternatively save to shared preferences

            Stormpath.logout();
            startActivity(new Intent(context, StormpathLoginActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver onNoteReceived = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().contentEquals(ACTION_GET_NOTES)) {
                mNote.setText(intent.getExtras().getString("notes"));
                try {
                    showSessionLastSession(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().contentEquals(ACTION_POST_NOTES)) {
                Snackbar.make(mNote, getString(R.string.saved), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    };

    private void saveNote() {
        RequestBody requestBody = new FormBody.Builder()
                .add("notes", mNote.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(Notes.baseUrl + "notes")
                .headers(buildStandardHeaders((Stormpath.accessToken())))
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                Intent intent = new Intent(ACTION_POST_NOTES);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    private void getNotes() {
        Request request = new Request.Builder()
                .url(Notes.baseUrl + "notes")
                .headers(buildStandardHeaders(Stormpath.accessToken()))
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                JSONObject mNotes;

                try {
                    mNotes = new JSONObject(response.body().string());
                    String noteCloud = mNotes.getString("notes");

                    // You can also include some extra data.
                    Intent intent = new Intent(ACTION_GET_NOTES);
                    intent.putExtra("notes", noteCloud);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } catch (JSONException e) {
                }
            }
        });
    }

    private Headers buildStandardHeaders(String accessToken) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");

        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }

        return builder.build();
    }

    /**
     * Función que muestra las aplicaciones que hay en ejecución en el dispositivo.
     * Function that shows up those apps that are running on the device.
     */
    private void listOfRunningApps() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager.getRunningAppProcesses();

        for (int i = 0; i < pidsTask.size(); i++) {
            //nameList.add(pidsTask.get(i).processName);
            Log.d("PIDTASKNAME: ", pidsTask.get(i).processName);
            //idList.add(pidsTask.get(i).pid);
            //uidList.add(pidsTask.get(i).uid);
            try {
                ApplicationInfo app = this.getPackageManager().getApplicationInfo(pidsTask.get(i).processName, 0);

                Drawable icon = this.getPackageManager().getApplicationIcon(app);
                Map application = new HashMap();
                application.put("name", (String) this.getPackageManager().getApplicationLabel(app));
                application.put("package", pidsTask.get(i).processName);
                application.put("pid", pidsTask.get(i).pid);
                application.put("uid", pidsTask.get(i).uid);
                //nameList.add(application);
            } catch (PackageManager.NameNotFoundException e) {
                Toast toast = Toast.makeText(this, "error in getting icon", Toast.LENGTH_SHORT);
                //toast.show();
                e.printStackTrace();
            }
        }
    }

    private void showSessionLastSession(Intent intent) throws JSONException {
        JSONObject jsonNotes = new JSONObject(intent.getStringExtra("notes"));

        // Create an instance of SectionedRecyclerViewAdapter
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        //EXAMPLE
        List<String> messages = Arrays.asList(jsonNotes.getString("rankingTitle"), jsonNotes.getString("app1"), jsonNotes.getString("app2"), jsonNotes.getString("app3"), jsonNotes.getString("app4"));

        // Create your sections with the list of data you got from your API
        MySection data1Section = new MySection("Última Comparación", messages);

        // Add your Sections
        sectionAdapter.addSection(data1Section);

        // Set up your RecyclerView with the SectionedRecyclerViewAdapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sessionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);
    }

    public Context getContext() {
        return context;
    }
}
