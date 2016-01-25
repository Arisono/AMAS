package com.ASMS.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

public class CommonUtil {
	
	public static void startActivity(Context ct,Class<?> activity){
		ct.startActivity(new Intent(ct, activity));
	}
	
	public static void startActivityForResult(Activity ct,Class<?> activity,int request){
		ct.startActivityForResult(new Intent(ct, activity), request);
	}
	
	public static void setResult(Activity ct,Intent intent,int result){
		ct.setResult(result, intent);
		ct.finish();
	}
	
	
	public static void showToast(Context ct,String msg){
		Toast.makeText(ct, msg, Toast.LENGTH_LONG).show();
	}
	
	public static void insertSpanForTextView(TextView view, String input,String matcher) {
		SpannableStringBuilder style=new SpannableStringBuilder(input); 
	    Pattern highlight = Pattern.compile(matcher);
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
