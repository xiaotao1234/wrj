package com.example.uav_client.Application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.example.uav_client.Data.Common.User;

public class SApplication extends Application {

    static String SUPER_USER = "super";
    public static User user = new  User("1", "",SUPER_USER, 1,SUPER_USER);
    public static Activity activity1;
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            activity1 = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            activity1 = null;
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };
}
