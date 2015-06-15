package com.trade.bluehole.trad.entity;

/**
 * Created by Administrator on 2015-03-25.
 */
public class ProductBase {
    private Integer id;
    private String productCode;
    private String brandCode;
    private String productColor;
    private String productSize;
    private Integer productNumber;
    private String productInfo;
    private Integer delFlag;
    private String videoAddress;
    private String videoThumbAddress;
    private Integer videoState;
    private String videoBak;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public Integer getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(Integer productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

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

    public Integer getVideoState() {
        return videoState;
    }

    public void setVideoState(Integer videoState) {
        this.videoState = videoState;
    }

    public String getVideoBak() {
        return videoBak;
    }

    public void setVideoBak(String videoBak) {
        this.videoBak = videoBak;
    }
}
