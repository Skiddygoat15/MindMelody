package com.devsquad.mind_melody.Adapter.SimpleDateFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    // 将 Date 转换为格式化后的字符串，并指定为悉尼时区，格式为 "MMM dd, HH:mm, yyyy"
    public static String formatDateToSydneyTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm, yyyy", Locale.getDefault()); // 格式为月 日, 时:分, 年
        sdf.setTimeZone(TimeZone.getTimeZone("Australia/Sydney")); // 设置时区为悉尼
        return sdf.format(date);
    }
}

