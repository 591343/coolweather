package com.example.lenovo.coolweather.util;

import android.text.TextUtils;

import com.example.lenovo.coolweather.db.City;
import com.example.lenovo.coolweather.db.County;
import com.example.lenovo.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LENOVO on 2018/11/17.
 * 由于服务器返回的数据为JSON格式所以提供一个工具类来解析和处理这种数据
 *
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     *
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){               //判断JSON数据是否为空
            try {
                JSONArray allProvinces=new JSONArray(response);   //创建一个JSON数组
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject= allProvinces.getJSONObject(i);//获取JSON数据中某个数据
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name")); //设置省名
                    province.setProvinceCode(provinceObject.getInt("id")); //设置省的代号
                    province.save();//将数据存到数据库中

                }
                return  true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

/**
 * 解析和处理服务器返回的市级数据
 */

    public static boolean handleCityResponse(String response  ,int provinceId){
        if(!TextUtils.isEmpty(response)){               //判断JSON数据是否为空
            try {
                JSONArray allCities=new JSONArray(response);   //创建一个JSON数组
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject= allCities.getJSONObject(i);//获取JSON数据中某个数据
                    City city=new City();
                    city.setCityName(cityObject.getString("name")); //设置市名
                    city.setCityCode(cityObject.getInt("id")); //设置市的代号
                    city.setProvinceId(provinceId); //设置该市所属省的代号
                    city.save();//将数据存到数据库中

                }
                return  true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){               //判断JSON数据是否为空
            try {
                JSONArray allCounties=new JSONArray(response);   //创建一个JSON数组
                for(int i=0;i<allCounties.length();i++){
                    JSONObject countyObject= allCounties.getJSONObject(i);//获取JSON数据中某个数据
                    County county=new County();
                    county.setCountyName(countyObject.getString("name")); //设置县名
                    county.setWeatherId(countyObject.getString("weather_id")); //设置县所对应的天气Id
                    county.setCityId(cityId);
                    county.save();//将数据存到数据库中

                }
                return  true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }


}
