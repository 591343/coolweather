package com.example.lenovo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by LENOVO on 2018/11/24.
 */

public class Weather {
    public String status;//状态,成功返回ok,失败则会返回具体原因

    public Basic basic;//城市基本状况

    public AQI aqi;//天气状况

    public Now now;//此时状况

    public Suggestion suggestion;//贴心建议

    @SerializedName("daily_forecast")
    public  List<Forecast> forecastList;//未来几日天气预报
}
