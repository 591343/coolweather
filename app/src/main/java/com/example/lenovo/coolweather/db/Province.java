package com.example.lenovo.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by LENOVO on 2018/11/17.
 * 省数据表
 */


public class Province extends DataSupport {
    private  int id;//添加进数据库的字段（序号）
    private  String provinceName;//省名
    private  int provinceCode; //省的代号

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
