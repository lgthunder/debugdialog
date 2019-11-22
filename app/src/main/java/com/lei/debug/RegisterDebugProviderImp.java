package com.lei.debug;

import com.lei.core.IRegisterDebug;
import com.lei.core.RegisterDebugProvider;

import java.util.ArrayList;
import java.util.List;

public class RegisterDebugProviderImp implements RegisterDebugProvider {
    @Override
    public List<IRegisterDebug> getRegisterDebugs() {
        List<IRegisterDebug> list =new ArrayList<>();
        list.add(new MainActivityRegisterDebug());
        return list;
    }
}
