package com.trade.bluehole.trad.entity.dynamic;

import java.io.Serializable;

/**
 * Created by Administrator on 2015-04-13.
 */
public class DynaicInfoVO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7213395792108727831L;
    private String dynamicCode;
    private String productCode;
    private String shopCode;
    private String productName;
    private String shopName;
    private Double oldPrice;
    private Double salePrice;
    private String productImage;
    private String saleCreateDate;
    private String saleStartDate;
    private String saleEndDate;
    private Integer saleFlag;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDynamicCode() {
        return dynamicCode;
    }

    public void setDynamicCode(String dynamicCode) {
        this.dynamicCode = dynamicCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getSaleCreateDate() {
        return saleCreateDate;
    }

    public void setSaleCreateDate(String saleCreateDate) {
        this.saleCreateDate = saleCreateDate;
    }

    public String getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(String saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public String getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(String saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public Integer getSaleFlag() {
        return saleFlag;
    }

    public void setSaleFlag(Integer saleFlag) {
        this.saleFlag = saleFlag;
    }
}
