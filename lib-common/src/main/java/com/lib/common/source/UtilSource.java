package com.lib.common.source;

import android.content.Context;

public class UtilSource {

    private static Context sApp;

    public static void setApp(Context context) {
        sApp = context;
    }

    public static Context getApp() {
        if (sApp == null) {
            throw new IllegalStateException("must call UtilSource.setApp()");
        }
        return sApp;
    }
}
