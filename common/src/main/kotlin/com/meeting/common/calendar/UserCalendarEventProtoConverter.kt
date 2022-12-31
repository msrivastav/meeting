package com.meeting.common.calendar

import com.meeting.ProtoCalendarEvent
import com.meeting.ProtoUserCalendarEvents
import com.meeting.ProtoUserCalendarEventsWithSuggestionsResponse
import com.meeting.util.datetime.toProtoDateTime
import com.meeting.util.datetime.toZonedDateTime

/**
 * Converts [CalendarEvent] to [ProtoCalendarEvent].
 */
fun CalendarEvent.toProtoCalendarEvent(): ProtoCalendarEvent = ProtoCalendarEvent.newBuilder()
    .apply { startDateTime = this@toProtoCalendarEvent.startDateTime.toProtoDateTime() }
    .apply { endDateTime = this@toProtoCalendarEvent.endDateTime.toProtoDateTime() }
    .build()

/**
 * Converts [ProtoCalendarEvent] to [CalendarEvent].
 */
fun ProtoCalendarEvent.toCalendarEvent() =
    CalendarEvent(this.startDateTime.toZonedDateTime(), this.endDateTime.toZonedDateTime())

/**
 * Converts [UserCalendarEvents] to [ProtoUserCalendarEvents].
 */
fun UserCalendarEvents.toProtoUserCalendarEvents(): ProtoUserCalendarEvents = ProtoUserCalendarEvents.newBuilder()
    .apply { calendarId = this@toProtoUserCalendarEvents.calendarId }
    .addAllCalendarEvents(this@toProtoUserCalendarEvents.calendarEvents.map { it.toProtoCalendarEvent() })
    .build()

/**
 * Converts [ProtoUserCalendarEvents] to [UserCalendarEvents].
 */
fun ProtoUserCalendarEvents.toUserCalendarEvents() =
    UserCalendarEvents(this.calendarId, this.calendarEventsList.map { it.toCalendarEvent() })

/**
 * Converts [UserCalendarEventsWithSuggestions] to [ProtoUserCalendarEventsWithSuggestionsResponse].
 */
fun UserCalendarEventsWithSuggestions.toProtoUserCalendarEventsWithSuggestionsResponse():
    ProtoUserCalendarEventsWithSuggestionsResponse =
    ProtoUserCalendarEventsWithSuggestionsResponse.newBuilder()
        .addAllUserCalendars(this.userCalendarEvents.map { it.toProtoUserCalendarEvents() })
        .addAllSuggestions(this.suggestions.map { it.toProtoCalendarEvent() })
        .build()

/**
 * Converts [ProtoUserCalendarEventsWithSuggestionsResponse] to [UserCalendarEventsWithSuggestions].
 */
fun ProtoUserCalendarEventsWithSuggestionsResponse.toUserCalendarEventsWithSuggestions() =
    UserCalendarEventsWithSuggestions(
        this.userCalendarsList.map { it.toUserCalendarEvents() },
        this.suggestionsList.map { it.toCalendarEvent() }
    )
