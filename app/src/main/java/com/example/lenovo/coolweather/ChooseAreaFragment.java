package com.example.lenovo.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.coolweather.R;
import com.example.lenovo.coolweather.db.City;
import com.example.lenovo.coolweather.db.County;
import com.example.lenovo.coolweather.db.Province;
import com.example.lenovo.coolweather.util.HttpUtil;
import com.example.lenovo.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by LENOVO on 2018/11/22
 * 遍历省市县数据的碎片.
 * 一定要用support.v4中的Fragment
 */

public class ChooseAreaFragment extends Fragment {
    public static final  int LEVEL_PROVINCE=0;
    public static final  int LEVEL_CITY=1;
    public static final  int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;//进度弹窗

    private TextView titleText;

    private Button  backButton; //返回按钮

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList=new ArrayList<>();//用来显示当前的数据列表

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     *选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     * 为碎片创建视图（加载布局）时调用
     * 显示数据列表
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);//将定义的choose.area.xml布局动态加载进来
        titleText=(TextView) view.findViewById(R.id.title_text);//在标题栏显示省市县信息
        backButton=(Button) view.findViewById(R.id.back_button);//返回上一个列表
        listView=(ListView) view.findViewById(R.id.list_view);//显示每个省市县的数据
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);//第一个参数当前上下文,第二个参属内置布局相当于显示一段文本.
        listView.setAdapter(adapter);//设置适配器
        return view;
    }

    /**
     *
     * @param savedInstanceState
     * 确保与碎片相关联的活动一定已经创建完毕的时候调用
     * 设置一系列点击事件
     * 根据当前级别的不同显示省市县列表
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //设置listView中子项的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {//将当前选中的天气id传到WeatherActivity中
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {//使用instanceof关键字判断出该碎片是在那个活动中
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);//创建Intent实例
                        intent.putExtra("weather_id", weatherId);//要传递的数据
                        startActivity(intent);//开始活动
                        getActivity().finish();//结束当前活动
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();//活动该活动实例
                        activity.drawerLayout.closeDrawers();//关闭滑动菜单
                        activity.swipeRefreshLayout.setRefreshing(true);//显示下拉刷新进度条
                        activity.requestWeather(weatherId);//请求新城市的天气信息
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();

                }
            }
        });
        queryProvinces();

        }

    /**
     * 查询全国所有的省,优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private  void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//设置按钮为不可见
        provinceList = DataSupport.findAll(Province.class);//查询这张表的所有数据返回类型为List<Province>.
        if (provinceList.size() > 0) {
            dataList.clear();//清楚dataList集合中的所有数据
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//通知数据发生改变刷新作用
            listView.setSelection(0);   //这个方法的作用就是将第position个item显示在listView的最上面一项
            currentLevel = LEVEL_PROVINCE;//设置当前级别
        } else {
            String address = "http://guolin.tech/api/china";//数据所在的URL
            queryFromServer(address, "province");//从服务器上查询
        }
    }

        /**
        *查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
         *
         */
        private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);//设置返回键可见
        cityList= DataSupport.where("provinceid= ?",String.valueOf(selectedProvince.getId())).find(City.class);//查找已选中的省份的所有城市数据
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库中查询，如果没有查询到再去服务器上查询
     */

    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid= ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;

        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");

        }
    }

    /**
     * 根据传入地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address,final String type){
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
             //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);//解析处理从服务器上获取的数据

                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());

                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){
                   //getActivity()获得Fragment依附的Activity对象
                    //getContext()这个是View类中提供的方法，在继承了View的类中才可以调用，返回的是当前View运行在哪个Activity Context中.
                    //由于queryProvinces()方法牵扯到UI操作了.因此必须再主线程中调用,这里借助runOnUiThread()方法来实现从子线程切换到主线程
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }

                        }
                    });
                }
            }

        });


    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载....");
            progressDialog.setCanceledOnTouchOutside(false);


        }
        progressDialog.show();//显示对话框

    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();//关闭进度对话框
        }
    }

}


