package com.lei.core;

import io.reactivex.Observable;

public interface SensorEventProvider {
    Observable<SensorEvent> providerSensorEvent();

}
