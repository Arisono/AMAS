package com.ASMS.activity;

import java.util.ArrayList;
import java.util.List;

import com.ASMS.entity.Contacts;
import com.ASMS.util.CommonUtil;
import com.github.clans.fab.FloatingActionButton;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
  * @author Administrator
  * @功能:短信群发界面
  */
public class MainActivity extends Activity {

	@Bind(R.id.bt_template) ImageButton bt_template;
	@Bind(R.id.bt_person) ImageButton bt_person;
	@Bind(R.id.bt_delete) ImageButton bt_delete;
	@Bind(R.id.bt_send) ImageButton bt_send;
	
	@Bind(R.id.fab) FloatingActionButton fb_scan;
	
	@Bind(R.id.et_bottom_template) EditText ed_template;
	@Bind(R.id.et_header_content) EditText ed_content;
	
	List<Contacts> mContacts=new ArrayList<Contacts>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		fb_scan.hide(false);
	     new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	            	fb_scan.show(true);
	            	fb_scan.setShowAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_from_bottom));
	            	fb_scan.setHideAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_to_bottom));
	            }
	     }, 800);
		
	}

	final int request_code_template=1;
	final int request_code_multiselect=2;
	@OnClick(R.id.bt_template) void bt_template(){
		CommonUtil.startActivityForResult(this, CustomTempletActivity.class, request_code_template);
	}
	
	@OnClick(R.id.bt_person) void bt_person(){
		CommonUtil.startActivityForResult(this, MultiSelectActivity.class, request_code_multiselect);
    }
	
	@OnClick(R.id.bt_delete) void bt_delete(){
		ed_template.setText("");
	}
	
	@OnClick(R.id.bt_send) void bt_send(){
		if (TextUtils.isEmpty(ed_template.getText().toString())) {
			CommonUtil.showToast(this, "短信内容为空！");
			return;
		}
		List<Contacts> contacts=getSelectedContacts();
		if (contacts!=null) {
			if (!contacts.isEmpty()) {
				for (int i = 0; i < contacts.size(); i++) {
					Contacts eContacts=contacts.get(i);
					String content=ed_template.getText().toString().replace(
							"@昵称", !TextUtils.isEmpty(eContacts.getNickname())
							?eContacts.getNickname():eContacts.getName());
					sendASMS(eContacts.getName(), eContacts.getPhone(), content);
				}
			}
			else{
				CommonUtil.showToast(this, "请指定联系人！");
			}
		}else{
			CommonUtil.showToast(this, "请指定联系人");
		}
	}
	
	@OnClick(R.id.et_bottom_template) void bt_content(){
		
	}
	
	
	@OnClick(R.id.fab) void bt_scan(){
		if (!TextUtils.isEmpty(ed_template.getText().toString())) {
			
			List<Contacts> isSelected = getSelectedContacts();
			if (isSelected.isEmpty()) {
				CommonUtil.showToast(this, "请选择联系人！");
				return;
			}
			Intent intent=new Intent(this,CustomTempletActivity.class);
			intent.putExtra("flag", "scanForMain");
			intent.putExtra("template", ed_template.getText().toString());
			intent.putParcelableArrayListExtra("Contacts", (ArrayList<? extends Parcelable>) isSelected);
			CommonUtil.startActivity(this,intent);
		}else{
			CommonUtil.showToast(this, "发送的内容为空,无法预览！");
		}
	}

	private List<Contacts> getSelectedContacts() {
		List<Contacts> isSelected=new ArrayList<Contacts>();
		for (int i = 0; i <mContacts.size(); i++) {
			Contacts eContacts=mContacts.get(i);
			if (eContacts.ischecked) {
				isSelected.add(eContacts);
			}
		}
		return isSelected;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case request_code_template:
			if (data!=null) {
				String template=data.getStringExtra("template");
				Log.i("request_code", template);
				//CommonUtil.showToast(this, template);
				CommonUtil.insertSpanForTextView(ed_template, template, "@昵称");
				if (template.length()%67>0) {
					int num=template.length()/67+1;
					CommonUtil.showToast(this, "请注意：短信将拆分"+num+"条");
				}else{
					int num=template.length()/67==0?1:template.length()/67;
					CommonUtil.showToast(this, "请注意：短信将拆分"+num+"条");
				}
			}
			break;
		case request_code_multiselect:
			if (data==null) {
				return ;
			}
		   mContacts=data.getParcelableArrayListExtra("Contacts");
		   if (!mContacts.isEmpty()) {
			   StringBuffer sb=new StringBuffer();
				int num = 0;
			   for (int i = 0; i <mContacts.size(); i++) {
					if (mContacts.get(i).ischecked) {
						Log.i("ischecked", mContacts.get(i).getName());
						sb.append(mContacts.get(i).getName()+",");
						num++;
					}
				}
			   
			   if (num!=0) {
				   String content=  (String) sb.subSequence(0, sb.length()-1);
				   content= content+"   "+num+"人";
				   CommonUtil.insertSpanForTextView(ed_content, content, String.valueOf(num));
			    }else{
			       ed_content.setText("");
			    }
			 
			   //ed_content.setText(content);
		     }
			
		   break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	String SENT_SMS_ACTION="SENT_SMS_ACTION";  
    String DELIVERED_SMS_ACTION="DELIVERED_SMS_ACTION";  
	
	private void sendASMS(String name,String number, String message){
		   Log.i("ASMS", "\n姓名："+name+"号码："+number+"\n正文："+message);
//		   String SENT = "sms_sent";
//		   String DELIVERED = "sms_delivered";
		   Intent sendIntent=new Intent(SENT_SMS_ACTION);
		   Intent deliverIntent=new Intent(DELIVERED_SMS_ACTION);
		   sendIntent.putExtra("number", number); 
		   sendIntent.putExtra("name", name);
		   deliverIntent.putExtra("number", number); 
		   deliverIntent.putExtra("name", name);
		   
		   PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sendIntent, 0);
		   PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
		   registerReceiver(new BroadcastReceiver(){
		      @Override
		      public void onReceive(Context context, Intent intent) {
		        switch(getResultCode())
		        {
		           case Activity.RESULT_OK:
		             Log.i("====>", "Activity.RESULT_OK");
		             String phoneNumber=intent.getStringExtra("number");
		             String name=intent.getStringExtra("name");
		             CommonUtil.showToast(context, "短信给："+name+" ["+phoneNumber+"] 已发送！");
		             break;
		           case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
		             Log.i("====>", "RESULT_ERROR_GENERIC_FAILURE");
		             break;
		           case SmsManager.RESULT_ERROR_NO_SERVICE:
		             Log.i("====>", "RESULT_ERROR_NO_SERVICE");
		             break;
		           case SmsManager.RESULT_ERROR_NULL_PDU:
		             Log.i("====>", "RESULT_ERROR_NULL_PDU");
		             break;
		           case SmsManager.RESULT_ERROR_RADIO_OFF:
		             Log.i("====>", "RESULT_ERROR_RADIO_OFF");
		             break;
		             default:
		              name=intent.getStringExtra("name");
		             CommonUtil.showToast(context, "发送到"+name+"失败");
		             break;
		        }
		     }
		   }, new IntentFilter(SENT_SMS_ACTION));
		   registerReceiver(new BroadcastReceiver(){
		     @Override
		     public void onReceive(Context context, Intent intent){
		       switch(getResultCode())
		       {
		         case Activity.RESULT_OK:
		           Log.i("====>", "RESULT_OK");
		             String phoneNumber=intent.getStringExtra("number");
		             String name=intent.getStringExtra("name");
		             CommonUtil.showToast(context, "短信给："+name+" ["+phoneNumber+"] 已送达！");
		           break;
		         case Activity.RESULT_CANCELED:
		           Log.i("=====>", "RESULT_CANCELED");
		            phoneNumber=intent.getStringExtra("number");
		            name=intent.getStringExtra("name");
		           CommonUtil.showToast(context, name+"接收短信失败！");
		           break;
		       }
		     }
		   }, new IntentFilter(DELIVERED_SMS_ACTION));
		   SmsManager smsm = SmsManager.getDefault();
		   List<String> divideContents = smsm.divideMessage(message);
	        for (String text : divideContents) {
	        	Log.i("ASMS", text);
	        	smsm.sendTextMessage(number, null, text, sentPI, deliveredPI);
	        	saveSMS(number, text);//保存短信数据
	        }
		  // smsm.sendTextMessage(number, null, message, sentPI, deliveredPI);
		  // saveSMS(number, message);//保存短信数据
		}
	
	
	public void saveSMS(String number,String text){
        /**将发送的短信插入数据库**/
        ContentValues values = new ContentValues();
        //发送时间
        values.put("date", System.currentTimeMillis());
        //阅读状态                0 表示未读 1表示已读
        values.put("read", 1);
        //1为收 2为发             1表示接收 2 表示发出
        values.put("type", 2);
        //送达号码
        values.put("address", number);
        //送达内容
        values.put("body", text);
        //插入短信库

        getContentResolver().insert(Uri.parse("content://sms"),values);

    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
