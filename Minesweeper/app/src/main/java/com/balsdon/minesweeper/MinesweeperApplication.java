package com.balsdon.minesweeper;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class MinesweeperApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Start", "Start");
        Crashlytics.start(this);
    }
}
