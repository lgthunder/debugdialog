package com.lei.debug;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lei.core.SensorEventProvider;
import com.lei.core.SensorManagerHelper;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SensorEventProvider {

    private SensorManager sensorManager;
    // 传感器
    private Sensor sensor;

    private BehaviorSubject<com.lei.core.SensorEvent> mSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManagerHelper.Builder builder = new SensorManagerHelper.Builder();
        SensorManagerHelper helper = builder.setContext(new MyContext()).setSensorEventProvider(this).build();
        helper.init();
        helper.addHandler("test", new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
        sensorManager = (SensorManager) getApplication()
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获得重力传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        // 注册
        if (sensor != null) {
            sensorManager.registerListener((SensorEventListener) this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        mSubject.onNext(new com.lei.core.SensorEvent() {
            @Override
            public float getX() {
                return sensorEvent.values[0];
            }

            @Override
            public float getY() {
                return sensorEvent.values[1];
            }

            @Override
            public float getZ() {
                return sensorEvent.values[2];
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public Observable<com.lei.core.SensorEvent> providerSensorEvent() {
        return mSubject;
    }
}
