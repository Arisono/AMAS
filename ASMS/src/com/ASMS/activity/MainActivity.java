package com.ASMS.activity;

import com.ASMS.util.CommonUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	
	@Bind(R.id.et_bottom_template) EditText ed_template;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		
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
		
	}
	
	@OnClick(R.id.et_bottom_template) void bt_content(){
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case request_code_template:
			if (data!=null) {
				String template=data.getStringExtra("template");
				Log.i("request_code", template);
				CommonUtil.showToast(this, template);
				CommonUtil.insertSpanForTextView(ed_template, template, "@昵称");
			}
			break;
		case request_code_multiselect:
			
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
