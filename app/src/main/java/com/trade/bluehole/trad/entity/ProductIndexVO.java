package com.trade.bluehole.trad.entity;

import java.io.Serializable;

public class ProductIndexVO implements Serializable {
	
	public ProductIndexVO(){
		
	}

	public ProductIndexVO(String shopCode, String productCode,
						  String productName, String coverMiddleImage, Double productPrice,
						  String typeName, String brandName, Integer putAway, Integer hotNum,
						  Integer coverNum, Double salePrice, Integer productNum,
						  Integer collectNum,String videoAddress,String videoThumbAddress) {
		super();
		this.shopCode = shopCode;
		this.productCode = productCode;
		this.productName = productName;
		this.coverMiddleImage = coverMiddleImage;
		this.productPrice = productPrice;
		this.typeName = typeName;
		this.brandName = brandName;
		this.putAway = putAway;
		this.hotNum = hotNum;
		this.salePrice = salePrice;
		this.productNum = productNum;
		this.collectNum = collectNum;
        this.videoAddress = videoAddress;
        this.videoThumbAddress = videoThumbAddress;
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
	private Double salePrice;
	private Integer productNum;
	private Integer collectNum;
    private String videoAddress;
    private String videoThumbAddress;

    public String getVideoAddress() {
        return videoAddress;
    }

    public void setVideoAddress(String videoAddress) {
        this.videoAddress = videoAddress;
    }

    public String getVideoThumbAddress() {
        return videoThumbAddress;
    }

    public void setVideoThumbAddress(String videoThumbAddress) {
        this.videoThumbAddress = videoThumbAddress;
    }

    public Integer getProductNum() {
		return productNum;
	}

	public void setProductNum(Integer productNum) {
		this.productNum = productNum;
	}

	public Integer getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(Integer collectNum) {
		this.collectNum = collectNum;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

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
