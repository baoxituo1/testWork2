package com.trade.bluehole.trad.service;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Administrator on 2015-06-15.
 */
public class ActivityVideoStackMgr {

        private static Stack<Activity> activityStack;
        private static ActivityVideoStackMgr instance;

        public static Stack<Activity> getActivityStack () {
        return activityStack;
    }

    public static ActivityVideoStackMgr getActivityStackMgr() {
        if (instance == null)
            instance = new ActivityVideoStackMgr();
        return instance;
    }

    public int ActivityVideoStackMgr() {
        return activityStack.size();
    }

    public Activity currentActivity() {
        return (Activity) activityStack.lastElement();
    }

    public Activity getActivityByName(Class paramClass) {
        int i = activityStack.size();
        int j = 0;
        if (j >= i)
            return null;
        Activity localActivity = (Activity) activityStack.get(j);
        if (localActivity == null) ;
        while (!localActivity.getClass().equals(paramClass)) {
            j++;
            break;
        }
        return localActivity;
    }

    public void popActivity() {
        Activity localActivity = (Activity) activityStack.lastElement();
        if (localActivity != null)
            localActivity.finish();
    }

    public void popActivity(Activity paramActivity) {
        if (paramActivity != null) {
            paramActivity.finish();
            activityStack.remove(paramActivity);
        }
    }

    public void popAllActivity() {
        while (true) {
            if (activityStack.size() <= 0)
                return;
            Activity localActivity;
            do {
                localActivity = currentActivity();
                popActivity(localActivity);
            }
            while (localActivity != null);
        }
    }

    public void popAllActivityExceptOne(Activity paramActivity) {
        int i = activityStack.size();
        while (true) {
            if (i <= 0) ;
            Activity localActivity;
            do {
                i--;
                localActivity = (Activity) activityStack.get(i);
                if (localActivity != paramActivity) {
                    popActivity(localActivity);
                }
            }
            while (i > 0);

        }
    }

    public void popAllActivityExceptOne(Class paramClass) {
        while (true) {
            Activity localActivity = currentActivity();
            if (localActivity != null) ;
            while (localActivity.getClass().equals(paramClass))
                return;
            popActivity(localActivity);
        }
    }

    public void popNofinishActivity(Activity paramActivity) {
        if (paramActivity != null)
            activityStack.remove(paramActivity);
    }

    public void pushActivity(Activity paramActivity) {
        if (activityStack == null)
            activityStack = new Stack();
        activityStack.add(paramActivity);
    }
}
