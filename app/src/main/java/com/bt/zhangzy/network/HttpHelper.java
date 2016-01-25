package com.bt.zhangzy.network;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.tools.UploadFileTask;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 数据链接类
 * 1、POST    /url      创建
 * 2、DELETE  /url/xxx  删除
 * 3、PUT     /url/xxx  更新
 * 4、GET     /url/xxx  查看
 * Created by ZhangZy on 2015/6/4.
 */
public class HttpHelper extends OkHttpClient {

    private static final String TAG = HttpHelper.class.getSimpleName();

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/png");

    /**
     * 请求超时 秒
     */
    public static final int TIMEOUT = 40;

    /**
     * UUID
     */
    public static String BOUNDARY = java.util.UUID.randomUUID().toString();

    /**
     * 单例实体
     */
    private static HttpHelper mInstance;

    private HttpHelper() {
        this.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setWriteTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        this.setRetryOnConnectionFailure(true);//连接超时重试

    }

    public static HttpHelper getInstance() {
        if (mInstance == null)
            mInstance = new HttpHelper();

        return mInstance;
    }


    private String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = this.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    private String post(String url, HashMap textParams) throws IOException {
        RequestBody requestBody = null;
//        MultipartBuilder multBuilder = new MultipartBuilder();
//        multBuilder.type(MediaType.parse("multipart/form-data"+ ";boundary=" + BOUNDARY));
        FormEncodingBuilder builder = new FormEncodingBuilder();
        //表单
        if (textParams != null && textParams.size() > 0) {
            Set<String> keySet = textParams.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
                String name = it.next();
                String value = (String) textParams.get(name);
//                multBuilder.addFormDataPart(name,value);
                builder.add(name, value);
            }
        }
//        multBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"image\"; filename=\"jpg\""),RequestBody.create(MediaType.parse("image/jpg;charset=UTF-8"),data));
//        requestBody = multBuilder.build();
        requestBody = builder.build();

        return post(url, requestBody);
    }

    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        return post(url, body);
    }

    private String post(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = this.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    public void post(String url, JSONObject json, NetCallback callback) {
        if (json == null)
            return;
        Log.i(TAG, "post url = " + url + " json = " + json.toString());
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json.toString());
        post(url, body, callback);
    }

    /**
     * @param url
     * @param entity   fastJSON实体类
     * @param callback
     */
    public void post(String url, BaseEntity entity, NetCallback callback) {
        post(url, JSON.toJSONString(entity), callback);
    }

    public void put(String url, BaseEntity entity, NetCallback callback) {
        put(url, JSON.toJSONString(entity), callback);
    }

    /**
     * 异步线程访问网络
     *
     * @param url
     * @param json     参数格式 JSON
     * @param callback
     */
    public void post(String url, String json, NetCallback callback) {
        Log.i(TAG, "post url = " + url + " json = " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        post(url, body, callback);
    }

    public void put(String url, String json, NetCallback callback) {
        Log.i(TAG, "post url = " + url + " json = " + json);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, json);
        put(url, body, callback);
    }

    /***
     * 异步线程访问网络
     *
     * @param url
     * @param textParams       参数格式Map
     * @param responseCallback
     * @throws IOException
     */
    public void post(String url, HashMap<String, String> textParams, NetCallback responseCallback) {
        try {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            //表单
            if (textParams != null && textParams.size() > 0) {
                Log.i(TAG, "post url = " + url + " params = " + textParams.toString());
                Set<String> keySet = textParams.keySet();
                for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
                    String name = it.next();
                    String value = (String) textParams.get(name);
//                multBuilder.addFormDataPart(name,value);
                    builder.add(name, value);
                }
            }
            RequestBody body = builder.build();
            post(url, body, responseCallback);
        } catch (Exception e) {
            Log.w(TAG, "post(" + url + "," + textParams == null ? "" : textParams.toString() + ")", e);
        }
    }

    /***
     * 异步线程访问网络
     *
     * @param url
     * @param body
     * @param responseCallback
     * @throws IOException
     */
    private void post(String url, RequestBody body, NetCallback responseCallback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        enqueue(request, responseCallback);

    }

    private void put(String url, RequestBody body, NetCallback responseCallback) {
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        enqueue(request, responseCallback);

    }

    /**
     * 文件上传 待测试
     *
     * @param url
     * @param file
     * @param rspCallback
     */
    public void postFile(String url, File file, NetCallback rspCallback) {

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        enqueue(request, rspCallback);
    }

    /**
     * 图片上传  使用HttpURLConnection 上传；
     *
     * @param url
     * @param file
     * @param rspCallback
     */
    public static void uploadImagePost(String url, File file, final NetCallback rspCallback) {
        //  照片上传逻辑
        new UploadFileTask(url) {
            @Override
            protected void onPostExecute(String result) {
//                super.onPostExecute(s);
                if (rspCallback == null)
                    return;
                if (TextUtils.isEmpty(result)) {
                    rspCallback.onFailed("null");
                } else {
                    rspCallback.onSuccess(result);
                }
            }
        }.execute(file);
    }

    /**
     * 基于okhttp上传文件
     *
     * @param url
     * @param file
     * @param rspCallback
     */
    public void postImage(String url, File file, NetCallback rspCallback) {
        /* MediaType FORM = MediaType.parse("multipart/form-data");
        *  MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/png");
        * */
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
//        builder.addPart(RequestBody.create(MEDIA_TYPE_IMAGE, file));
        /* MediaType.parse("media/type") */
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));

//        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\""),
//                RequestBody.create(MediaType.parse("image/jpeg"), file));

        //遍历map中所有参数到builder
//        for (String key : map.keySet()) {
//            builder.addFormDataPart(key, map.get(key));
//        }

        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
//        for (String path : paths) {
//            builder.addFormDataPart("upload", null, RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
//        }


        //构建请求体
//        RequestBody requestBody = builder.build();

//        Headers handler = Headers.Builder.;
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

       /* MediaType jsonMediaType = MediaType.parse("application/json");
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"data\""),
                        RequestBody.create(jsonMediaType, photoMetaDataStr))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"localName\""),
                        RequestBody.create(MediaType.parse("text/plain"), localName.getPath()))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\""),
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(localName.getPath())))
                .build();*/

        enqueue(request, rspCallback);
    }

    public void get(String url, HashMap textParams, NetCallback responseCallback) {
        //表单
        if (textParams != null && textParams.size() > 0) {
            Log.i(TAG, "post url = " + url + " params = " + textParams.toString());
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url);
            stringBuffer.append("?");
            Set<String> keySet = textParams.keySet();
            for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
                String name = it.next();
                String value = (String) textParams.get(name);
//                multBuilder.addFormDataPart(name,value);
//                builder.add(name, value);
                stringBuffer.append(name + "=" + value);
            }
            get(stringBuffer.toString(), responseCallback);
        }
    }

    public void get(String url, NetCallback responseCallback) {
        try {
            Request request = new Request.Builder().url(url).get().build();
            this.newCall(request).enqueue(responseCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "网络请求异常url=" + url, e);
        }
    }

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public Response execute(Request request) throws IOException {
        return this.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public void enqueue(Request request, NetCallback responseCallback) {
        this.newCall(request).enqueue(responseCallback);
    }


    public void uploadImage() {
//        LinkedHashMap<String ,Object> map = new LinkedHashMap<>();
//        map.add("file", new File());
    }


}
