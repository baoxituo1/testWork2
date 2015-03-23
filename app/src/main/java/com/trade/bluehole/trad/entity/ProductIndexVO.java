package com.trade.bluehole.trad.entity;

public class ProductIndexVO {
	
	public ProductIndexVO(){
		
	}
	
	public ProductIndexVO(String shopCode, String productCode, String productName, String coverMiddleImage, Double productPrice, String typeName, String brandName, Integer putAway,Integer hotNum, Integer coverNum) {
		super();
		this.shopCode = shopCode;
		this.productCode = productCode;
		this.productName = productName;
		this.coverMiddleImage = coverMiddleImage;
		this.productPrice = productPrice;
		this.typeName = typeName;
		this.brandName = brandName;
		this.putAway = putAway;
		this.coverNum = coverNum;
		this.hotNum = hotNum;
	}

	private String shopCode;
	private String productCode;
	private String productName;
	private String coverMiddleImage;
	private Double productPrice;
	private String typeName;
	private String brandName;
	private Integer putAway;
	private Integer hotNum;
	private Integer coverNum;

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCoverMiddleImage() {
		return coverMiddleImage;
	}

	public void setCoverMiddleImage(String coverMiddleImage) {
		this.coverMiddleImage = coverMiddleImage;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Integer getPutAway() {
		return putAway;
	}

	public void setPutAway(Integer putAway) {
		this.putAway = putAway;
	}

	public Integer getCoverNum() {
		return coverNum;
	}

	public void setCoverNum(Integer coverNum) {
		this.coverNum = coverNum;
	}

	public Integer getHotNum() {
		return hotNum;
	}

	public void setHotNum(Integer hotNum) {
		this.hotNum = hotNum;
	}
}
