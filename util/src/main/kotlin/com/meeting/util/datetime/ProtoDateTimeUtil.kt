package com.meeting.util.datetime

import com.google.type.Date
import com.google.type.DateTime
import com.google.type.TimeOfDay
import com.google.type.TimeZone
import java.time.*

/**
 * Converts [LocalDate] to [Date].
 */
fun LocalDate.toProtoDate(): Date = Date.newBuilder().apply {
    year = this@toProtoDate.year
    month = this@toProtoDate.monthValue
    day = this@toProtoDate.dayOfMonth
}.build()

/**
 * Converts [Date] to [LocalDate].
 */
fun Date.toLocalDate(): LocalDate = LocalDate.of(this.year, this.month, this.day)

/**
 * Converts [LocalTime] to [TimeOfDay].
 */
fun LocalTime.toProtoTimeOfDay(): TimeOfDay = TimeOfDay.newBuilder().apply {
    hours = this@toProtoTimeOfDay.hour
    minutes = this@toProtoTimeOfDay.minute
    seconds = this@toProtoTimeOfDay.second
    nanos = this@toProtoTimeOfDay.nano
}.build()

/**
 * Converts [TimeOfDay] to [LocalTime].
 */
fun TimeOfDay.toLocalTime(): LocalTime = LocalTime.of(this.hours, this.minutes, this.seconds, this.nanos)

/**
 * Converts [ZonedDateTime] to [DateTime].
 */
fun ZonedDateTime.toProtoDateTime(): DateTime = DateTime.newBuilder().apply {
    year = this@toProtoDateTime.year
    month = this@toProtoDateTime.monthValue
    day = this@toProtoDateTime.dayOfMonth
    hours = this@toProtoDateTime.hour
    minutes = this@toProtoDateTime.minute
    seconds = this@toProtoDateTime.second
    nanos = this@toProtoDateTime.nano
    timeZone = TimeZone.newBuilder().apply { id = this@toProtoDateTime.zone.id }.build()
}.build()

/**
 * Converts [DateTime] to [ZonedDateTime].
 */
fun DateTime.toZonedDateTime(): ZonedDateTime = ZonedDateTime.of(
    this.year,
    this.month,
    this.day,
    this.hours,
    this.minutes,
    this.seconds,
    this.nanos,
    if (this.timeZone.id == TimeZone.getDefaultInstance().id) {
        ZoneId.systemDefault()
    } else ZoneId.of(this.timeZone.id)
)

/**
 * Converts [Duration] to [Duration].
 */
fun com.google.protobuf.Duration.toDuration(): Duration = Duration.ofSeconds(this.seconds, this.nanos.toLong())

/**
 * Converts [Duration] to [com.google.protobuf.Duration].
 */
fun Duration.toProtoDuration() = com.google.protobuf.Duration.newBuilder().apply {
    seconds = this@toProtoDuration.seconds
    nanos = this@toProtoDuration.nano
}.build()
