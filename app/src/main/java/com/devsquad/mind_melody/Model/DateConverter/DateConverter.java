package com.devsquad.mind_melody.Model.DateConverter;
import androidx.room.TypeConverter;

import java.util.Date;

// Convert Date to timestamp or vice versa
public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}