package com.meeting.common.calendar

import java.time.ZonedDateTime

data class CalendarEvent(val startDateTime: ZonedDateTime, val endDateTime: ZonedDateTime)

/**
 * All calendar events for one calendar id.
 */
data class UserCalendarEvents(val calendarId: String, val calendarEvents: List<CalendarEvent>)

/**
 * Calendar events for all calendar ids, and list of possible overlapping free timeslots.
 */
data class UserCalendarEventsWithSuggestions(
    val userCalendarEvents: List<UserCalendarEvents>,
    val suggestions: List<CalendarEvent>
)
