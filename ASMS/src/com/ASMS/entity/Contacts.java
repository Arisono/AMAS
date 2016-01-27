package com.ASMS.entity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
  * @author Administrator
  * @����:��ϵ��
  */
public class Contacts implements Parcelable {
	public int id;
	public int rawid;
    public String name;//����
    public String nickname;//�ǳ�
    public String phone;
    public List<String> phones=new ArrayList<>();//����ֻ��˺���
    public String phoneAddress;
    public boolean ischecked;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoneAddress() {
		return phoneAddress;
	}
	public void setPhoneAddress(String phoneAddress) {
		this.phoneAddress = phoneAddress;
	}
	public List<String> getPhones() {
		return phones;
	}
	public void setPhones(List<String> phones) {
		this.phones = phones;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRawid() {
		return rawid;
	}
	public void setRawid(int rawid) {
		this.rawid = rawid;
	}
	public boolean isIschecked() {
		return ischecked;
	}
	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	
	public Contacts(){
		
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub  
        /*��Person�ĳ�Աд��Parcel�� 
         * ע��Parcel�е������ǰ�˳��д��Ͷ�ȡ�ģ����ȱ�д��ľͻ��ȱ���ȡ���� 
         */  
		 dest.writeInt(id);  
		 dest.writeInt(rawid);  
         dest.writeString(name);  
         dest.writeString(phone);  
         dest.writeString(nickname);
         //dest.writeStringList(phones); //���� 
         dest.writeString(phoneAddress);  
         dest.writeByte((byte)(ischecked?1:0));  
	}
	
	   //�þ�̬���Ǳ���Ҫ�еģ��������ֱ�����CREATOR����������  
    public static final Parcelable.Creator<Contacts> CREATOR =  
        new Parcelable.Creator<Contacts>()  {

			@Override
			public Contacts createFromParcel(Parcel source) {
		        //��Parcel��ȡͨ��writeToParcel����д���Person����س�Ա��Ϣ  
				Contacts contacts=new Contacts();
				contacts.id=source.readInt();
				contacts.rawid=source.readInt();
				contacts.name = source.readString();   
				contacts.phone = source.readString();  
				contacts.nickname=source.readString();
				//contacts.phones=source.readStringList();
				contacts.phoneAddress=source.readString();
				contacts.ischecked =source.readByte()!=0;
	            return contacts;
			}

			@Override
			public Contacts[] newArray(int size) {
				return new Contacts[size];
			}
    };
	
}
