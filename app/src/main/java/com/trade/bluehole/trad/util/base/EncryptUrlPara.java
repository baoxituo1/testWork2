package com.trade.bluehole.trad.util.base;

/**
 * Created by Administrator on 2015-05-27.
 */
public class EncryptUrlPara {
    static final String myKey="402881214c99ASDD55DF99bf28ec0000";
    /**
     * 加密
     * @param encryptTxt
     * @return
     */
    public static String encrypt(String encryptTxt){
        String result="";
        try {
            result= SecurityHelper.encrypt(myKey, encryptTxt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
