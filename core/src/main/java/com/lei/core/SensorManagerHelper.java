package com.lei.core;

import com.lei.core.annotation.DebugClass;
import com.lei.core.annotation.DebugField;
import com.lei.core.annotation.DebugMethod;

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
import io.reactivex.subjects.BehaviorSubject;

public class SensorManagerHelper implements SensorEventListener {
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
    private List<Class> ignoreList = new ArrayList<>();

    private Disposable mDisposable;
    BehaviorSubject<String> subject;
    private boolean isDialogShow = false;
    private DialogFactory mDialogFactory;
    private IContext mContext;

    private SensorEventProvider mSensorEventProvider;
    private static SensorManagerHelper mHelper;

    private Map<String, Action> callBackHandler = new HashMap<>();

    private List<IRegisterDebug> registerDebugList = new ArrayList<>();
    private Map<String, IRegisterDebug> registerDebugMap = new HashMap<>();


    public void init() {
        if (!BuildUtils.isDebug()) return;
        start();
    }


    private SensorManagerHelper() {
        registerDebugList.add(new TaskRegisterDebug());
        registerDebugList.add(new FeetRecordRegisterDebug());
//        ignoreList.add(MainActivity.class);
        initRegister();
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


    public void addHandler(String name, Runnable runnable) {
        _addHandler(name, runnable);
    }

    public static void removeHanlder(String name) {
        if (mHelper == null) {
            mHelper = new SensorManagerHelper();
        }
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
        DialogFactory.Dialog listDialog = mDialogFactory.createDialog();
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
        subject = BehaviorSubject.create();
        // 获得传感器管理器
        mSensorEventProvider.providerSensorEvent().subscribe(new Consumer<SensorEvent>() {
            @Override
            public void accept(SensorEvent sensorEvent) throws Exception {
                onSensorChanged(sensorEvent);
            }
        });

        mDisposable = subject.throttleFirst(500, TimeUnit.MILLISECONDS).subscribe((new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                if (!BuildUtils.isDebug()) return;
                showListDialog();
            }
        }), new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
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
        float x = event.getX();
        float y = event.getY();
        float z = event.getZ();
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
            subject.onNext("1");
        }
    }

    public static class Builder {
        private DialogFactory mDialogFactory;
        private IContext mContext;

        private SensorEventProvider mSensorEventProvider;

        public Builder() {

        }

        public Builder setDialogFactory(DialogFactory dialogFactory) {
            mDialogFactory = dialogFactory;
            return this;
        }

        public Builder setContext(IContext context) {
            mContext = context;
            return this;
        }

        public Builder setSensorEventProvider(SensorEventProvider sensorEventProvider) {
            mSensorEventProvider = sensorEventProvider;
            return this;
        }

        public SensorManagerHelper build() {
            SensorManagerHelper helper = new SensorManagerHelper();
            helper.mDialogFactory = mContext.getDialogFactory();
            helper.mContext = mContext;
            helper.mSensorEventProvider = mSensorEventProvider;
            return helper;
        }
    }


}
