package com.trade.bluehole.trad.entity.pro;

public class ProductCoverRelVO implements java.io.Serializable{

	private String shopCode;
	private String proCode;
	private String coverCode;
	private String coverName;

	public ProductCoverRelVO(String shopCode, String proCode, String coverCode, String coverName) {
		super();
		this.shopCode = shopCode;
		this.proCode = proCode;
		this.coverCode = coverCode;
		this.coverName = coverName;
	}

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

	public String getCoverCode() {
		return coverCode;
	}

	public void setCoverCode(String coverCode) {
		this.coverCode = coverCode;
	}

	public String getCoverName() {
		return coverName;
	}

	public void setCoverName(String coverName) {
		this.coverName = coverName;
	}
}
