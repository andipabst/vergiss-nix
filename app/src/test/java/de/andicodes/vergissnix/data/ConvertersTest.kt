package de.andicodes.vergissnix.data

import de.andicodes.vergissnix.data.Converters.zonedDateTimeFromISOString
import de.andicodes.vergissnix.data.Converters.zonedDateTimeToISOString
import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ConvertersTest {
    @Test
    fun zonedDateTimeFromISOString() {
        val actual = zonedDateTimeFromISOString("2021-11-06T10:29:57.632+01:00")
        val expected = ZonedDateTime.of(2021, 11, 6, 10, 29, 57, 632, ZoneOffset.ofHours(1))
        Assertions.assertThat(actual).isCloseTo(expected, Assertions.within(1, ChronoUnit.SECONDS))
    }

    @Test
    fun zonedDateTimeToISOString() {
        val time = ZonedDateTime.of(2021, 11, 6, 10, 29, 57, 632, ZoneOffset.ofHours(1))
        val actual = zonedDateTimeFromISOString(zonedDateTimeToISOString(time))
        Assertions.assertThat(actual).isCloseTo(actual, Assertions.within(1, ChronoUnit.SECONDS))
    }
}