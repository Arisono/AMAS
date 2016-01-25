package com.ASMS.entity;

import java.util.ArrayList;
import java.util.List;

/**
  * @author Administrator
  * @功能:联系人
  */
public class Contacts {
	public int id;
    public String name;//姓名
    public String nickname;//昵称
    public String phone;
    public List<String> phones=new ArrayList<>();//多个手机人号码
    public String phoneAddress;
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
	
	
}
