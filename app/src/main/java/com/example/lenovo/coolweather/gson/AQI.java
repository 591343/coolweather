package com.example.lenovo.coolweather.gson;

/**
 * Created by LENOVO on 2018/11/24.
 * AQI表示与空气质量有关的指数
 */

public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
