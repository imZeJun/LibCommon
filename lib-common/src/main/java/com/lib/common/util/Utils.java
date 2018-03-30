package com.lib.common.util;

import android.content.Context;

public class Utils {

    private static Context sApp;

    public static void setApp(Context context) {
        sApp = context;
    }

    public static Context getApp() {
        if (sApp == null) {
            throw new IllegalStateException("must call Utils.setApp()");
        }
        return sApp;
    }
}
