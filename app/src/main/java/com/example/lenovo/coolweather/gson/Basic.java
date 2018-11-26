package com.example.lenovo.coolweather.gson;

/**
 * Created by LENOVO on 2018/11/24.
 */

import com.google.gson.annotations.SerializedName;

/**
*为了使用GOSN解析服务器的数据而要建立对应的实体类
 */

public class Basic {

    @SerializedName("city")//使用注解的方式来让JSON字段和Java字段之间建立映射关系
    public String cityName;

    @SerializedName("id")
    public String weatherID;

    public Update update;//loc表示天气更新的时间

    public  class Update{//使用了内部类

        @SerializedName("loc")
        public String updateTime;
    }
}
