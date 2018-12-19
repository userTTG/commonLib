package com.bigkoo.pickerview;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.utils.StringUtil;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.contrarywind.interfaces.IPickerViewData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2018/9/26 下午5:03
 */
public class AddressPickerPresenter {
    private boolean isJsonLoaded = false;
    private ArrayList<CityBean> address1Items = new ArrayList<>();
    private ArrayList<ArrayList<CityBean>> address2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<CityBean>>> address3Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<ArrayList<CityBean>>>> address4Items = new ArrayList<>();

    public static final String SPLIT_ADDRESS = " ";
    public void loadJsonData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String JsonData = getJson(Utils.getApp(), "pcas-code.json");//获取assets目录下的json文件数据

                ArrayList<CityBean> jsonBean = new Gson().fromJson(JsonData,new TypeToken<List<CityBean>>(){}.getType());//用Gson 转成实体

                /**
                 * 添加省份数据
                 *
                 * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
                 * PickerView会通过getPickerViewText方法获取字符串显示出来。
                 */
                address1Items = jsonBean;

                //遍历省份
                for (int i = 0; i < jsonBean.size(); i++) {
                    ArrayList<CityBean> province_CityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<CityBean>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
                    ArrayList<ArrayList<ArrayList<CityBean>>> province_StreetList = new ArrayList<>();//该省的所有街道列表（第四级）
                    province_CityList.addAll(jsonBean.get(i).getChildren());

                    //所有城市数据
                    for (int c = 0; c < jsonBean.get(i).getChildren().size(); c++) {//遍历该省份的所有城市

                        ArrayList<CityBean> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                        ArrayList<ArrayList<CityBean>> city_StreetList = new ArrayList<>();//该城市的所有地区列表
                        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                        if (jsonBean.get(i).getChildren().get(c).getChildren() == null
                                || jsonBean.get(i).getChildren().get(c).getChildren().size() == 0) {
                            city_AreaList.add(new CityBean());
                        } else {
                            city_AreaList.addAll(jsonBean.get(i).getChildren().get(c).getChildren());
                        }
                        province_AreaList.add(city_AreaList);//添加该省所有地区数据

                        //所有地区数据
                        for(int k = 0; k<jsonBean.get(i).getChildren().get(c).getChildren().size();k++){
                            city_StreetList.add((ArrayList<CityBean>) jsonBean.get(i).getChildren().get(c).getChildren().get(k).getChildren());
                        }
                        province_StreetList.add(city_StreetList);
                    }

                    /**
                     * 添加城市数据
                     */
                    address2Items.add(province_CityList);

                    /**
                     * 添加地区数据
                     */
                    address3Items.add(province_AreaList);

                    /**
                     * 添加街道数据
                     */
                    address4Items.add(province_StreetList);

                    isJsonLoaded = true;
                }
            }
        }).start();
    }

    public List<String> getSelectAddress(TextView textView){
        if (TextUtils.isEmpty(textView.getText().toString())){
            return Arrays.asList(textView.getText().toString().split(SPLIT_ADDRESS));
        }
        return null;
    }

    public void selectAddress(final TextView textView) {// 弹出选择器
        if (isJsonLoaded){
            OptionsPickerView addressPicker = new OptionsPickerBuilder(ActivityUtils.getTopActivity(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3,int option4, View v) {
                    String[] selectAddress = getAddressArrray(options1,options2,options3,option4);
                    textView.setText(StringUtil.arrayToSplitString(selectAddress,SPLIT_ADDRESS));
                }
            })
                    .setCancelColor(Color.parseColor("#00c1c1"))
                    .setSubmitColor(Color.parseColor("#00c1c1"))
                    .build();
            addressPicker.setPicker(address1Items, address2Items, address3Items, address4Items);
            if (!TextUtils.isEmpty(textView.getText().toString())){
                int[] positions = getAddressPositions(textView.getText().toString());
                addressPicker.setSelectOptions(positions[0],positions[1],positions[2],positions[3]);
            }
            addressPicker.show();
        }else {
//            BNToastUtil.showShort("数据加载中请稍后尝试");
        }
    }

    private String[] getAddressArrray(int option1,int option2,int option3,int option4){
        String[] address = new String[4];
        address[0] = address1Items.get(option1).getName();
        address[1] = address2Items.get(option1).get(option2).getName();
        address[2] = address3Items.get(option1).get(option2).get(option3).getName();
        address[3] = address4Items.get(option1).get(option2).get(option3).get(option4).getName();
        return address;
    }

    private int[] getAddressPositions(String addresses){
        int[] positions = new int[4];
        String[] addressArray = addresses.split(SPLIT_ADDRESS);
        positions[0] = getPosition(address1Items,addressArray[0]);
        List<CityBean> cityBeanList;
        cityBeanList = address2Items.get(positions[0]);
        positions[1] = getPosition(cityBeanList,addressArray[1]);
        cityBeanList = address3Items.get(positions[0]).get(positions[1]);
        positions[2] = getPosition(cityBeanList,addressArray[2]);
        cityBeanList = address4Items.get(positions[0]).get(positions[1]).get(positions[2]);
        positions[3] = getPosition(cityBeanList,addressArray[3]);
        return positions;
    }

    private int getPosition(List<CityBean> cityBeanList,String address){
        for (int i = 0;i<cityBeanList.size();i++){
            if (cityBeanList.get(i).getName().equals(address)){
                return i;
            }
        }
        return -1;
    }

    public class CityBean implements IPickerViewData {
        private String name;
        private List<CityBean> children;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CityBean> getChildren() {
            return children;
        }

        public void setChildren(List<CityBean> children) {
            this.children = children;
        }


        @Override
        public String getPickerViewText() {
            return name;
        }
    }

    public static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
