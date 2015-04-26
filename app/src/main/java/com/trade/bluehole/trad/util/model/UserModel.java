package com.trade.bluehole.trad.util.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * 用户登录信息
 * Created by Administrator on 2015-04-26.
 */
@Table(name="User")
public class UserModel extends Model {
    @Column(name = "UserCode")
    public  String userCode;

    @Column(name = "ShopCode")
    public  String shopCode;

    @Column(name = "UserAccount")
    public  String userAccount;

    @Column(name = "PassWord")
    public String passWord;

    @Column(name = "CreateDate")
    public String createDate;

    @Column(name = "StateFlag")
    public String stateFlag;

    @Column(name = "Bak")
    public String bak;
}
