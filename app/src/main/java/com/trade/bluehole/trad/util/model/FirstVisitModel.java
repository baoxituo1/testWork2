package com.trade.bluehole.trad.util.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 首次访问标记
 * Created by Administrator on 2015-04-26.
 */
@Table(name="First")
public class FirstVisitModel extends Model{

    @Column
    public String visiFlag;
}
