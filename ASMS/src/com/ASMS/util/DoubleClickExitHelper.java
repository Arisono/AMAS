package com.ASMS.util;

import com.ASMS.app.AppApplication;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;


/**
 * 双击退出
 * 创建日期 2014-05-12
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * 
 */
public class DoubleClickExitHelper {

	private final Activity mActivity;
	
	private boolean isOnKeyBacking;
	private Handler mHandler;
	private Toast mBackToast;
	
	//构造方法    
	public DoubleClickExitHelper(Activity activity) {
		mActivity = activity;
		//如果你的Handler是要来刷新操作UI的，那么就需要在主线程下跑
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	/**
	 * Activity onKeyDown事件
	 * */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode != KeyEvent.KEYCODE_BACK) {
			return false;
		}
		if(isOnKeyBacking) {
			//返回键两次，线程队列删除onBackTimeRunnable
			mHandler.removeCallbacks(onBackTimeRunnable);
			if(mBackToast != null){
				mBackToast.cancel();
			}
			// 退出
			AppApplication.getInstance().exit();
			return true;
		} else {
			//第一次按返回键
			isOnKeyBacking = true;
			if(mBackToast == null) {
		// 第三个参数：显示的时间长短。Toast默认的有两个LENGTH_LONG(长)和LENGTH_SHORT(短)，也可以使用毫秒如2000ms 
				mBackToast = Toast.makeText(mActivity, "再次点击，退出应用！", 1000);
			}
			mBackToast.show();
			//方法postDelayed的作用是延迟多少毫秒后开始运行
			//延迟两秒钟执行，在两秒钟之内再次按返回键，isOnKeyBacking ==true
			mHandler.postDelayed(onBackTimeRunnable, 5000);
			return true;
		}
	}
	
	private Runnable onBackTimeRunnable = new Runnable() {
		
		@Override
		public void run() {
			isOnKeyBacking = false;
			if(mBackToast != null){
				mBackToast.cancel();
			}
		}
	};
}
