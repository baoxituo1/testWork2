package com.trade.bluehole.trad.entity;

/**
 * Created by Administrator on 2015-03-25.
 */
public class Product {
    private Integer id;
    private String shopCode;
    private String productCode;
    private String productName;
    private String coverSmallImage;
    private String coverMiddleImage;
    private String coverBigImage;
    private Double productPrice;
    private String createDate;
    private Double salePrice;
    private Integer saleFlag;
    private String saleCreateDate;
    private String saleStartDate;
    private String saleEndDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCoverSmallImage() {
        return coverSmallImage;
    }

    public void setCoverSmallImage(String coverSmallImage) {
        this.coverSmallImage = coverSmallImage;
    }

    public String getCoverMiddleImage() {
        return coverMiddleImage;
    }

    public void setCoverMiddleImage(String coverMiddleImage) {
        this.coverMiddleImage = coverMiddleImage;
    }

    public String getCoverBigImage() {
        return coverBigImage;
    }

    public void setCoverBigImage(String coverBigImage) {
        this.coverBigImage = coverBigImage;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getSaleFlag() {
        return saleFlag;
    }

    public void setSaleFlag(Integer saleFlag) {
        this.saleFlag = saleFlag;
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

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    private Integer delFlag;
}
