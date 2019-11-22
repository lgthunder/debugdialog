package com.lei.core;

import java.util.Map;

public abstract class DebugHandler implements Action {
    abstract void handler(Map<String,Object> params);
     public   <T> T getParams(String key,Map<String,Object> map){
        return (T)map.get(key);
    }
}
