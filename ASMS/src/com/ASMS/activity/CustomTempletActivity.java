package com.ASMS.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ASMS.activity.CustomTempletActivity.SimpleAdapter.ViewModel;
import com.ASMS.util.CommonUtil;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.ThemeSingleton;
import com.afollestad.materialdialogs.internal.MDTintHelper;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
/**
  * @author Administrator
  * @����:ϰ������༭����
  */
public class CustomTempletActivity extends Activity {
	
	@Bind(R.id.lv_template)
	ListView  lv_template;
	@Bind(R.id.iv_add)
	ImageView iv_add;
	@Bind(R.id.tv_back)
	TextView tv_back;
	@Bind(R.id.tv_title)
	TextView tv_title; 
	
	Context ct;
	SimpleAdapter adapter;
	List<String> templates=new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customtemplet);
		ButterKnife.bind(this);
		ct=this;
		tv_title.setText("��ӳ�����");
		loadData();
		initData();
		

	
	}
	
	
	private void initData() {
		if (adapter==null) {
			adapter=new SimpleAdapter(ct, templates);
			lv_template.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}
	
	private void loadData() {
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("dsafjds dsafhdsajfdsajfdsa��ݿռ�Ŀռ�ѵ����Ŵ󾵷����Ͽռ仮�ִ���ؼ���jdsajfjsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
       templates.add("adfdsafdsafdsafadsfdsafsadfdsafdsa");
	}
	
	@OnClick(R.id.iv_add) void iv_add() {
//		Toast.makeText(this, "����¼���", Toast.LENGTH_LONG).show();
		showDialogTemplteInput(null,0);
	}
	
	@OnClick(R.id.tv_back) void iv_back(){
		onBackPressed();
	}

	@OnItemClick(R.id.lv_template) void item_onClick(AdapterView<?> parent, View view, final int position){
		ViewModel model=(ViewModel) view.getTag();
	    Intent intent=new Intent();
	    intent.putExtra("template", model.template_text.getText().toString());
		CommonUtil.setResult(this,intent,RESULT_FIRST_USER);
	}
	
	@OnItemLongClick(R.id.lv_template) boolean item_onLongClick(AdapterView<?> parent, View view, final int position, long id){
		Vibrator vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{0,100}, -1);
		MaterialDialog dialog=new MaterialDialog.Builder(ct) 
				.items(R.array.menus)
				.itemsCallback(new MaterialDialog.ListCallback() {
					
					@Override
					public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
						switch (which) {
						case 0:
							
							break;
						case 1:
							templates.remove(position);
							adapter.notifyDataSetChanged();
							break;
						case 2:
					     String  content=templates.get(position);
					     showDialogTemplteInput(content,position);		
							break;
						default:
							break;
						}
					}
				}).build();
		dialog.show();
		return false;
	}
	
   
	public class SimpleAdapter extends BaseAdapter{
		private List<String> data;
		
		private LayoutInflater inflater;
		
		public SimpleAdapter(Context ct,List<String> data) {
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
				view=inflater.inflate(R.layout.item_template_simple, parent,false);
				model=new ViewModel();
				model.template_text=(TextView) view.findViewById(R.id.item_template_text);
			    view.setTag(model);
			}else{
				model=(ViewModel) view.getTag();
			}
			
			model.template_text.setText(data.get(position));
			insertSpanForEditView(model.template_text, model.template_text.getText().toString());
			return view;
		}
		
		class ViewModel{
			TextView template_text;
		}
		
	}

	
	
	private static EditText content;
	private static View positiveAction;
	private static View neutralAction;
	boolean isUpdate=false;
	/**
	  * @author Administrator
	  * @����:չʾ����ģ���dialog
	  */
	public void showDialogTemplteInput(String updateStr,final int position){
		MaterialDialog dialog = new MaterialDialog.Builder(ct).title("�½�")
				.customView(R.layout.dialog_templet_input, true)
				.positiveText("����").negativeText(android.R.string.cancel)
				.neutralText("�����ǳ�")
				.neutralColor(ct.getResources().getColor(R.color.red))
				.autoDismiss(false)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						if (isUpdate) {
							templates.remove(position);
							templates.add(position,content.getText().toString());
							adapter.notifyDataSetChanged();
						}else{
						templates.add(content.getText().toString());
						adapter.notifyDataSetChanged();
						}
						dialog.dismiss();
					}
					
					@Override
					public void onNegative(MaterialDialog dialog) {
						
						dialog.dismiss();
					}
					
					@Override
					public void onNeutral(MaterialDialog dialog) {
						//�����ǳ�
						int index=content.getSelectionStart();
						String text="@�ǳ�";
						Editable edit = content.getEditableText();//��ȡEditText������
						if (index < 0 || index >= edit.length() ){
						       edit.append(text);
						}else{
						      edit.insert(index,text);//�������λ�ò�������
						}
						String input=content.getText().toString();
						Log.i("input", input);
						int fend=index+text.length();
					    insertSpanForEditView(content,input);
					    content.setSelection(fend);
					}

					
					
				}).build();
		
		content=(EditText) dialog.getCustomView().findViewById(R.id.et_content);
		if (!TextUtils.isEmpty(updateStr)) {
			content.setText(updateStr);
			isUpdate=true;
		}else{
			isUpdate=false;
		}
		
		insertSpanForEditView(content,content.getText().toString());
		
		positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
		neutralAction=dialog.getActionButton(DialogAction.NEUTRAL);
		
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
					neutralAction.setEnabled(s.toString().trim().length() > 0);
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
        	neutralAction.setEnabled(false);
		}
        dialog.show();
	}
	
	
	private void insertSpanForEditView(TextView view, String input) {
		SpannableStringBuilder style=new SpannableStringBuilder(input); 
	    Pattern highlight = Pattern.compile("@�ǳ�");
	    Matcher m = highlight.matcher(style.toString());
	    while (m.find()) {
            style.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), m.start(), m.end(), 
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            style.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), 
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            style.setSpan(new StrikethroughSpan(), m.start(), m.end(), 
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
		view.setText(style);
	}
}
