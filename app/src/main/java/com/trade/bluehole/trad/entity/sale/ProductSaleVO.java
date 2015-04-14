package com.trade.bluehole.trad.entity.sale;

public class ProductSaleVO {
	private String productName;
	private String productCode;
	private String productImage;
	private Double oldPrice;
	private Double salePrice;
	private String dynamicCode;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public Double getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(Double oldPrice) {
		this.oldPrice = oldPrice;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public String getDynamicCode() {
		return dynamicCode;
	}

	public void setDynamicCode(String dynamicCode) {
		this.dynamicCode = dynamicCode;
	}

	public ProductSaleVO(String productName, String productCode, String productImage, Double oldPrice, Double salePrice, String dynamicCode) {
		super();
		this.productName = productName;
		this.productCode = productCode;
		this.productImage = productImage;
		this.oldPrice = oldPrice;
		this.salePrice = salePrice;
		this.dynamicCode = dynamicCode;
	}
}
