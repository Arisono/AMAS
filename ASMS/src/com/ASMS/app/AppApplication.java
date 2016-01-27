package com.ASMS.app;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.Application;

public class AppApplication extends Application {
	private static  AppApplication instance;
	private List<Activity> activityList = new ArrayList<Activity>();
	
	
	public static synchronized AppApplication getInstance() {
		if (null == instance) {
			instance = new AppApplication();
		}
		return instance;
	}
	
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityList.remove(activity);
			activity.finish();
			activity = null;
		}
	}
	
	public void finishAllActivity() {
		if (activityList != null) {
			Activity activity;
			for (int i = 0; i < activityList.size(); i++) {
				System.out.println("i=" + activityList.get(i).toString());
				activity = activityList.get(i);
				activity.finish();
			}
			activityList.clear();
		}
	}
	
	public void exit() {
		if (activityList != null) {
			Activity activity;
			for (int i = 0; i < activityList.size(); i++) {
				System.out.println("i=" + activityList.get(i).toString());
				activity = activityList.get(i);
				activity.finish();
			}
			activityList.clear();
		}
	}

	
}
