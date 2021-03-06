package com.bt.zhangzy.tools;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * Created by ZhangZy on 2015/7/22.
 */
public final class Tools {

    private static final String TAG = Tools.class.getSimpleName();

    /**
     * 判断多个参数是否为空
     *
     * @param params
     * @return
     */
    public static boolean isEmptyStrings(String... params) {
        if (params == null)
            return true;
        for (String string : params)
            if (TextUtils.isEmpty(string))
                return true;

        return false;
    }


    public static String encryptString(String string) {
        if (TextUtils.isEmpty(string))
            return string;
        StringBuffer stringBuffer = new StringBuffer();
        if (IsPhoneNum(string)) {
            stringBuffer.append(string.substring(0, 2));
            for (int k = 2; k < string.length() - 3; k++)
                stringBuffer.append('*');
            stringBuffer.append(string.substring(string.length() - 3, string.length()));
        } else {
            stringBuffer.append(string.substring(0, 1));
            for (int k = 1; k < string.length(); k++)
                stringBuffer.append('*');
        }
        return stringBuffer.toString();
    }

    /**
     * 验证手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean IsPhoneNum(String mobiles) {
//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^1[3|4|5|7|8]\\d{9}$");

        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 按照sp分隔字符串，
     *
     * @param string
     * @param sp     分隔符
     * @return 返回一个字符串数组，最小两个元素
     */
    public static String[] splitAddress(String string, String sp) {
        String[] split = {"", ""};
        if (TextUtils.isEmpty(string))
            return split;
        String[] tmp_split = string.split(sp);
        if (tmp_split.length > 0)
            split[0] = tmp_split[0];
        if (tmp_split.length > 1)
            split[1] = tmp_split[1];
        if (tmp_split.length > 2)
            split = tmp_split;

        return split;
    }


    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = {1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600};
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z'};

    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {

            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
            } else {
                char spell = getFirstLetter(ch);
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString();
    }

    // 获取一个汉字的首字母
    public static Character getFirstLetter(char ch) {

        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
            return null;
        } else {
            return convert(uniCode);
        }
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }


    /*"yyyy-MM-dd HH:mm:ss"
    * */
    static final String FORMAT_DEFAULT = "yyyy-MM-dd";
    static SimpleDateFormat DefaultDateFormat = new SimpleDateFormat(FORMAT_DEFAULT, Locale.getDefault());

    /**
     * 转换显示日期
     *
     * @param date
     * @return
     */
    public static String toStringDate(Date date) {
        if (date == null)
            return "";
        return DefaultDateFormat.format(date);
//        return toStringDate(date,FORMAT_DEFAULT);
    }

    public static String toStringDate(Date date, String format) {
        if (date == null)
            return "";
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    /**
     * @param format 格式
     * @return 返回当前时间的字符串表达式
     */
    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    /**
     * @return 根据默认的格式返回当前时间
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }


    /**
     * MD5加密字符串
     *
     * @param string
     * @return
     */
    public static String MD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static JSONObject toJson(String string) {
        try {
            return new JSONObject(string);
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.w(TAG, string, e);
        }
        return null;
    }


    public static String toString(char sep, String... params) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String p : params) {
            stringBuffer.append(p).append(sep);
        }
        //删除最后一个 /
        if (stringBuffer.length() > 0 && stringBuffer.charAt(stringBuffer.length() - 1) == sep)
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    private static double EARTH_RADIUS = 6378.137;////地球半径

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * 参数为String类型
     *
     * @return      
     */
    public static String ComputeDistance(String lat1Str, String lng1Str, String lat2Str, String lng2Str) {
        Double lat1 = Double.parseDouble(lat1Str);
        Double lng1 = Double.parseDouble(lng1Str);
        Double lat2 = Double.parseDouble(lat2Str);
        Double lng2 = Double.parseDouble(lng2Str);

        return ComputeDistance(lat1, lng1, lat2, lng2);
    }

    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * 参数为String类型
     *
     * @param lat1 用户经度
     * @param lng1 用户纬度
     * @param lat2 商家经度
     * @param lng2 商家纬度
     * @return      
     */
    @NonNull
    public static String ComputeDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double difference = radLat1 - radLat2;
        double mdifference = rad(lng1) - rad(lng2);
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(mdifference / 2), 2)));
        distance = distance * EARTH_RADIUS;
        distance = Math.round(distance * 10000) / 10000;
        String distanceStr = distance + "";
        distanceStr = distanceStr.substring(0, distanceStr.indexOf("."));

        return distanceStr;
    }

    /**
     * 去除字符串中的 \r \t \n
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
