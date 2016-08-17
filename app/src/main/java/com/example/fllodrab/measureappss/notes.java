package com.example.fllodrab.measureappss;

import android.app.Application;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.StormpathLogger;

/**
 * Created by FllodraB.
 */
public class Notes extends Application {

    public static final String baseUrl = "https://glacial-shore-68285.herokuapp.com/";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Stormpath
        StormpathConfiguration stormpathConfiguration = new StormpathConfiguration.Builder()
                .baseUrl(baseUrl)
                .build();
        Stormpath.init(this, stormpathConfiguration);

        if (BuildConfig.DEBUG) {
            // we only want to show the logs in debug builds, for easier debugging
            Stormpath.setLogLevel(StormpathLogger.VERBOSE);
        }
    }

}