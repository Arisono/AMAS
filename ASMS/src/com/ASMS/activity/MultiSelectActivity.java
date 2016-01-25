package com.ASMS.activity;

import java.util.ArrayList;
import java.util.List;

import com.ASMS.activity.MultiSelectActivity.SimpleAdapter.ViewModel;
import com.ASMS.entity.Contacts;
import com.ASMS.util.CommonUtil;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.ThemeSingleton;
import com.afollestad.materialdialogs.internal.MDTintHelper;

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
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
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
	
	Context ct;
	SimpleAdapter adapter;
	List<Contacts> mContacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiselect);
		ButterKnife.bind(this);
		ct=this;
		initView();
		initData();
	}
	
	private void initView() {
         tv_right.setText("确定");
         tv_right.setVisibility(View.VISIBLE);
         tv_title.setText("请选择项目");
	}
	
	private void initData() {
		mContacts=getContacts();
		adapter=new SimpleAdapter(this, mContacts);
		lv_multiselect.setAdapter(adapter);
	}
	
	@OnItemClick(R.id.lv_multiselect) void item_Multi_onClick(
			AdapterView<?> parent, View view, final int position){
		final ViewModel model=(ViewModel) view.getTag();
//		 Toast.makeText(ct, "点击事件！1", Toast.LENGTH_LONG).show();
			List<String> phones=	mContacts.get(position).getPhones(); 
			if (phones.size()==1) {
				return;
			}
		    final String[] phone_items = (String[])phones.toArray(new String[phones.size()]);
			MaterialDialog dialog=new MaterialDialog.Builder(ct) 
					.title("选择号码")
					.items(phone_items)
					.itemsCallback(new MaterialDialog.ListCallback() {
						
						@Override
						public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						 String phone_selected = phone_items[which];
						 model.phone.setText(phone_selected);
						 dialog.dismiss();
						}
					}).build();
			dialog.show();	
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
							 showDialogsInput(model.name.getText().toString(),mContacts.get(position).getId());
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
		Cursor idCursor = resolver.query(uri,new String[]{android.provider.ContactsContract.Contacts._ID}, null, null, null);  
		while (idCursor.moveToNext()) { 
			Contacts contact=new Contacts();
		   //获取到raw_contacts表中的id  
		   int id = idCursor.getInt(0);   
		   contact.setId(id);
		   //根据获取到的ID查询data表中的数据  
		   uri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");  
		   Cursor dataCursor = resolver.query(uri, new String[] { "data1", "mimetype" }, null, null, null);  
		   StringBuilder sb = new StringBuilder();  
		   sb.append("id=" + id);  
		   //查询联系人表中的  
		  
		  // getContactsPhones(idCursor, contact, idCursor.getString(id));
		   
		   while (dataCursor.moveToNext()) { 
			  
		       String data = dataCursor.getString(0);  
		       String type = dataCursor.getString(1);  
		       if ("vnd.android.cursor.item/name".equals(type))  
		          // sb.append(", name=" + data);  
		            contact.setName(data);
		       else if ("vnd.android.cursor.item/phone_v2".equals(type))  
		           //sb.append(", phone=" + data);  
		    	   contact.getPhones().add(data);
		       else if ("vnd.android.cursor.item/email_v2".equals(type))  
		           sb.append(", email=" + data);
		       else if("vnd.android.cursor.item/nickname".equals(type))
		    	   //sb.append(",nickname="+data);
		    	   contact.setNickname(data);
		      
		   }  
		   contacts.add(contact);
		   dataCursor.close();
		}  
		 idCursor.close();
		   
		return contacts;
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
		
		public SimpleAdapter(Context ct,List<Contacts> data) {
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
			if (!data.get(position).getPhones().isEmpty()) {
				model.phoneCount.setText(data.get(position).getPhones().get(0));
				model.phone.setText(data.get(position).getPhones().get(0));
				CommonUtil.insertSpanForTextView(model.phoneCount, "共"+data.get(position).getPhones().size()+"个号码", 
						String.valueOf(data.get(position).getPhones().size()));
			}else{
				model.phone.setText(data.get(position).getPhone());
			}
			
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
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
	public void showDialogsInput(final String name,final int ContactId){
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
						updateContactNickName(new String().valueOf(ContactId), content.getText().toString());
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
