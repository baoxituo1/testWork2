package com.trade.bluehole.trad.entity.msg;

/**
 * Created by Administrator on 2015-04-19.
 */
public class ShopLetter {

    private String letterCode;
    private String shopCode;
    private String letterTitle;
    private String letterContent;
    private String createTime;

    public String getLetterCode() {
        return letterCode;
    }

    public void setLetterCode(String letterCode) {
        this.letterCode = letterCode;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getLetterTitle() {
        return letterTitle;
    }

    public void setLetterTitle(String letterTitle) {
        this.letterTitle = letterTitle;
    }

    public String getLetterContent() {
        return letterContent;
    }

    public void setLetterContent(String letterContent) {
        this.letterContent = letterContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getLetterState() {
        return letterState;
    }

    public void setLetterState(Integer letterState) {
        this.letterState = letterState;
    }

    private Integer letterState;
}
