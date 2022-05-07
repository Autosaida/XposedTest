package com.example.hookeasyreader;

import android.content.Context;
import android.content.Intent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage{
    private static final String TARGET_PACKAGE = "com.netease.pris";
    private static final String TAG = "Xposed MainHook EasyReader:";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(lpparam.packageName.equals(TARGET_PACKAGE)) {
            XposedBridge.log(TAG+"loaded "+TARGET_PACKAGE);

            ClassLoader classLoader = lpparam.classLoader;
            Class<?> clazzPrefConfig = XposedHelpers.findClass("com.netease.config.PrefConfig",classLoader);
            XposedBridge.log(TAG+clazzPrefConfig);

            XposedHelpers.findAndHookMethod(clazzPrefConfig, "j",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(false);
                        }
                    });
            XposedHelpers.findAndHookMethod(clazzPrefConfig, "g",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(false);
                        }
                    });
            XposedHelpers.findAndHookMethod(clazzPrefConfig, "aP",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult("");
                        }
                    });

            Class<?> clazzMainGridActivity = XposedHelpers.findClass(
                    "com.netease.pris.activity.MainGridActivity", classLoader);
            XposedHelpers.findAndHookMethod(clazzMainGridActivity, "b", Context.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            Context context = (Context) param.args[0];
                            Intent intent = new Intent(context, clazzMainGridActivity);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            return null;

                        }
                    });
        }
    }
}
