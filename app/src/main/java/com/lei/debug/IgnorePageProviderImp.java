package com.lei.debug;

import com.lei.core.IgnorePageProvider;

import java.util.ArrayList;
import java.util.List;

public class IgnorePageProviderImp implements IgnorePageProvider {
    @Override
    public List<Class> providerIgnoreList() {
        List<Class> list =new ArrayList<>();
//        list.add(MainActivity.class);
        return list;
    }
}
