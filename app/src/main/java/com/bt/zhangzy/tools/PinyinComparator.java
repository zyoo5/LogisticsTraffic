package com.bt.zhangzy.tools;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;

/**
 * 按照拼音排序字符串
 * 缺点：无法处理多音字
 * Created by ZhangZy on 2015/8/11.
 */

public class PinyinComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {

        String key1 = o1.toString();

        String key2 = o2.toString();

        for (int i = 0; i < key1.length() && i < key2.length(); i++) {


            int codePoint1 = key1.charAt(i);

            int codePoint2 = key2.charAt(i);


            if (Character.isSupplementaryCodePoint(codePoint1)

                    || Character.isSupplementaryCodePoint(codePoint2)) {

                i++;

            }


            if (codePoint1 != codePoint2) {

                if (Character.isSupplementaryCodePoint(codePoint1)

                        || Character.isSupplementaryCodePoint(codePoint2)) {

                    return codePoint1 - codePoint2;

                }


                String pinyin1 = pinyin((char) codePoint1);

                String pinyin2 = pinyin((char) codePoint2);


                if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字

                    if (!pinyin1.equals(pinyin2)) {

                        return pinyin1.compareTo(pinyin2);

                    }

                } else {

                    return codePoint1 - codePoint2;

                }

            }

        }

        return key1.length() - key2.length();

    }


    private String pinyin(char c) {

        String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);

        if (pinyins == null) {

            return null;   //如果转换结果为空，则返回null

        }

        return pinyins[0];   //如果为多音字返回第一个音节

    }
}

