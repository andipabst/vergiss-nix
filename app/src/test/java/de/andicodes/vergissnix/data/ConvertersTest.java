package de.andicodes.vergissnix.data;

import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;


public class ConvertersTest {

    @Test
    public void zonedDateTimeFromISOString() {
        ZonedDateTime actual = Converters.zonedDateTimeFromISOString("2021-11-06T10:29:57.632+01:00");
        ZonedDateTime expected = ZonedDateTime.of(2021, 11, 6, 10, 29, 57, 632, ZoneOffset.ofHours(1));
        assertThat(actual).isCloseTo(expected, within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void zonedDateTimeToISOString() {
        ZonedDateTime time = ZonedDateTime.of(2021, 11, 6, 10, 29, 57, 632, ZoneOffset.ofHours(1));
        ZonedDateTime actual = Converters.zonedDateTimeFromISOString(Converters.zonedDateTimeToISOString(time));
        assertThat(actual).isCloseTo(actual, within(1, ChronoUnit.SECONDS));
    }
}