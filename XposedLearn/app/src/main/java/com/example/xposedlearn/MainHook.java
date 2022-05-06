package com.example.xposedlearn;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.app.Notification;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainHook implements IXposedHookLoadPackage {
        private static final String TAG = "xposed_learn:";
        private static final String TARGET_PACKAGE = "com.example.notificationtest";
        private static ClassLoader classLoader;

        @Override
        public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
                String packageName = lpparam.packageName;
                XposedBridge.log(TAG + packageName);
                if (packageName.equals(TARGET_PACKAGE)) {
                        classLoader = lpparam.classLoader;
                        //className classLoader methodName parameterTypesAndCallback
                        //XposedHelpers.findAndHookMethod("android.app.NotificationManager",
                        //        lpparam.classLoader, "notify", ......

                        //class methodName parameterTypesAndCallback

                        XC_MethodHook methodHook;

                        //methodHook = new infoMethod();
                        //methodHook = new preventMethod();
                        //methodHook = new changeMethod();
                        methodHook = new replaceMethod();

                        Class<?> clazzNotificationManager = XposedHelpers.findClass(
                                "android.app.NotificationManagerCompat", classLoader);
                        XposedHelpers.findAndHookMethod(clazzNotificationManager, "notify",
                                String.class, int.class, Notification.class, methodHook);

                }
        }

        static class infoMethod extends XC_MethodHook {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + "infoMethod");
                        String tag = (String)param.args[0];
                        int id = (int)param.args[1];
                        Notification notification = (Notification) param.args[2];
                        Bundle notificationExtras = notification.extras;
                        XposedBridge.log(TAG+" tag:" + tag + " id:"+id + " extras"+ notificationExtras);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object res = param.getResult();
                        XposedBridge.log(TAG+res);
                }
        }

        static class preventMethod extends XC_MethodHook {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG+"preventMethod");
                        param.setResult(null);
                }
        }

        static class changeMethod extends XC_MethodHook {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG+"changeMethod");
                        Notification notification = (Notification) param.args[2];
                        Bundle notificationExtras = notification.extras;
                        notificationExtras.putString("android.title", "hooked title");
                        notificationExtras.putString("android.text", "hooked text");
                        notification.extras = notificationExtras;
                        param.args[2] = notification;
                }
        }

        static class replaceMethod extends XC_MethodReplacement {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG+"replaceMethod");
                        Toast toast = Toast.makeText(AndroidAppHelper.currentApplication().getApplicationContext(),
                                "replaced!", Toast.LENGTH_SHORT);
                        toast.show();
                        /* endless loop
                        Class<?> clazzNotificationManager = XposedHelpers.findClass(
                                "android.app.NotificationManager", classLoader);
                        Method notify = clazzNotificationManager.getMethod("notify",
                                String.class, int.class, Notification.class);
                        notify.invoke(param.thisObject,param.args);
                        */
                        return null;
                }
        }
}

