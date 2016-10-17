package com.tehnicomsoft.androidtest;

import android.app.Application;

/**
 * Created by aleksandar on 11.10.16..
 */

public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

    }

    public static App get() {
        return app;
    }
}
