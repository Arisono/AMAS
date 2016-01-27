package com.ASMS.entity;

/**
  * @author :Administrator   2016年1月28日 上午11:13:48
  * @注释:联系人组
  */
public class GroupContacts {
    long id;
    String name;
    String contactsNum;//下属联系人数量
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContactsNum() {
		return contactsNum;
	}
	public void setContactsNum(String contactsNum) {
		this.contactsNum = contactsNum;
	}
	   
   
}
