package com.colman.trippy;

import android.app.Application;
import android.content.Context;

public class Trippy extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
