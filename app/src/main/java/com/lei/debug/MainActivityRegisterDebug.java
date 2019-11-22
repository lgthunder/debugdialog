package com.lei.debug;

import android.app.Activity;
import android.widget.Toast;

import com.lei.core.Action;
import com.lei.core.DebugAction;
import com.lei.core.DebugHandler;
import com.lei.core.IRegisterDebug;
import com.lei.core.annotation.DebugClass;

import java.util.Map;

@DebugClass(target = MainActivity.class)
public class MainActivityRegisterDebug implements IRegisterDebug {
    @Override
    public void registerDebug(Map<String, Action> map) {

        map.put("1", (DebugAction) () -> {

        });

        map.put("2", new DebugHandler() {
            @Override
            public void handler(Map<String, Object> params) {
                Activity activity = DebugHandler.getParams("target", params);
                Toast.makeText(activity,"test 2",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
