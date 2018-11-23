package com.example.lenovo.coolweather.util;


import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by LENOVO on 2018/11/17.
 * 全国所有的省市县的数据都是从服务器端获得的，因此这里和服务器的交互是必不可少的
 */

//用于发起http请求
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();//创建OkHttpClient实例
        Request request=new Request.Builder().url(address).build();//Request请求
        client.newCall(request).enqueue(callback);//callback回调处理服务器响应

    }

}
