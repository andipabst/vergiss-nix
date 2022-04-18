package de.andicodes.vergissnix.data

import java.time.LocalDateTime

class DateTimeParser(now: LocalDateTime = LocalDateTime.now()) {

    fun parse(text: String): List<LocalDateTime> {
        return listOf(LocalDateTime.now())
    }
}
