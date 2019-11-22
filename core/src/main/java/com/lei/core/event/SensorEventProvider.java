package com.lei.core.event;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class SensorEventProvider implements EventProvider, SensorEventListener {
    private SensorManager sensorManager;
    // 传感器
    private Sensor sensor;
    // 速度阈值，当摇晃速度达到这值后产生作用
    private final int SPEED_SHRESHOLD = 5000;
    // 两次检测的时间间隔
    private final int UPTATE_INTERVAL_TIME = 50;
    // 传感器管理器

    // 上下文对象context
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;
    BehaviorSubject<String> subject= BehaviorSubject.create();



   public SensorEventProvider(Context context){
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获得重力传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        // 注册
        if (sensor != null) {
            sensorManager.registerListener( this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public Observable<String> providerSensorEvent() {
        return subject;
    }

    /**
     * 重力感应器感应获得变化数据
     * android.hardware.SensorEventListener#onSensorChanged(android.hardware
     * .SensorEvent)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME) return;
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;
        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 10000;
        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD) {
            subject.onNext("");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
