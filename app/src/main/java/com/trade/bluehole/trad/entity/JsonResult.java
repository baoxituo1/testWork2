package com.trade.bluehole.trad.entity;

/**
 * Created by Administrator on 2015-04-09.
 */
public class JsonResult {
    private boolean success;

    private String message;

    private String code;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
