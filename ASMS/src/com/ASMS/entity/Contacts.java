package com.ASMS.entity;

import java.util.ArrayList;
import java.util.List;

/**
  * @author Administrator
  * @����:��ϵ��
  */
public class Contacts {
	public int id;
    public String name;//����
    public String nickname;//�ǳ�
    public String phone;
    public List<String> phones=new ArrayList<>();//����ֻ��˺���
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
