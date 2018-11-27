package com.example.lenovo.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.coolweather.gson.Forecast;
import com.example.lenovo.coolweather.gson.Weather;
import com.example.lenovo.coolweather.service.AutoUpdateService;
import com.example.lenovo.coolweather.util.HttpUtil;
import com.example.lenovo.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 使用了SharedPreferences存储数据（以键值的方式存储数据）
 * 用Glide处理从网上获取的图片
 */
public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;//滚动控件

    private TextView titleCity;//城市

    private TextView titleUpdateTime;//更新时间

    private TextView degreeText;//温度

    private TextView weatherInfoText;//天气状况（晴，多云之类的）

    private LinearLayout forecastLayout;//未来几天的天气预报

    private TextView aqiText;//空气质量指数

    private TextView pm25Text;//PM2.5指数

    private TextView comfortText;//舒适度

    private TextView carWash;//洗车指数

    private TextView sportText;//运动建议

    private ImageView bingPicImg;//天气预报背景图片

    public SwipeRefreshLayout swipeRefreshLayout;//下拉刷新控件

    public DrawerLayout drawerLayout;//滑动菜单

    private Button navButton;//导航按钮用于引出滑动菜单

    private String mWeatherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*
        使得天气预报的背景图片覆盖全屏
         */
        if(Build.VERSION.SDK_INT>=21) {//如果版本号大于或等于21时，也就是5.0及以上系统时才会执行
            View decorView = getWindow().getDecorView();//得到当前活动的DecorView
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//表示活动的布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT);//将状态栏设置为透明色

        }

        setContentView(R.layout.activity_weather);
        //初始化各个控件
        bingPicImg=(ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aiq_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWash=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);//设置下拉刷新进度条的颜色

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);

        navButton=(Button)findViewById(R.id.nav_button);

        //数据的存储与读取
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);//创建实例对象
        String weatherString=prefs.getString("weather",null);//读取SharedPreferences文件中的数据，如果没有则返回null
        if(weatherString!=null) {
            //有缓存时直接解析数据天气
            Weather weather = Utility.handleWeatherRespone(weatherString);
            mWeatherId=weather.basic.weatherID;//获得天气ID
            showWeatherInfo(weather);
        }else {
            //无缓存时去服务器查询天气
            mWeatherId=getIntent().getStringExtra("weather_id");//获得对应的天气id
            weatherLayout.setVisibility(View.INVISIBLE);//注意，在请求数据时先将ScrollView进行隐藏,不然空数据看上去很奇怪
            requestWeather(mWeatherId);//从服务器上请求数据
        }

        //设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);//加载图片
        }else{
            loadBingPic();
        }

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);//打开滑动菜单
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */

    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+
                weatherId+"&key=51f29d3b20e44fc892c8bbd4f085a27f";//数据所在的URL

        //请求数据
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            //回调失败
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);//用于表示刷新事件结束,并隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
             final String responseText =response.body().string();//获得数据
                final Weather weather=Utility.handleWeatherRespone(responseText);//解析数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){ //如果请求数据成功且有效
                            //向SharedPreferences文件中存储数据一共分为3步实现following
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();//获取编辑对象
                            editor.putString("weather",responseText);//将数据添加到editor对象中
                            editor.apply();//将添加的数据提交，从而完成数据的存储操作
                            mWeatherId=weather.basic.weatherID;
                            showWeatherInfo(weather);//天气预报显示
                        }else{//没有网时可能会发生
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();

                        }
                        swipeRefreshLayout.setRefreshing(false);//用于表示刷新事件结束,并隐藏刷新进度条
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    public  void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);//布局填充剂,第一个参数子布局，第二个父布局
            TextView dateText=(TextView) view.findViewById(R.id.date_text);
            TextView infoText=(TextView) view.findViewById(R.id.info_text);
            TextView maxText=(TextView) view.findViewById(R.id.max_text);
            TextView minText=(TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);//添加子布局布局
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort="舒适度: "+weather.suggestion.comfort.infp;
        String carwash="洗车指数: "+weather.suggestion.carWash.info;
        String sport="运动建议: "+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWash.setText(carwash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);//所有数据布置成功，显示ScrollView
        Intent intent=new Intent(this, AutoUpdateService.class);//启动服务
        startService(intent);
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";//加载Url
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });

    }

}
