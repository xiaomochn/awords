package com.xiaomo.funny.awords;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;


import com.code19.library.DeviceUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hwangjr.rxbus.Bus;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.umeng.commonsdk.UMConfigure;
import com.xiaomo.funny.awords.weex.bll.common.module.CommonModule;
import com.xiaomo.funny.awords.weex.extend.adapter.FrescoImageAdapter;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class MyApp extends Application {

    private static MyApp context;

    private boolean isDebug = false;

    public static MyApp getInstance() {
        return context;
    }


    private static List<Activity> mActivitys = Collections
            .synchronizedList(new LinkedList<Activity>());


    private static Bus sBus;

    public static synchronized Bus getBus() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }

    public static List<Activity> getmActivitys() {
        return mActivitys;
    }

    public static void setmActivitys(List<Activity> mActivitys) {
        MyApp.mActivitys = mActivitys;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        MultiDex.install(this);
//        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5ba0c677b465f569af00025c");
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, "5ba0c677b465f569af00025c", "all", UMConfigure.DEVICE_TYPE_PHONE, "");

        Logger.addLogAdapter(new AndroidLogAdapter());
        InitConfig config = new InitConfig.Builder().setImgAdapter(new FrescoImageAdapter()).build();
        WXSDKEngine.initialize(this, config);
        Fresco.initialize(this);
        registerActivityListener();
        try {
            WXSDKEngine.registerModule("CommonModule", CommonModule.class);
        } catch (WXException e) {
            e.printStackTrace();
        }

    }

    private void jpush() {
        JPushInterface.init(this);
        String registrationID = JPushInterface.getRegistrationID(this);
        String appId = DeviceUtils.getAndroidID(this);

        JPushInterface.setAlias(this, (int) (new Date().getTime()), appId);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
    }

    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    /**
                     *  监听到 Activity创建事件 将该 Activity 加入list
                     */
                    pushActivity(activity);

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null == mActivitys && mActivitys.isEmpty()) {
                        return;
                    }
                    if (mActivitys.contains(activity)) {
                        /**
                         *  监听到 Activity销毁事件 将该Activity 从list中移除
                         */
                        popActivity(activity);
                    }
                }
            });
        }
    }

}
