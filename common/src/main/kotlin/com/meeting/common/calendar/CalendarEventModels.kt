package com.meeting.common.calendar

import com.meeting.ProtoCalendarEvent
import com.meeting.ProtoUserCalendarEvents
import com.meeting.ProtoUserCalendarEventsWithSuggestionsResponse
import com.meeting.util.datetime.toProtoDateTime
import com.meeting.util.datetime.toZonedDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class CalendarEvent(val startDateTime: ZonedDateTime, val endDateTime: ZonedDateTime)

infix fun CalendarEvent.shiftToZone(zoneId: ZoneId) = CalendarEvent(
    this.startDateTime.withZoneSameInstant(zoneId),
    this.endDateTime.withZoneSameInstant(zoneId)
)

/**
 * All calendar events for one user id.
 */
data class CalendarEvents(val userId: String, val calendarEvents: List<CalendarEvent>)

infix fun CalendarEvents.shiftToZone(zoneId: ZoneId) = CalendarEvents(
    this.userId,
    this.calendarEvents.map { it shiftToZone zoneId }
)

/**
 * Calendar events for all user ids, and list of possible overlapping free timeslots.
 */
data class CalendarEventsWithSuggestions(
    val userCalendarEvents: List<CalendarEvents>,
    val suggestions: List<CalendarEvent>
)

infix fun CalendarEventsWithSuggestions.shiftToZone(zoneId: ZoneId) = CalendarEventsWithSuggestions(
    this.userCalendarEvents.map { it shiftToZone zoneId },
    this.suggestions.map { it shiftToZone zoneId }
)

/**
 * Converts [CalendarEvent] to [ProtoCalendarEvent].
 */
fun CalendarEvent.toProtoCalendarEvent(): ProtoCalendarEvent = ProtoCalendarEvent.newBuilder()
    .apply {
        startDateTime = this@toProtoCalendarEvent.startDateTime.toProtoDateTime()
        endDateTime = this@toProtoCalendarEvent.endDateTime.toProtoDateTime()
    }.build()

/**
 * Converts [ProtoCalendarEvent] to [CalendarEvent].
 */
fun ProtoCalendarEvent.toCalendarEvent() =
    CalendarEvent(this.startDateTime.toZonedDateTime(), this.endDateTime.toZonedDateTime())

/**
 * Converts [CalendarEvents] to [ProtoUserCalendarEvents].
 */
fun CalendarEvents.toProtoUserCalendarEvents(): ProtoUserCalendarEvents =
    ProtoUserCalendarEvents.newBuilder().apply { userId = this@toProtoUserCalendarEvents.userId }
        .addAllCalendarEvents(this@toProtoUserCalendarEvents.calendarEvents.map { it.toProtoCalendarEvent() }).build()

/**
 * Converts [ProtoUserCalendarEvents] to [CalendarEvents].
 */
fun ProtoUserCalendarEvents.toUserCalendarEvents() =
    CalendarEvents(this.userId, this.calendarEventsList.map { it.toCalendarEvent() })

/**
 * Converts [CalendarEventsWithSuggestions] to [ProtoUserCalendarEventsWithSuggestionsResponse].
 */
fun CalendarEventsWithSuggestions.toProtoUserCalendarEventsWithSuggestionsResponse(): ProtoUserCalendarEventsWithSuggestionsResponse =
    ProtoUserCalendarEventsWithSuggestionsResponse.newBuilder()
        .addAllUserCalendars(this.userCalendarEvents.map { it.toProtoUserCalendarEvents() })
        .addAllSuggestions(this.suggestions.map { it.toProtoCalendarEvent() }).build()

/**
 * Converts [ProtoUserCalendarEventsWithSuggestionsResponse] to [CalendarEventsWithSuggestions].
 */
fun ProtoUserCalendarEventsWithSuggestionsResponse.toUserCalendarEventsWithSuggestions() =
    CalendarEventsWithSuggestions(
        this.userCalendarsList.map { it.toUserCalendarEvents() },
        this.suggestionsList.map { it.toCalendarEvent() }
    )
