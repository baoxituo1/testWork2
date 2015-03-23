package com.trade.bluehole.trad.util;

import java.util.List;

public class Result <T,L>{

	public boolean success;
	public List<T> list;
	public L obj;
	public String message;
	public List<T>  aaData;
	public int iTotalRecords;
	public int iTotalDisplayRecords;
	public int pageSize=12;
	public int page=1;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	/*public Object[][] getAaData() {
		return aaData;
	}
	public void setAaData(Object[][] aaData) {
		this.aaData = aaData;
	}*/
	public List<T> getAaData() {
		return aaData;
	}
	public void setAaData(List<T> aaData) {
		this.aaData = aaData;
	}
	public int getiTotalRecords() {
		return iTotalRecords;
	}
	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}
	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}
	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}
	public L getObj() {
		return obj;
	}
	public void setObj(L obj) {
		this.obj = obj;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
}
