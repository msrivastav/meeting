package com.meeting.common.calendar

import java.time.LocalDate

interface CalendarService {

    /**
     * Converts and returns the calendar entry for given client organisation and user id.
     * Returns the calendar from start date minus seven days to start date plus fourteen days.
     */
    fun getUserCalendarSchedule(
        orgId: Int,
        userId: String,
        startDate: LocalDate,
        fetchDaysBefore: Int,
        fetchDaysAfter: Int
    ): List<CalendarEvent>
}
