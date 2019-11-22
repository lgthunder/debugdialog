package com.lei.core.page;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

public class ComponentImp implements Component , Application.ActivityLifecycleCallbacks {
    private LinkedList<WeakReference<Activity>> mActivityList = new LinkedList<>();
    private Activity mCurrentActivity;
    private Application mApplication;
    public ComponentImp(Application application){
        mApplication=application;
        application.registerActivityLifecycleCallbacks(this);

    }
    @Override
    public Object[] getPageList() {
        return new Object[]{mCurrentActivity};
    }

    @Override
    public Object getCurrentPage() {
        return mCurrentActivity;
    }

    @Override
    public Application getApplication() {
        return mApplication;
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        mActivityList.addLast(new WeakReference<>(activity));
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mCurrentActivity=activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        mCurrentActivity=null;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        for (Iterator<WeakReference<Activity>> obj = mActivityList.iterator(); obj.hasNext(); ) {
            WeakReference<Activity> next = obj.next();
            Activity value = next.get();
            if (value != null && value.equals(activity)) {
                obj.remove();
            }

        }
    }
}
