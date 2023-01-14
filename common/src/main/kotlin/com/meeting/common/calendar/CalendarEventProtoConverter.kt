package com.meeting.common.calendar

import com.meeting.ProtoCalendarEvent
import com.meeting.ProtoUserCalendarEvents
import com.meeting.ProtoUserCalendarEventsWithSuggestionsResponse
import com.meeting.util.datetime.toProtoDateTime
import com.meeting.util.datetime.toZonedDateTime

/**
 * Converts [CalendarEvent] to [ProtoCalendarEvent].
 */
fun CalendarEvent.toProtoCalendarEvent(): ProtoCalendarEvent = ProtoCalendarEvent.newBuilder().apply {
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
    ProtoUserCalendarEvents.newBuilder().apply { calendarId = this@toProtoUserCalendarEvents.calendarId }
        .addAllCalendarEvents(this@toProtoUserCalendarEvents.calendarEvents.map { it.toProtoCalendarEvent() }).build()

/**
 * Converts [ProtoUserCalendarEvents] to [CalendarEvents].
 */
fun ProtoUserCalendarEvents.toUserCalendarEvents() =
    CalendarEvents(this.calendarId, this.calendarEventsList.map { it.toCalendarEvent() })

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
    CalendarEventsWithSuggestions(this.userCalendarsList.map { it.toUserCalendarEvents() },
        this.suggestionsList.map { it.toCalendarEvent() })
