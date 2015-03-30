package com.trade.bluehole.trad.entity.shop;

import java.sql.Timestamp;

public class ShopCommonInfo {
	private String shopCode;
	private String title;
	private String address;
	private String tags;
	private Double latitude;
	private Double longitude;
	private String createDate;

	private String shopLogo;
	private String slogan;
	private String shopBackground;
	private Integer shopLevel;
	private String provinceName;
	private String cityName;
	private String district;

    public ShopCommonInfo(){

    }

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getShopLogo() {
		return shopLogo;
	}

	public void setShopLogo(String shopLogo) {
		this.shopLogo = shopLogo;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getShopBackground() {
		return shopBackground;
	}

	public void setShopBackground(String shopBackground) {
		this.shopBackground = shopBackground;
	}

	public Integer getShopLevel() {
		return shopLevel;
	}

	public void setShopLevel(Integer shopLevel) {
		this.shopLevel = shopLevel;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public ShopCommonInfo(String shopCode, String title, String address, String tags, Double latitude, Double longitude, String createDate, String shopLogo, String slogan, String shopBackground, Integer shopLevel, String provinceName, String cityName, String district) {
		super();
		this.shopCode = shopCode;
		this.title = title;
		this.address = address;
		this.tags = tags;
		this.latitude = latitude;
		this.longitude = longitude;
		this.createDate = createDate;
		this.shopLogo = shopLogo;
		this.slogan = slogan;
		this.shopBackground = shopBackground;
		this.shopLevel = shopLevel;
		this.provinceName = provinceName;
		this.cityName = cityName;
		this.district = district;
	}

}
