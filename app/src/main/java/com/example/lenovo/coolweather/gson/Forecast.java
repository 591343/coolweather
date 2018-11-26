package com.example.lenovo.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LENOVO on 2018/11/24.
 * 未来几天的天气预报数据都是一个类型为一个数组
 * 定义时只定义单日天气即可,然后在声明实体类引用的时候使用集合类型来进行声明
 */


public class Forecast {
    public String date;//日期

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;//最高温度
        public String min;//最低温度
    }

    public class More{

        @SerializedName("txt_d")
        public String info;//天气状况

    }


}
