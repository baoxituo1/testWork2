package com.trade.bluehole.trad.entity.shop;

/**
 * Created by Administrator on 2015-06-03.
 */
public class ShopAuthentic implements java.io.Serializable {
    private String shopCode;
    private String userCode;
    private Long applyDate;
    private Long acceptDate;
    private Long doneDate;
    private Integer authenticResult;
    private String resultDescribe;
    private String applyDescribe;
    private String licenseImage;
    private String identityImageFront;
    private String identityImageReverse;
    private String acceptUser;
    private Integer delFlag;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Long applyDate) {
        this.applyDate = applyDate;
    }

    public Long getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(Long acceptDate) {
        this.acceptDate = acceptDate;
    }

    public Long getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Long doneDate) {
        this.doneDate = doneDate;
    }

    public Integer getAuthenticResult() {
        return authenticResult;
    }

    public void setAuthenticResult(Integer authenticResult) {
        this.authenticResult = authenticResult;
    }

    public String getResultDescribe() {
        return resultDescribe;
    }

    public void setResultDescribe(String resultDescribe) {
        this.resultDescribe = resultDescribe;
    }

    public String getApplyDescribe() {
        return applyDescribe;
    }

    public void setApplyDescribe(String applyDescribe) {
        this.applyDescribe = applyDescribe;
    }

    public String getLicenseImage() {
        return licenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        this.licenseImage = licenseImage;
    }

    public String getIdentityImageFront() {
        return identityImageFront;
    }

    public void setIdentityImageFront(String identityImageFront) {
        this.identityImageFront = identityImageFront;
    }

    public String getIdentityImageReverse() {
        return identityImageReverse;
    }

    public void setIdentityImageReverse(String identityImageReverse) {
        this.identityImageReverse = identityImageReverse;
    }

    public String getAcceptUser() {
        return acceptUser;
    }

    public void setAcceptUser(String acceptUser) {
        this.acceptUser = acceptUser;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
