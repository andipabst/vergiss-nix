package de.andicodes.vergissnix.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.ZonedDateTime
import java.util.*

@Entity
data class Task(
    var time: ZonedDateTime? = null,
    var timeDone: ZonedDateTime? = null,
    var timeCreated: ZonedDateTime? = null,
    var text: String? = null
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}