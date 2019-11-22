package com.lei.core;

import android.content.Context;

import com.lei.core.annotation.DebugClass;
import com.lei.core.annotation.DebugField;
import com.lei.core.annotation.DebugMethod;
import com.lei.core.event.EventProvider;
import com.lei.core.event.SensorEventProvider;
import com.lei.core.page.Component;
import com.lei.core.ui.DialogFactory;
import com.lei.core.ui.SimpleListDialogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DebugCore {

    private List<Class> ignoreList = new ArrayList<>();

    private Disposable mDisposable;
    private boolean isDialogShow = false;
    private DialogFactory mDialogFactory;
    private Component mContext;

    private EventProvider mEventProvider;
    private static DebugCore mHelper;

    private Map<String, Action> callBackHandler = new HashMap<>();

    private List<IRegisterDebug> registerDebugList = new ArrayList<>();
    private Map<String, IRegisterDebug> registerDebugMap = new HashMap<>();


    public void init() {
        if (!BuildUtils.isDebug()) return;
        start();
    }


    private DebugCore() {
        mHelper = this;
//        ignoreList.add(MainActivity.class);
    }

    private void initRegister() {
        for (IRegisterDebug debug : registerDebugList) {
            if (debug.getClass().isAnnotationPresent(DebugClass.class)) {
                DebugClass debugClass = debug.getClass().getAnnotation(DebugClass.class);
                String key = debugClass.target().getSimpleName();
                registerDebugMap.put(key, debug);
            }
        }
    }


    public static void addHandler(String name, Runnable runnable) {
        mHelper._addHandler(name, runnable);
    }

    public static void removeHanlder(String name) {
        mHelper._removeHandler(name);
    }

    public static void addIgnoreClass(Class aClass) {
        mHelper.ignoreList.add(aClass);
    }

    public static void removeIgnoreClass(Class aClass) {
        mHelper.ignoreList.remove(aClass);
    }

    public static boolean hasIgnoreClass(Class aClass) {
        return mHelper.ignoreList.contains(aClass);
    }


    private void _removeHandler(String name) {
        callBackHandler.remove(name);
    }

    private void _addHandler(String name, Runnable runnable) {
        callBackHandler.put(name, new DebugAction() {
            @Override
            public void call() {
                runnable.run();
            }
        });
    }

    private void showListDialog() {
        if (isDialogShow) return;

        Object page = mContext.getCurrentPage();
        if (page == null) return;
        for (Class aClass : ignoreList) {
            if (page.getClass().equals(aClass)) {
                return;
            }
        }

        Map<String, Combine> listContent = new LinkedHashMap<>();
        //无页面绑定
        listContent.putAll(combineAction(callBackHandler, null));


        Object[] pages = mContext.getPageList();
        //直接绑定
        for (Object p : pages) {
            parserDebugData(listContent, p);
        }


//        if (activity instanceof FragmentActivity) {
//            FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
//            List<Fragment> list = manager.getFragments();
//            for (Fragment fragment : list) {
//                if (fragment instanceof IRegisterDebug) {
//                    Map<String, Action> data = apply((IRegisterDebug) fragment);
//                    listContent.putAll(combineAction(data, fragment));
//                }
//                parserDebugData(listContent, fragment);
//            }
//        }

//        final String[] items = concat(keySet.toArray(new String[keySet.size()]), tempKeySet.toArray(new String[tempKeySet.size()]));

        final String[] items = listContent.keySet().toArray(new String[listContent.size()]);
        DialogFactory.Dialog listDialog = mDialogFactory.createDialog((Context) page);
        listDialog.setTitle("DEBUG包测试弹窗");
        listDialog.setItems(items, (dialog, which) -> {
            String key = items[which];
            Combine call = listContent.get(key);
            if (call != null) {
                if (call.getAction() instanceof DebugAction) {
                    ((DebugAction) call.getAction()).call();
                }
                if (call.getAction() instanceof DebugHandler) {
                    ((DebugHandler) call.getAction()).handler(parserParams(call.getTarget()));
                }
            }
        });
        listDialog.setOnCancelListener((dialog) -> isDialogShow = false);
        listDialog.setOnDismissListener((dialog) -> isDialogShow = false);
        listDialog.show();
        isDialogShow = true;
    }


    private void parserDebugData(Map<String, Combine> listContent, Object object) {
        String key = object.getClass().getSimpleName();
        IRegisterDebug debug = registerDebugMap.get(key);
        if (debug != null) {
            Map<String, Action> data = apply(debug);
            Map<String, Combine> combineMap = combineAction(data, object);
            listContent.putAll(combineMap);
        }
    }

    private Map<String, Action> apply(IRegisterDebug debug) {
        Map<String, Action> content = new LinkedHashMap<>();
        debug.registerDebug(content);
        return content;
    }


    private Map<String, Combine> combineAction(Map<String, Action> params, Object target) {
        Map<String, Combine> map = new LinkedHashMap<>();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            Action action = params.get(key);
            Combine combine = new Combine(action, target);
            map.put(key, combine);
        }
        return map;
    }

    private Map<String, Object> parserParams(Object target) {
        Map<String, Object> params = new HashMap<>();
        params.put("target", target);
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DebugField.class)) {
                field.setAccessible(true);
                try {
                    Object object = field.get(target);
                    DebugField debugField = field.getAnnotation(DebugField.class);
                    String key = debugField.value();
                    params.put(key, object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Method[] methods = target.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(DebugMethod.class)) {
                method.setAccessible(true);
                DebugMethod debugMethod = method.getAnnotation(DebugMethod.class);
                String key = debugMethod.value();
                params.put(key, method);
            }
        }
        return params;
    }

    public static String[] concat(String[] a, String[] b) {
        if (a.length == 0) return b;
        if (b.length == 0) return a;
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;

    }

    /**
     * 开始检测
     */
    public void start() {
        if (mDisposable != null)
            mDisposable.dispose();
        mDisposable = mEventProvider.providerSensorEvent()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe((o -> {
                    if (!BuildUtils.isDebug()) return;
                    showListDialog();
                }), new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        isDialogShow = false;
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 停止检测
     */
    public void stop() {
        if (mDisposable != null)
            mDisposable.dispose();
    }


    public static class Builder {
        private DialogFactory mDialogFactory;
        private Component mComponent;
        private EventProvider mEventProvider;
        private RegisterDebugProvider mIRegisterDebugs;
        private IgnorePageProvider mIgnorePageProvider;

        public Builder() {

        }

        public Builder setDialogFactory(DialogFactory dialogFactory) {
            mDialogFactory = dialogFactory;
            return this;
        }

        public Builder setComponent(Component context) {
            mComponent = context;
            return this;
        }

        public Builder setEventProvider(EventProvider eventProvider) {
            mEventProvider = eventProvider;
            return this;
        }

        public Builder setIRegisterDebugs(RegisterDebugProvider provider) {
            mIRegisterDebugs = provider;
            return this;
        }

        public Builder setIgnoreList(IgnorePageProvider provider) {
            this.mIgnorePageProvider = provider;
            return this;
        }

        public DebugCore build() {
            DebugCore helper = new DebugCore();
            if (mComponent == null)
                throw new RuntimeException("debug core : component cant be null");
            helper.mContext = mComponent;
            helper.mDialogFactory = mDialogFactory == null ? new SimpleListDialogFactory() : mDialogFactory;
            helper.mEventProvider = mEventProvider == null ? new SensorEventProvider(mComponent.getApplication()) : mEventProvider;
            if (mIgnorePageProvider != null)
                helper.ignoreList = mIgnorePageProvider.providerIgnoreList() == null ? helper.ignoreList : mIgnorePageProvider.providerIgnoreList();
            if (mIRegisterDebugs != null)
                helper.registerDebugList = mIRegisterDebugs.getRegisterDebugs() == null ? helper.registerDebugList : mIRegisterDebugs.getRegisterDebugs();
            helper.initRegister();
            return helper;
        }


    }


}
