package com.initiative.cmd.cooltour;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class CoolTourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey("e8b53132-7f53-4bd4-a3bb-acd64b03cf1a");
    }
}
