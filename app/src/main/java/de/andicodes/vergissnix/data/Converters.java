package de.andicodes.vergissnix.data;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import androidx.room.TypeConverter;

class Converters {
    private static final DateTimeFormatter ZONED_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


    @TypeConverter
    public static ZonedDateTime zonedDateTimeFromISOString(String isoString) {
        return isoString == null ? null : ZonedDateTime.parse(isoString, ZONED_FORMATTER);
    }

    @TypeConverter
    public static String zonedDateTimeToISOString(ZonedDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(ZONED_FORMATTER);
    }
}
