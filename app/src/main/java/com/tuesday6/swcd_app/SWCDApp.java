package com.tuesday6.swcd_app;

import android.app.Application;

/**
 * Created by cfair_000 on 4/19/2015.
 */
public class SWCDApp extends Application {

    public static String databaseMessage = "Stain List:";
    public static boolean isDeleted = false;
    public static boolean isLoggedIn = false;

    private static SWCDApp singleton;

    public static SWCDApp getInstance(){
        return singleton;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        singleton = this;
    }
}