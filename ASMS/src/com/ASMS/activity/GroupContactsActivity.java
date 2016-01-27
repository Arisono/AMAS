package com.ASMS.activity;

import java.util.ArrayList;
import java.util.List;

import com.ASMS.activity.GroupContactsActivity.SimpleAdapter.ViewModel;
import com.ASMS.app.AppApplication;
import com.ASMS.entity.GroupContacts;
import com.ASMS.util.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
/**
  * @author Administrator
  * @功能:联系人分组模式
  */
public class GroupContactsActivity extends Activity {
	
	@Bind(R.id.lv_group)  ListView lv_group;
	@Bind(R.id.tv_title)  TextView tv_title;
 	private List<GroupContacts> groupContacts=new ArrayList<GroupContacts>();
 	private SimpleAdapter adapter;

	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupconcacts);
		AppApplication.getInstance().addActivity(this);
		ButterKnife.bind(this);
		initView();
		initData();
		
	}
	 private void initView() {
        tv_title.setText("分组模式");
	}
	 
	 @OnClick(R.id.tv_back) void iv_back(){
		 onBackPressed();
	 }
	 
	 private void initData() {
        if (adapter==null) {
			adapter=new SimpleAdapter(this, getGroupContactsList());
			lv_group.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}
	 
	 @OnItemClick(R.id.lv_group) void item_onClik(AdapterView<?> parent, View view, final int position){
		 ViewModel model=(ViewModel) view.getTag();
		 long id=model.id;
		 //CommonUtil.showToast(this,id+"");
		 Intent intent=new Intent(this,MultiSelectActivity.class);
		 intent.putExtra("groupId", id);
		 CommonUtil.startActivity(this, intent);
	 }
	 /**
	  * @author Administrator
	  * @功能:读取联系人分组列表
	  *       用户已经删除的不查询出来
	  *       当Groups.DELETED=0的时候， 是 查询没有被删除的联系人分组 
	  */
	 public List<GroupContacts> getGroupContactsList(){
		 groupContacts.clear();
        String[] RAW_PROJECTION = new String[] {ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};  
        String RAW_CONTACTS_WHERE = ContactsContract.Groups.DELETED + " = ? ";  
        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, RAW_PROJECTION,  
                RAW_CONTACTS_WHERE, new String[] { "" + 0 }, null);  
        while (cursor.moveToNext()) { 
        	GroupContacts gContacts=new GroupContacts();
        	long id = cursor.getLong(cursor.getColumnIndex("_id"));  
            String title = cursor.getString(cursor.getColumnIndex("title")); 
            String count = getCountGroup(id  ,this);//联系人分组下面的数量
            gContacts.setName(title);
            gContacts.setId(id);
            gContacts.setContactsNum(count);
            groupContacts.add(gContacts);
        }  
        cursor.close();  
        return groupContacts;
	 }
	 
	  private String getCountGroup(long GroupId ,Context context){
	        String select = ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE +"' and " +ContactsContract.Data.DATA1 + "=" +GroupId;
	        String[] colValue = new String[]{ContactsContract.Data.RAW_CONTACT_ID };
	        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, colValue, select , null , "data1 asc");
	        String count = String.valueOf(cursor.getCount());
	        cursor.close();
	        return count;
	    }
	  
	  
		public class SimpleAdapter extends BaseAdapter{
			private List<GroupContacts> data;
			
			private LayoutInflater inflater;
			
			public SimpleAdapter(Context ct,List<GroupContacts> data) {
				this.inflater=LayoutInflater.from(ct);
				this.data=data;
			}

			@Override
			public int getCount() {
				return data.size();
			}

			@Override
			public Object getItem(int position) {
				return data.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View view, ViewGroup parent) {
				ViewModel model=null;
				if (view==null) {
					view=inflater.inflate(R.layout.item_group_contacts, parent,false);
					model=new ViewModel();
					model.template_name=(TextView) view.findViewById(R.id.tv_name);
					model.template_num=(TextView) view.findViewById(R.id.tv_contacts_num);
				    view.setTag(model);
				}else{
					model=(ViewModel) view.getTag();
				}
				model.template_name.setText(data.get(position).getName());
				//model.template_num.setText(data.get(position).getContactsNum());
				CommonUtil.insertSpanForTextView(model.template_num,
						data.get(position).getContactsNum()+"位", data.get(position).getContactsNum());
				model.id=data.get(position).getId();
				return view;
			}
			
			class ViewModel{
				long id;
				TextView template_name;
				TextView template_num;
			}
			
		}
	 
}
