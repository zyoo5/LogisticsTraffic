package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.d.pay.WeiXinPay;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.OrderDetailMode;
import com.bt.zhangzy.logisticstraffic.data.Product;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.AdViewFlipper;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonFavorite;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseComment;
import com.bt.zhangzy.network.entity.ResponseCompany;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ZhangZy on 2015/6/11.
 */
public class DetailCompany extends BaseActivity {

    private Product product;
    int favoritesId;//收藏id  标识是否被收藏；
    ResponseCompany jsonCompany;//更新的物流公司信息
    String jsonCommentList;//用于传递数据；
    private AdViewFlipper imgFlipper;//门头照片轮播图；
//    List<JsonComment> commentList;//评价列表
//    ListView listView;
//    EvaluationListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_cp);
        setPageName("门企详情");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(AppParams.BUNDLE_PRODUCT_KEY)) {
            product = (Product) bundle.get(AppParams.BUNDLE_PRODUCT_KEY);
            requestGetCompany(product.getID());
            View collectBtn = findViewById(R.id.detail_colletion);
            favoritesId = User.getInstance().checkFavoritesId(product.getID());
            collectBtn.setSelected(favoritesId > 0);
        } else {
            showToast("product = null");
            finish();
        }

//        listView = (ListView) findViewById(R.id.evaluation_list);
        initView();
        setTelephonyListen();

        //更新用户的浏览历史
        User.getInstance().addHistoryList(product);

    }

    private void setTelephonyListen() {
        //TODO 设置电话监听
        TelephonyManager mTelephonyMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyMgr.listen(new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        System.out.println("挂断 -CALL_STATE_IDLE");
                        if (openCall) {
                            openCall = false;
// restart app
//                            Intent i = getBaseContext().getPackageManager()
//                                    .getLaunchIntentForPackage(
//                                            getBaseContext().getPackageName());
////                            i.putExtra("target", TARGET);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(i);
                            onRestart();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        System.out.println("接听");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        System.out.println("响铃:来电号码" + incomingNumber);
                        //输出来电号码
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //更新店铺信息
    private void requestGetCompany(int id) {
        // to do 店铺信息没有返回
        HttpHelper.getInstance().get(AppURL.GetCompany.toString() + id, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                jsonCompany = ParseJson_Object(result, ResponseCompany.class);
                Product p = Product.ParseJson(jsonCompany);
                if (p != null) {
                    product = p;
                    //获取评论列表
                    requestCommentList();
                    initView_JsonCompany();
                }
            }

            @Override
            public void onFailed(String str) {
                showToast("店铺信息返回错误" + str);
            }
        });
    }

    String distance;//距离计算结果

    private void initView_JsonCompany() {
        //计算坐标之间的距离
        Location location = User.getInstance().getLocation();
        final JsonCompany jsonCompany = this.jsonCompany.getCompany();
        if (location != null && jsonCompany != null) {
            float latitude = Float.valueOf(location.getLatitude());
            float longitude = Float.valueOf(location.getLongitude());
            if (jsonCompany.getLatitude() != 0 && jsonCompany.getLongitude() != 0)
                distance = Tools.ComputeDistance(latitude, longitude, jsonCompany.getLatitude(), jsonCompany.getLongitude());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JsonCompany jsonCompany = product.getCompany().getCompany();
                if (jsonCompany != null) {
                    setTextView(R.id.detail_name_tx, product.getName());
                    setTextView(R.id.detail_cp_address_tx, product.getAddress());
                    setTextView(R.id.detail_type_tx, jsonCompany.getOftenSendType());
                    String oftenRoute = jsonCompany.getOftenRoute();
                    if (!TextUtils.isEmpty(oftenRoute)) {
                        if (oftenRoute.charAt(oftenRoute.length() - 1) == ',') {
                            oftenRoute = oftenRoute.substring(0, oftenRoute.length() - 1);
                        }
                        oftenRoute = oftenRoute.replace("/", "——").replace(",", "\n");
                        setTextView(R.id.detail_lines_tx, oftenRoute);
                    }
                    setTextView(R.id.detail_size_tx, jsonCompany.getScaleOfOperation());

                    setImageUrl(R.id.detail_user_head_img, product.getCompany().getUser().getPortraitUrl());

                    //照片初始化
                    if (imgFlipper != null) {
                        imgFlipper.addView(jsonCompany.getPhotoUrl());
                        boolean is_stop_flipping = true;
                        if (!TextUtils.isEmpty(jsonCompany.getPhotoUrl2())) {
                            imgFlipper.addView(jsonCompany.getPhotoUrl2());
                            is_stop_flipping = false;
                        }
                        if (!TextUtils.isEmpty(jsonCompany.getPhotoUrl3())) {
                            imgFlipper.addView(jsonCompany.getPhotoUrl3());
                            is_stop_flipping = false;
                        }

                        if (is_stop_flipping)
                            imgFlipper.stopFlipping();
                    }
//                    setImageUrl(R.id.detail_flipper_1, jsonCompany.getPhotoUrl());
//                    if (TextUtils.isEmpty(jsonCompany.getPhotoUrl2())) {
//                        findViewById(R.id.detail_flipper_2).setVisibility(View.GONE);
//                    } else
//                        setImageUrl(R.id.detail_flipper_2, jsonCompany.getPhotoUrl2());
//
//                    if (TextUtils.isEmpty(jsonCompany.getPhotoUrl3())) {
//                        findViewById(R.id.detail_flipper_3).setVisibility(View.GONE);
//                    } else
//                        setImageUrl(R.id.detail_flipper_3, jsonCompany.getPhotoUrl3());


                    if (TextUtils.isEmpty(distance)) {
                        setTextView(R.id.detail_cp_distance_tx, "距离未知");
                    } else {
                        String str = getString(R.string.location_distance_km);
                        str = String.format(str, distance);
                        setTextView(R.id.detail_cp_distance_tx, str);
                    }


                    RatingBar bar = (RatingBar) findViewById(R.id.detail_lv_bar);
                    if (jsonCompany.getStar() > 0) {
                        bar.setRating((float) jsonCompany.getStar());
                    } else {
                        bar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initView() {
        if (AppParams.DRIVER_APP || User.getInstance().getUserType() == Type.CompanyInformationType) {
            findViewById(R.id.detail_gray_line).setVisibility(View.GONE);
            findViewById(R.id.detail_order_btn).setVisibility(View.GONE);
        }

        if (product != null) {
            setTextView(R.id.detail_name_tx, product.getName());
            setTextView(R.id.detail_cp_address_tx, product.getAddress());
            setImageUrl(R.id.detail_user_head_img, product.getIconImgUrl());
        }
//            ImageView headImg = (ImageView) findViewById(R.id.detail_user_head_img);
//        String url = "http://img1.3lian.com/img2011/w1/105/4/13.jpg";
//        ImageHelper.getInstance().load(url, headImg);

        imgFlipper = (AdViewFlipper) findViewById(R.id.detail_cp_flipper);
    }

    boolean openCall;

    public void onClick_CallPhone(View view) {
        if (!User.getInstance().checkUserVipStatus(this)) {
            return;
        }
        openCall = true;
//        ContextTools.callPhone(this, "10010");
//        getApp().callPhone(product.getPhoneNumber());
        showDialogCallPhone(product.getPhoneNumber(), product.getID());
    }


    public void onClick_Evaluation(View view) {
        if (TextUtils.isEmpty(jsonCommentList)) {
            showToast("评价列表为空");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.BUNDLE_EVALUATION_JSON_LIST, jsonCommentList);
        startActivity(EvaluationListActivity.class, bundle);
    }

    public void onClick_Map(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(AppParams.WEB_PAGE_NAME, "物流公司位置");
        bundle.putString(AppParams.WEB_PAGE_URL, String.format(AppURL.LOCATION_MAP_COMPANY.toString(), product.getID()));
        startActivity(WebViewActivity.class, bundle);
    }

    public void onClick_OpenPicture(View view) {

        ImageView img = (ImageView) view;
        if (img != null) {
            gotoActionView(img);
            return;
        }
//        final int[] ids = {R.id.detail_flipper_1, R.id.detail_flipper_2, R.id.detail_flipper_3};
//        ArrayList<String> list = new ArrayList<>();
//        for (String str : product.getPhotoImgUrl())
//            list.add(str);
//        Bundle bundle = new Bundle();
////        bundle.putString(AppParams.BUNDLE_PICTURE_URL, jsonCompany.getPhotoUrl());
//        bundle.putStringArrayList(AppParams.BUNDLE_PICTURE_ARRAY, list);
//        startActivity(PictureActivity.class, bundle);
    }

    public void gotoActionView(ImageView imageView) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setAction("android.intent.action.GET_CONTENT");
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String uriString = MediaStore.Images.Media.insertImage(getContentResolver(), image, null, null);
        if (TextUtils.isEmpty(uriString)) {
            showToast("图片无法打开");
            return;
        }
        Uri uri = Uri.parse(uriString);
        intent.setDataAndType(uri, "image/*");

        startActivity(intent);

    }

    public void onClick_Order(View view) {
        if (!User.getInstance().isLogin()) {
            //用户未登陆时自动跳转到登陆页面
            gotoLogin();
            return;
        }
        if (User.getInstance().getJsonTypeEntity() == null) {
            showToast("请先完成企业认证");
            return;
        }
        if (User.getInstance().checkUserStatus(this)) {
            return;
        }
        if (!User.getInstance().checkUserVipStatus(this)) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(AppParams.ORDER_DETAIL_KEY_TYPE, OrderDetailMode.CreateMode.ordinal());
        bundle.putSerializable(AppParams.BUNDLE_PRODUCT_KEY, product);
        startActivity(OrderDetailActivity.class, bundle);
    }

    public void onClick_Share(View view) {
//        WeiXinPay.getInstanse().shareText(this, "分享测试");
        WeiXinPay.getInstanse().shareWebUrl(this, AppURL.DOWNLOAD_APP.toString());
    }

    public void onClick_CollectAdd(View view) {
        if (!User.getInstance().checkUserVipStatus(this)) {
            return;
        }
        if (findViewById(R.id.detail_colletion).isSelected()) {
            requestFavoritesDel();
        } else {
            //// TO DO: 2016-1-28  收藏接口
            requestFavorites();

        }
    }

    private void requestFavoritesDel() {
        HttpHelper.getInstance().del(AppURL.DelFavourite.toString() + favoritesId, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("取消收藏成功");
                Iterator<JsonFavorite> iterator = User.getInstance().getJsonFavorites().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getId() == favoritesId) {
                        iterator.remove();
                        break;
                    }
                }

                Iterator<Product> it = User.getInstance().getCollectionList().iterator();
                while (it.hasNext()) {
                    if (it.next().getID() == product.getID()) {
                        it.remove();
                        break;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.detail_colletion).setSelected(false);
                    }
                });

            }

            @Override
            public void onFailed(String str) {
                showToast("取消收藏失败");
            }
        });
    }

    private void requestFavorites() {
        //传4个参数 fromRole,fromRoleId,toRole,toRoleId
        JsonUser jsonUser = User.getInstance().getJsonUser();
        if (jsonUser == null) {
            showToast("用户未登陆");
            return;
        }
        int fromRole = jsonUser.getRole();
        int fromRoleId = User.getInstance().getRoleId();
        int toRole = Type.CompanyInformationType.toRole();//默认只能显示信息部
        int toRoleId = product.getID();

        HashMap<String, Integer> params = new HashMap<>();
        params.put("fromRole", fromRole);
        params.put("fromRoleId", fromRoleId);
        params.put("toRole", toRole);
        params.put("toRoleId", toRoleId);


        HttpHelper.getInstance().get(AppURL.GetFavourite, params,
                new JsonCallback() {
                    @Override
                    public void onSuccess(String msg, String result) {
                        JsonFavorite favorite = ParseJson_Object(result, JsonFavorite.class);
                        favoritesId = favorite.getId();
                        User.getInstance().getJsonFavorites().add(favorite);
                        User.getInstance().addCollectionProduct(product);
                        showToast("收藏成功");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.detail_colletion).setSelected(true);
                            }
                        });
                    }

                    @Override
                    public void onFailed(String str) {
                        showToast("收藏失败");
                    }
                });
    }


    private void requestCommentList() {

        HashMap<String, String> params = new HashMap<>();
        params.put("role", String.valueOf(Type.CompanyInformationType.toRole()));
        params.put("roleId", String.valueOf(product.getID()));

        HttpHelper.getInstance().get(AppURL.GetCommentList, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
//                List<JsonComment> commentList = ParseJson_Array(result, JsonComment.class);
                List<ResponseComment> commentList = ParseJson_Array(result, ResponseComment.class);
                if (commentList == null || commentList.isEmpty()) {
                    showToast("评价列表为空");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.dl_evaluate_ly).setVisibility(View.GONE);
                        }
                    });
                    return;
                }
                jsonCommentList = result;
//                adapter = new EvaluationListAdapter(commentList);
                setSampleList(commentList);
            }

            @Override
            public void onFailed(String str) {
                showToast("评价列表获取失败" + str);
            }
        });
    }

    private void setSampleList(final List<ResponseComment> commentList) {

        if (commentList != null && !commentList.isEmpty()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final int[] ly_ids = {R.id.detail_evaluate_1_ly, R.id.detail_evaluate_2_ly};
                    View view_ly;
                    RatingBar starBar;
                    for (int k = 0; k < ly_ids.length; k++) {
                        view_ly = findViewById(ly_ids[k]);
                        if (k < commentList.size()) {
                            ResponseComment json = commentList.get(k);
                            ViewUtils.setText((TextView) view_ly.findViewById(R.id.item_name_tx), json.getRoleObject().getName());
                            ViewUtils.setText((TextView) view_ly.findViewById(R.id.item_content_tx), json.getComment().getContent());
                            ViewUtils.setText((TextView) view_ly.findViewById(R.id.item_date_tx), Tools.toStringDate(json.getComment().getDate()));
                            starBar = (RatingBar) view_ly.findViewById(R.id.item_star_bar);
                            starBar.setRating((float) json.getComment().getRate());
                            ViewUtils.setImageUrl((ImageView) view_ly.findViewById(R.id.evaluate_list_user_img), json.getRoleObject().getPersonPhotoUrl());
                        } else {
                            view_ly.setVisibility(View.GONE);
                        }
                    }

                }
            });

        }


    }
}
