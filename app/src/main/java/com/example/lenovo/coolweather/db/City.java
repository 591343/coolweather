package com.example.lenovo.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by LENOVO on 2018/11/17.
 * 市数据表
 */

public class City extends DataSupport {
    private  int id;
    private String cityName;//城市的名字
    private int cityCode;//市的代号
    private int provinceId;//记录当前市所属省的Id值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
