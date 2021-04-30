package com.example.demo.domain;

import Model.BussinessTimes;

public class StoreVO {
	int id;
	String name;
	String description;
	int level;
	String address;
	String phone;
	BussinessTimes[] bussinesstime;
	public StoreVO() {}
	
	public int getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public String getDescription() {
		return this.description;
	}
	public int getLevel() {
		return this.level;
	}
	public String getAddress() {
		return this.address;
	}
	public String getPhone() {
		return this.phone;
	}
	public BussinessTimes[] getBussinesstime() {
		return this.bussinesstime;
	}
	public void setBussinessTime() {
		this.bussinesstime=new BussinessTimes[5];
		
		
	}
	public void setId(int id) {
		this.id=id;
	}
	public void setName(String name) {
		this.name=name;
	}
	public void setDescription(String description) {
		this.description=description;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setBussinessTime(BussinessTimes[] bussiness) {
		this.bussinesstime=bussiness;
	}
}
