package com.lei.core.event;

import io.reactivex.Observable;

public interface EventProvider<T> {
    Observable<T> providerSensorEvent();

}
