package com.trade.bluehole.trad.entity.pro;

/**
 * Created by Administrator on 2015-03-25.
 */
public class ShopCoverType implements    java.io.Serializable{
    private Integer id;
    private String shopCode;
    private String coverTypeCode;
    private String coverTypeName;
    private String userCode;
    private String createDate;
    private Integer delFlag;
    private String remark;

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

    public String getCoverTypeCode() {
        return coverTypeCode;
    }

    public void setCoverTypeCode(String coverTypeCode) {
        this.coverTypeCode = coverTypeCode;
    }

    public String getCoverTypeName() {
        return coverTypeName;
    }

    public void setCoverTypeName(String coverTypeName) {
        this.coverTypeName = coverTypeName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(Integer productNumber) {
        this.productNumber = productNumber;
    }

    // Constructors
    private Integer num;
    private Integer productNumber;
}
