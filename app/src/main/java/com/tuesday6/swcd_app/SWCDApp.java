package com.tuesday6.swcd_app;

import android.app.Application;

/**
 * Created by Carl on 4/19/2015.
 */

//This is the global variable class. holds all variables for app
// it is important to set manifest to SWCDApp
    //android:name=".SWCDApp"
public class SWCDApp extends Application {

    public static String databaseMessage = "Stain List:";
    public static boolean isDeleted = false;
    public static boolean isLoggedIn = false;
    public static String noStainResult = "No Stains Found";
    public static boolean stainFound = true;
    public static boolean test = false;

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
