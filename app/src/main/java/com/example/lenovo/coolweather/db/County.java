package com.example.lenovo.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by LENOVO on 2018/11/17.
 * 县的数据表
 */

public class County extends DataSupport {
    private  int id;
    private String countyName;//县名
    private String weatherId;//县所对应的天气id
    private int cityId;//记录当前县所属市的id值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
