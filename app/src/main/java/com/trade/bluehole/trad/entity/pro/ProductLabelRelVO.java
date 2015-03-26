package com.trade.bluehole.trad.entity.pro;

public class ProductLabelRelVO implements java.io.Serializable{
	private String shopCode;
	private String proCode;
	private String labelCode;
	private String labelName;

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public String getProCode() {
		return proCode;
	}

	public void setProCode(String proCode) {
		this.proCode = proCode;
	}

	public String getLabelCode() {
		return labelCode;
	}

	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public ProductLabelRelVO(String shopCode, String proCode, String labelCode, String labelName) {
		super();
		this.shopCode = shopCode;
		this.proCode = proCode;
		this.labelCode = labelCode;
		this.labelName = labelName;
	}
	
}
