package com.lei.core;

import java.util.Map;

//@DebugClass(target = UserFeetRecordActivity.class)
public class FeetRecordRegisterDebug implements IRegisterDebug {
    @Override
    public void registerDebug(Map<String, Action> map) {
//        map.put("progress 25", (DebugHandler) params -> {
//            setProgress(params, 25);
//        });
//        map.put("progress 50", (DebugHandler) params -> setProgress(params, 50));
//        map.put("progress 75", (DebugHandler) params -> setProgress(params, 75));
//        map.put("progress 100", (DebugHandler) params -> setProgress(params, 100));
    }

    private void setProgress(Map<String, Object> params, int progress) {
//        CircleProgress circleProgress = DebugHandler.getParams("progress", params);
//        circleProgress.update(progress, 1000);
    }
}