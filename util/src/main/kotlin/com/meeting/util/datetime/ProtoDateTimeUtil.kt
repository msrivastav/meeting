package com.meeting.util.datetime

import com.google.type.Date
import com.google.type.DateTime
import com.google.type.TimeOfDay
import com.google.type.TimeZone
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Converts [LocalDate] to [Date].
 */
fun LocalDate.toProtoDate(): Date = Date.newBuilder()
    .apply { year = this@toProtoDate.year }
    .apply { month = this@toProtoDate.monthValue }
    .apply { day = this@toProtoDate.dayOfMonth }
    .build()

/**
 * Converts [Date] to [LocalDate].
 */
fun Date.toLocalDate(): LocalDate = LocalDate.of(this.year, this.month, this.day)

/**
 * Converts [LocalTime] to [TimeOfDay].
 */
fun LocalTime.toProtoTimeOfDay(): TimeOfDay = TimeOfDay.newBuilder()
    .apply { hours = this@toProtoTimeOfDay.hour }
    .apply { minutes = this@toProtoTimeOfDay.minute }
    .apply { seconds = this@toProtoTimeOfDay.second }
    .apply { nanos = this@toProtoTimeOfDay.nano }
    .build()


/**
 * Converts [TimeOfDay] to [LocalTime].
 */
fun TimeOfDay.toLocalTime(): LocalTime = LocalTime.of(this.hours, this.minutes, this.seconds, this.nanos)

/**
 * Converts [ZonedDateTime] to [DateTime].
 */
fun ZonedDateTime.toProtoDateTime(): DateTime = DateTime.newBuilder()
    .apply { year = this@toProtoDateTime.year }
    .apply { month = this@toProtoDateTime.monthValue }
    .apply { day = this@toProtoDateTime.dayOfMonth }
    .apply { hours = this@toProtoDateTime.hour }
    .apply { minutes = this@toProtoDateTime.minute }
    .apply { seconds = this@toProtoDateTime.second }
    .apply { nanos = this@toProtoDateTime.nano }
    .apply {
        timeZone = TimeZone.newBuilder()
            .apply { id = this@toProtoDateTime.zone.id }.build()
    }
    .build()

/**
 * Converts [DateTime] to [ZonedDateTime].
 */
fun DateTime.toZonedDateTime(): ZonedDateTime = ZonedDateTime.of(
    this.year, this.month, this.day,
    this.hours, this.minutes, this.seconds, this.nanos,
    if (this.timeZone.id == TimeZone.getDefaultInstance().id)
        ZoneId.systemDefault() else ZoneId.of(this.timeZone.id)
)
