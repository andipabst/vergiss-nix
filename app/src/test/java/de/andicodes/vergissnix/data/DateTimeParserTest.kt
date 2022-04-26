package de.andicodes.vergissnix.data

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class DateTimeParserTest {

    private val BASE_TIME = LocalDateTime.of(2006, 1, 2, 15, 4, 5)

    @Test
    fun times() {
        assertDateTime("13:30", hour = 13, minute = 30, second = 0)
        //assertDateTime("in 5 Minuten", minute = 9)
    }

    @Test
    fun dates() {
        assertDateTime("01.10.2022", year = 2022, month = 10, day = 1)
    }

    fun assertDateTime(
        text: String,
        year: Int = BASE_TIME.year,
        month: Int = BASE_TIME.monthValue,
        day: Int = BASE_TIME.dayOfMonth,
        hour: Int = BASE_TIME.hour,
        minute: Int = BASE_TIME.minute,
        second: Int = BASE_TIME.second
    ) {
        val parser = DateTimeParser(BASE_TIME)
        val actual = parser.parse(text)

        assertEquals(1, actual.size)
        val expectedTime = LocalDateTime.of(year, month, day, hour, minute, second)
        assertEquals(expectedTime, actual[0])
    }
}