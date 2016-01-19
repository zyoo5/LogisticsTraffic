package com.bt.zhangzy.logisticstraffic.data;

import android.text.TextUtils;

import com.bt.zhangzy.logisticstraffic.app.Constant;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/30.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static User instance = new User();

    public static User getInstance() {
        return instance;
    }

    /**
     * 加载用回对象，用于数据存储；
     *
     * @param user
     */
    public void loadUser(User user) {
        if (user == null)
            return;
        instance = user;
    }


    private boolean isLogin = false;
    private Type userType = Type.EmptyType;
    private long id;
    private String userName, nickName;
    private String phoneNum;
    private String address;
    private Location location;//保存用户的定位信息
    private boolean isFirstOpen = true;
    private boolean isVIP = false;//标记用户是否付费
    private boolean isSave = true;//标记用户是否记住用户名


    public boolean isSave() {
        return isSave;
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setIsVIP(boolean isVIP) {
        this.isVIP = isVIP;
    }

    public boolean isFirstOpen() {
        return isFirstOpen;
    }

    public void setIsFirstOpen(boolean isFirstOpen) {
        this.isFirstOpen = isFirstOpen;
    }

    /**
     * 司机列表
     */
    private ArrayList<People> driverList;
    /**
     * 浏览历史
     */
    private ArrayList<Product> historyList = new ArrayList<Product>();

    /**
     * 收藏列表
     */
    private ArrayList<Product> collectionList = new ArrayList<Product>();

    /**
     * 搜索关键字历史
     */
    private ArrayList<String> searchKeyWordList = new ArrayList<String>();

    private User() {
        //test data
        driverList = new ArrayList<People>();
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));

        searchKeyWordList.add("测试测试");
        searchKeyWordList.add("测试1");

        Product p = new Product(12343);
        p.setName("到底多大多大的");
        collectionList.add(p);

    }

    @Override
    public String toString() {
        return super.toString();
//        StringBuffer stringBuffer = new StringBuffer();
//        driverList.toJsonString();
//        return stringBuffer.toJsonString();
    }

    public ArrayList<String> getSearchKeyWordList() {
        return searchKeyWordList;
    }

    /**
     * 添加搜索记录
     *
     * @param keyStr
     */
    public void addSearchKeyWord(String keyStr) {
        if (TextUtils.isEmpty(keyStr))
            return;
        searchKeyWordList.add(keyStr);
        if (searchKeyWordList.size() > 5) {
            searchKeyWordList.remove(0);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<People> getDriverList() {
        return driverList;
    }

    public ArrayList<Product> getCollectionList() {
        return collectionList;
    }

    /**
     * 添加到收藏列表
     *
     * @param product
     */
    public void addCollectionProduct(Product product) {
        collectionList.add(product);
    }

    public ArrayList<Product> getHistoryList() {
        return historyList;
    }

    /*添加历史记录*/
    public void addHistoryList(Product product) {
        historyList.add(product);
    }

    public void setDriverList(ArrayList<People> driverList) {
        this.driverList = driverList;
    }

    public boolean getLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        this.isLogin = login;
    }

    public boolean isDevicesType() {
        return getUserType() == Type.DriverType;
    }

    public Type getUserType() {
        if (Constant.DEVICES_APP) {
            return Type.DriverType;
        } else
            return userType;
    }

    public void setUserType(Type userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
