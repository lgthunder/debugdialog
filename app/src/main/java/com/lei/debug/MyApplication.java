package com.lei.debug;

import android.app.Application;

import com.lei.core.DebugCore;
import com.lei.core.page.ComponentImp;

public class MyApplication extends Application {

    public static MyApplication mObject = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mObject = this;
        new DebugCore.Builder()
                .setComponent(new ComponentImp(MyApplication.mObject))
                .setIRegisterDebugs(new RegisterDebugProviderImp())
                .setIgnoreList(new IgnorePageProviderImp())
                .build()
                .init();

    }
}
