package com.lei.core;

import java.util.Map;

//@DebugClass(target = TaskFragment.class)
public class TaskRegisterDebug implements IRegisterDebug {

    @Override
    public void registerDebug(Map<String, Action> map) {
//        map.put("webview", (DebugAction) () -> WebViewActivity.openWebView("file:////android_asset/test.html", ""));
//
////        map.put("h5 福利视频 上报", (DebugHandler) params -> {
////            JsInteraction js = DebugHandler.getParams("js", params);
////            js.taskReport("1");
////        });
//
//        map.put("h5 通知权限上报", (DebugAction) () -> CalendarPermissionUtil.reportTask());
//
//        map.put("sign double", (DebugHandler) params -> {
//            JsInteraction js = DebugHandler.getParams("js", params);
//           TaskFragment fragment= DebugHandler.getParams("target", params);
//            Method method= DebugHandler.getParams("test", params);
//            try {
//                method.invoke(fragment,null);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//
//        });
//
//        map.put("获取金币弹窗", new DebugHandler() {
//            @Override
//            public void handler(Map<String, Object> params) {
//                JsInteraction js = DebugHandler.getParams("js", params);
//                DialogData data = new DialogData();
//                data.setPopupType("1");
//                data.setCoinNum("12");
//                data.setIsDouble("1");
//                js.popupGetCoinSuccess(JsonUtil.toJson(data));
//            }
//        });
//
//        map.put("sign double dialog", (DebugHandler) params -> {
//            JsInteraction js = DebugHandler.getParams("js", params);
//            js.showSignUpDialog(true, 2, 30);
//        });
//        map.put("task double dialog", (DebugHandler) params -> {
//            DialogData data = new DialogData();
//            data.setPopupType("1");
//            data.setCoinNum("50");
//            data.setTaskId("1");
//            data.setIsDouble("1");
//            data.setTask_name("开启签到提醒");
//            JsInteraction js = DebugHandler.getParams("js", params);
//            js.popupGetCoinSuccess(JsonUtil.toJson(data));
//        });
    }
}
