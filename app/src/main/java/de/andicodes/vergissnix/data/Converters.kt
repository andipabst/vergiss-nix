package de.andicodes.vergissnix.data

import androidx.room.TypeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal object Converters {
    private val ZONED_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    @JvmStatic
    @TypeConverter
    fun zonedDateTimeFromISOString(isoString: String?): ZonedDateTime? {
        return if (isoString == null) null else ZonedDateTime.parse(isoString, ZONED_FORMATTER)
    }

    @JvmStatic
    @TypeConverter
    fun zonedDateTimeToISOString(dateTime: ZonedDateTime?): String? {
        return dateTime?.format(ZONED_FORMATTER)
    }
}