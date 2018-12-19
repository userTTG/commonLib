package com.bigkoo.pickerview.utils;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @Description:
 * @Author: zhanghao
 * @Date: 2018/6/28 下午7:07
 */

public class StringUtil {

    public static String floatToString(float value){
        int copy = (int)value;
        if (copy == value){
            return String.valueOf(copy);
        }
        return String.valueOf(value);
    }

    /*方法二：推荐，速度最快
  * 判断是否为整数
  * @param str 传入的字符串
  * @return 是整数返回true,否则返回false
*/

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String arrayToString(String[] array){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i =0 ;i<array.length;i++){
            stringBuilder.append(array[i]);
        }
        return stringBuilder.toString();
    }

    public static String arrayToSplitString(String[] array,String split){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i =0 ;i<array.length;i++){
            stringBuilder.append(array[i]).append(split);
        }
        if (array.length>0){
            stringBuilder.delete(stringBuilder.length()-split.length(),stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    public static String getInnerNumber(String str){
        String  result = null;
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        result = m.replaceAll("").trim();
        return result;
    }

    public static void addSpanableStringWithStyle(@NonNull SpannableStringBuilder builder, @NonNull String str, @NonNull Object style, int flags){
        int startIndex = builder.length();
        builder.append(str);
        builder.setSpan(style,startIndex,builder.length(),flags);
    }
}
