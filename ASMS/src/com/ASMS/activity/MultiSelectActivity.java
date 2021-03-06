package com.ASMS.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ASMS.activity.MultiSelectActivity.SimpleAdapter.ViewModel;
import com.ASMS.app.AppApplication;
import com.ASMS.entity.Contacts;
import com.ASMS.util.CommonUtil;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.ThemeSingleton;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.github.clans.fab.FloatingActionButton;

import android.app.Activity;
import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.support.v7.internal.widget.ViewUtils;
import android.provider.ContactsContract.Data;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

/**
  * @author Administrator
  * @功能:多选联系人界面
  */
public class MultiSelectActivity extends Activity {

	@Bind(R.id.tv_back) TextView tv_back;
	@Bind(R.id.tv_title) TextView tv_title;
	@Bind(R.id.tv_right) TextView  tv_right;
	@Bind(R.id.lv_multiselect) ListView lv_multiselect;
	@Bind(R.id.fab) FloatingActionButton fb_scan;
	Context ct;
	SimpleAdapter adapter;
	List<Contacts> mContacts;
	private int checkNum; // 记录选中的条目数量
	Handler mhandle=new Handler(){
		public void handleMessage(android.os.Message msg) {
		  switch (msg.what) {
		   case 1:
			    mContacts=getContacts();
				adapter=new SimpleAdapter(ct, mContacts);
				lv_multiselect.setAdapter(adapter);
				lv_multiselect.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			break;

		default:
			break;
		}	
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiselect);
		ButterKnife.bind(this);
		AppApplication.getInstance().addActivity(this);
		ct=this;
		initView();
	}
	
	private void initView() {
         tv_right.setText("确定");
         tv_right.setVisibility(View.VISIBLE);
         tv_title.setText("请选择项目");
         
         fb_scan.hide(false);
	     new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	            	fb_scan.show(true);
	            	fb_scan.setShowAnimation(AnimationUtils.loadAnimation(ct, R.anim.show_from_bottom));
	            	fb_scan.setHideAnimation(AnimationUtils.loadAnimation(ct, R.anim.hide_to_bottom));
	            }
	     }, 800);
	}
	long groupId=0;
	private void initData() {
		Intent intent=getIntent();
	  
		if (intent!=null) {
			groupId=intent.getLongExtra("groupId", 0);
		}
		if (groupId!=0) {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mContacts=getAllContactsByGroupId(groupId);
					adapter=new SimpleAdapter(ct, mContacts);
					lv_multiselect.setAdapter(adapter);
				}
			}, 1000);
		}else{
			Message message=new Message();
			message.what=1;
			mhandle.sendMessageDelayed(message, 800);
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}
	
	@OnClick(R.id.fab) void bt_allSelect(){
		for (int i = 0; i < mContacts.size(); i++) {
			if (mContacts.get(i).ischecked==true) {
				mContacts.get(i).setIschecked(false);
				checkNum--;
			}else{
				if (mContacts.get(i).getPhones().size()==0) {
					CommonUtil.showToast(ct, mContacts.get(i).getName()+"：手机号码为空,不能选择！");
				}else{
					mContacts.get(i).setIschecked(true);
					checkNum++;
				}
			}
		}
		 CommonUtil.insertSpanForTextView(tv_title, "已选择"+checkNum+"位联系人", String.valueOf(checkNum));
		if (adapter!=null) {
			adapter.notifyDataSetChanged();
		}
	}
	
	
	@OnClick(R.id.tv_right) void bt_commit(){
		if (groupId!=0) {
			Intent intent=new Intent(this,MainActivity.class);
			intent.putParcelableArrayListExtra("Contacts", (ArrayList<? extends Parcelable>) mContacts);
			CommonUtil.startActivity(this, intent);
			return;
		}
		CommonUtil.showToast(ct, "选中："+checkNum+"");
		Intent intent=new Intent(this,MainActivity.class);
		intent.putParcelableArrayListExtra("Contacts", (ArrayList<? extends Parcelable>) mContacts);
		CommonUtil.setResult(this,intent,RESULT_FIRST_USER);
	}
	
	@OnClick(R.id.tv_back) void bt_back(){
		if (groupId!=0) {
			Intent intent=new Intent(this,MainActivity.class);
			intent.putParcelableArrayListExtra("Contacts", (ArrayList<? extends Parcelable>) mContacts);
			CommonUtil.startActivity(this, intent);
			return;
		}
		CommonUtil.showToast(ct, "选中："+checkNum+"");
		Intent intent=new Intent();
		intent.putParcelableArrayListExtra("Contacts", (ArrayList<? extends Parcelable>) mContacts);
		CommonUtil.setResult(this,intent,RESULT_FIRST_USER);
	}
	
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		Intent intent=new Intent(this,MainActivity.class);
		intent.putParcelableArrayListExtra("Contacts", (ArrayList<? extends Parcelable>) mContacts);
		CommonUtil.setResult(this,intent,RESULT_FIRST_USER);
	}
	
	@OnItemClick(R.id.lv_multiselect) void item_Multi_onClick(
			AdapterView<?> parent, View view, final int position){
		final ViewModel model=(ViewModel) view.getTag();
	    String phone =model.phone.getText().toString();
		List<String> phones=mContacts.get(position).getPhones(); 
		 if (model.selected.isChecked() == true) {
	            mContacts.get(position).setIschecked(false);
	            model.selected.setChecked(false);
	            checkNum--;
	            CommonUtil.insertSpanForTextView(tv_title, "已选择"+checkNum+"位联系人", String.valueOf(checkNum));
	            return;
	        }
		 if (phones.size()==0) {
	         CommonUtil.showToast(ct, "没有手机号码!无法选择该联系人发送短信！");
		 }
		 if (TextUtils.isEmpty(mContacts.get(position).getName())) {
	         CommonUtil.showToast(ct, "联系人名字为空！");
		 }
		if (phones.size()==1) {
		        	if(model.selected.isChecked() == false) {
		           // adapter.getIsSelected().put(position, true);
		            mContacts.get(position).setIschecked(true);
		            model.selected.setChecked(true);
		            checkNum++;
		        }
			 mContacts.get(position).setPhone(phone);
			 CommonUtil.insertSpanForTextView(tv_title, "已选择"+checkNum+"位联系人", String.valueOf(checkNum));
			return;
		}
		if (phones.size()>1) {
	     final String[] phone_items = (String[])phones.toArray(new String[phones.size()]);
	     MaterialDialog dialog=new MaterialDialog.Builder(ct) 
				.title("选择号码")
				.items(phone_items)
				.itemsCallback(new MaterialDialog.ListCallback() {
					
					@Override
					public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						 String phone_selected = phone_items[which];
						 model.phone.setText(phone_selected);
//						if (model.selected.isChecked() == true) {
//							 mContacts.get(position).setPhone(phone_selected);
//							 model.selected.setChecked(false);
//							 checkNum--;
//						 }else
							 if(model.selected.isChecked() == false) {
							 mContacts.get(position).setPhone(phone_selected);
							 model.selected.setChecked(true);
							 mContacts.get(position).setIschecked(true);
							 checkNum++;
						 }
					 CommonUtil.insertSpanForTextView(tv_title, "已选择"+checkNum+"位联系人", String.valueOf(checkNum));
					 dialog.dismiss();
					}
				}).build();
		dialog.show();	
		}
		 
	}
	
	@OnItemLongClick(R.id.lv_multiselect) boolean item_onLongClick(AdapterView<?> parent, View view, final int position, long id){
		Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{0,100}, -1);
		final ViewModel model=(ViewModel) view.getTag();
		MaterialDialog dialog=new MaterialDialog.Builder(ct) 
				.items(R.array.menus_multiSelect)
				.itemsCallback(new MaterialDialog.ListCallback() {
					
					@Override
					public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						switch (which) {
						case 0:
							 Log.i("ContactId","mId:"+mContacts.get(position).getId()+"");
							 showDialogsInput(model,model.name.getText().toString(),mContacts.get(position));
							break;
						case 1:
							List<String> phones=	mContacts.get(position).getPhones(); 
							if (phones.size()==1) {
								return;
							}
						    final String[] phone_items = (String[])phones.toArray(new String[phones.size()]);
							 dialog=new MaterialDialog.Builder(ct) 
									.title("选择号码")
									.items(phone_items)
									.itemsCallback(new MaterialDialog.ListCallback() {
										
										@Override
										public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
										 String phone_selected = phone_items[which];
										 model.phone.setText(phone_selected);
										 mContacts.get(position).setPhone(phone_selected);
										 dialog.dismiss();
										}
									}).build();
							dialog.show();	
							break;
						case 2:
							CommonUtil.startActivity(ct, GroupContactsActivity.class);
							break;
						default:
							break;
						}
					}
				}).build();
		dialog.show();
		
		return false;
	}
	

	
    //查询所有联系人  
	@Deprecated
	public List<Contacts> getContacts() {  
		List<Contacts> contacts=new ArrayList<Contacts>();
		ContentResolver resolver = getContentResolver();  
		Uri uri = Uri.parse("content://com.android.contacts/contacts");  
		Cursor idCursor = resolver.query(
				ContactsContract.Contacts.CONTENT_URI,
				new String[]{
						android.provider.ContactsContract.Contacts._ID
						},
				null, null, null);  
		while (idCursor.moveToNext()) { //空指针
			Contacts contact=new Contacts();
		   //获取到raw_contacts表中的id  
		   int id = idCursor.getInt(0);   
		   String idstr=idCursor.getString(idCursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));  
		   contact.setId(id);
		   //根据获取到的ID查询data表中的数据  
		   uri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");  
		   Cursor dataCursor = resolver.query(
				   android.provider.ContactsContract.Data.CONTENT_URI, 
				   new String[] {
						   android.provider.ContactsContract.Data.DATA1, 
						   android.provider.ContactsContract.Data.MIMETYPE,
						   android.provider.ContactsContract.Data.RAW_CONTACT_ID}, 
				   android.provider.ContactsContract.Data.CONTACT_ID+"="+idstr, 
				   null, null);  
		   StringBuilder sb = new StringBuilder();  
		   sb.append("id=" + id);  
		   //查询联系人表中的  
		  // getContactsPhones(idCursor, contact, idCursor.getString(id));
		   while (dataCursor.moveToNext()) { 
			   Log.i("rawid","-------------\n");
		       String data = dataCursor.getString(dataCursor.getColumnIndex(android.provider.ContactsContract.Data.DATA1));  
		       String mimetype = dataCursor.getString(  
		    		   dataCursor.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE)); 
		       int rawid=dataCursor.getInt(2);
		       contact.setRawid(rawid);
		       Log.i("rawid","某联系人下："+rawid+"");
		       if (mimetype.contains("/name"))  //vnd.android.cursor.item/name
		          // sb.append(", name=" + data);  
		            contact.setName(data);
		       else if (mimetype.contains("/phone_v2"))  
		           //sb.append(", phone=" + data);  nickname
		    	   contact.getPhones().add(data);
		       else if(mimetype.contains("/nickname"))
		       {
		    	   contact.setNickname(data);
		    	   Log.i("rawid", data+"");
		    	   Log.i("rawid", rawid+"");
		       }
		   }  
		   contacts.add(contact);
		   dataCursor.close();
		}  
		 idCursor.close();
		return contacts;
	}
	
	
	
	/**
	 * 获取某个分组下的 所有联系人信息
	 * 思路：通过组的id 去查询 RAW_CONTACT c_ID, 通过RAW_CONTACT_ID去查询联系人
		要查询得到 data表的Data.RAW_CONTACT_ID字段
	 * @param groupId
	 * @return
	 */
	public List<Contacts> getAllContactsByGroupId(long groupId) {
		String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID, };
		String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
				+ "=?"
				+ " and "
				+ ContactsContract.Data.MIMETYPE
				+ "="
				+ "'"
				+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
				+ "'";
		// 通过分组的id 查询得到RAW_CONTACT_ID
		Cursor cursor =getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
				RAW_CONTACTS_WHERE, new String[] { groupId + "" }, "data1 asc");
		List<Contacts> contactList = new ArrayList<Contacts>();
		while (cursor.moveToNext()) {
			// RAW_CONTACT_ID
			int col = cursor.getColumnIndex("raw_contact_id");
			int raw_contact_id = cursor.getInt(col);
			// Log.i("getAllContactsByGroupId", "raw_contact_id:" +
			// raw_contact_id);
			Contacts ce = new Contacts();
			ce.setRawid(raw_contact_id);

			Uri dataUri = Uri.parse("content://com.android.contacts/data");
			Cursor dataCursor = getContentResolver().query(dataUri,
					null, "raw_contact_id=?",
					new String[] { raw_contact_id + "" }, null);

			while (dataCursor.moveToNext()) {
				String data1 = dataCursor.getString(dataCursor
						.getColumnIndex("data1"));
				String mime = dataCursor.getString(dataCursor
						.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
					ce.getPhones().add(data1);
				} else if ("vnd.android.cursor.item/name".equals(mime)) {
					ce.setName(data1);
				}else if("vnd.android.cursor.item/nickname".equals(mime)){
			    	   ce.setNickname(data1);
			    }
			}
			dataCursor.close();
			contactList.add(ce);
			ce = null;
		}
		cursor.close();
		return contactList;
	}
	
	   /*
     * 自定义显示Contacts提供的联系人的方法
     */
    public List<Contacts> printContacts() {
    	List<Contacts> contacts=new ArrayList<Contacts>();
        ContentResolver contentResolver = getContentResolver();
        /*Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);*/
        //这段代码和上面代码是等价的，使用两种方式获得联系人的Uri
        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/contacts"),null,null,null,null);
        try{
        if (cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameColumn = cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            do {
            	Contacts contact=new Contacts();
            	int id=cursor.getInt(0);
                String contactId = cursor.getString(idColumn); // 获得联系人的ID
                String displayName = cursor.getString(displayNameColumn);// 获得联系人姓名
                contact.setName(displayName);
                getContactsPhones(cursor, contact, contactId);// 查看联系人有多少个号码，如果没有号码，返回0
                
                
//					Uri uri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");  
//					Cursor dataCursor = contentResolver.query(uri, new String[] { "data1", "mimetype" }, null, null, null);  
//					try {
//					while (dataCursor.moveToNext()) { 
//					   String data = dataCursor.getString(0);  
//					   String type = dataCursor.getString(1);  
//					    if("vnd.android.cursor.item/nickname".equals(type))
//						   contact.setNickname(data);
//					}
//				    } finally{
//					   dataCursor.close();
//				   }
					
                contacts.add(contact);
            } while (cursor.moveToNext());
           
        }
        }finally {
			cursor.close();
		}
       return contacts;    
    }

	private void getContactsPhones(Cursor cursor, Contacts contact, String contactId) {
		int phoneCount = cursor.getInt(cursor
		                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		if (phoneCount > 0) {
		    // 获得联系人的电话号码列表
		    Cursor phoneCursor = getContentResolver().query(
		            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		            null,
		            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
		                    + "=" + contactId, null, null);
		    if(phoneCursor.moveToFirst())
		    {
		        do
		        {
		            //遍历所有的联系人下面所有的电话号码
		            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		            //使用Toast技术显示获得的号码
		            // Toast.makeText(ContentProviderDemo.this, "联系人电话："+phoneNumber,Toast.LENGTH_LONG).show();
		           contact.getPhones().add(phoneNumber);  
		        }while(phoneCursor.moveToNext());
		    }
		}
	}



	
	
	public class SimpleAdapter extends BaseAdapter{
		private List<Contacts> data;
		private LayoutInflater inflater;
		// 用来控制CheckBox的选中状况
	    private HashMap<Integer,Boolean> isSelected;
		public SimpleAdapter(Context ct,List<Contacts> data) {
			this.inflater=LayoutInflater.from(ct);
			this.data=data;
			isSelected = new HashMap<Integer, Boolean>();
		    initSelectState(); // 初始化数据
		}
		 // 初始化isSelected的数据
	    public void initSelectState(){
	        for(int i=0; i<data.size();i++) {
	            getIsSelected().put(i,false);
	        }
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
		
		public HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
			this.isSelected = isSelected;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewModel model=null;
			if (view==null) {
				view=inflater.inflate(R.layout.item_friends_multiselect, parent,false);
				model=new ViewModel();
				model.name=(TextView) view.findViewById(R.id.tv_name);
				model.nickname=(TextView) view.findViewById(R.id.tv_nickname);
				model.phone=(TextView) view.findViewById(R.id.tv_phone);
				model.phoneAddress=(TextView) view.findViewById(R.id.tv_phoneaddress);
				model.selected=(CheckBox) view.findViewById(R.id.cb_right);
				model.phoneCount=(TextView) view.findViewById(R.id.tv_phonecount);
			    view.setTag(model);
			}else{
				model=(ViewModel) view.getTag();
			}
			
			model.name.setText(data.get(position).getName());
			model.nickname.setText(data.get(position).getNickname());
			model.phoneAddress.setText(data.get(position).getPhoneAddress());
			model.selected.setChecked(data.get(position).ischecked);
			
			if (!data.get(position).getPhones().isEmpty()) {
				model.phone.setText(data.get(position).getPhones().get(0));
				CommonUtil.insertSpanForTextView(model.phoneCount, "共"+data.get(position).getPhones().size()+"个号码", 
						String.valueOf(data.get(position).getPhones().size()));
			}else{
				model.phone.setText(data.get(position).getPhone());
				CommonUtil.insertSpanForTextView(model.phoneCount, "共"+data.get(position).getPhones().size()+"个号码", 
						String.valueOf(data.get(position).getPhones().size()));
			}
			//model.selected.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
			return view;
		}
		
		class ViewModel{
			TextView name;
			TextView nickname;
			TextView phone;
			TextView phoneCount;
			TextView phoneAddress;
			CheckBox selected;
		}
		
	}
	final String COLUMN_CONTACT_ID =   
            ContactsContract.Data.CONTACT_ID;  
	final String COLUMN_MIMETYPE =   
            ContactsContract.Data.MIMETYPE; 
	
	
	
	
	/**
	  * @author Administrator
	  * @功能:更新昵称
	  */
	public void updateContactNickName(String ContactId,String newNickName){
		 ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>();
		 ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
				 .withSelection(COLUMN_CONTACT_ID + "=? AND " + COLUMN_MIMETYPE + "=?",  
                         new String[]{ContactId, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE})  
                 .withValue(COLUMN_MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)  
                 .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, newNickName)  
                 .build());  
		 try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}      
	}
	
	public void insertContactNickName(String contactId,String newNickName){
		 ArrayList<ContentProviderOperation> ops =  new ArrayList<ContentProviderOperation>();
		 ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		          .withValue(Data.RAW_CONTACT_ID, contactId)
		          .withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE)
		          .withValue(Nickname.NAME, newNickName)
		          .withValue(Nickname.TYPE, Nickname.TYPE_CUSTOM)
		          .withValue(Nickname.LABEL, "AMASIN")
		          .build());

		 try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}      
	}
	

	private static EditText content;
	private static View positiveAction;
	boolean isUpdate=false;
	/**
	  * @author Administrator
	  * @功能:展示输入模板的dialog
	  */
	public String showDialogsInput(final ViewModel model,final String name,final Contacts contacts){
		MaterialDialog dialog = new MaterialDialog.Builder(ct).title("输入昵称")
				.customView(R.layout.dialog_templet_input, true)
				.positiveText("保存").negativeText(android.R.string.cancel)
				.neutralColor(ct.getResources().getColor(R.color.red))
				.autoDismiss(false)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						CommonUtil.showToast(ct, content.getText().toString());
						//getContactID(name);
						if (!TextUtils.isEmpty(model.nickname.getText().toString())) {
							updateContactNickName(String.valueOf(contacts.getId()), content.getText().toString());
						}else{
							insertContactNickName(String.valueOf(contacts.getRawid()), content.getText().toString());
						}
						model.nickname.setText(content.getText().toString());
						contacts.setNickname(content.getText().toString());
						dialog.dismiss();
					}
					
					@Override
					public void onNegative(MaterialDialog dialog) {
						
						dialog.dismiss();
					}
				}).build();
		
		content=(EditText) dialog.getCustomView().findViewById(R.id.et_content);
		positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
		
		content.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(content.getText().toString())) {
					positiveAction.setEnabled(s.toString().trim().length() > 0);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		int widgetColor = ThemeSingleton.get().widgetColor;
		MDTintHelper.setTint(content, widgetColor == 0 ? ct.getResources()
				.getColor(R.color.material_teal_500) : widgetColor);
		
        if (TextUtils.isEmpty(content.getText().toString())) {
        	positiveAction.setEnabled(false); // disabled by default
		}
        dialog.show();
        
        return content.getText().toString();
	}
	
	
	
	
	
	 /** 
     *  
     * @param contact The contact who you get the id from. The name of 
     * the contact should be set. 
     * @return 0 if contact not exist in contacts list. Otherwise return 
     * the id of the contact. 
     */  
    public String getContactID(String name) {  
        String id = "0";  
        Cursor cursor = getContentResolver().query(  
                android.provider.ContactsContract.Contacts.CONTENT_URI,    
                new String[]{android.provider.ContactsContract.Contacts._ID},  
                android.provider.ContactsContract.Contacts.DISPLAY_NAME +   
                "='" + name + "'", null, null);  
        Log.i("ContactId","name:"+name);
        if(cursor.moveToNext()) {  
            id = cursor.getString(cursor.getColumnIndex(  
                    android.provider.ContactsContract.Contacts._ID));  
            Log.i("ContactId","id:"+id);
        }  
        return id;  
    }  
	
	
}
