package com.trade.bluehole.trad.entity.msg;

/**
 * Created by Administrator on 2015-04-19.
 */
public class MessageVO {
    private String msgCode;
    private String msgTitle;
    private String msgSubTitle;
    private String createTime;
    private String author;
    private Integer redState;

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgSubTitle() {
        return msgSubTitle;
    }

    public void setMsgSubTitle(String msgSubTitle) {
        this.msgSubTitle = msgSubTitle;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getRedState() {
        return redState;
    }

    public void setRedState(Integer redState) {
        this.redState = redState;
    }
}
