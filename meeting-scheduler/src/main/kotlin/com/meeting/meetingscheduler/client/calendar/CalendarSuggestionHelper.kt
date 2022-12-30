package com.meeting.meetingscheduler.client.calendar

import com.meeting.ProtoUserCalendarEvents

class CalendarSuggestionHelper {

    companion object {

        /**
         * Provides possible meeting slot suggestions that are suitable for all users.
         *
         * @param userCalendars A set containing calendar events of all users.
         *
         * @return Proto containing list of slots that can be suggested to all users.
         */
        fun getMeetingTimeslotSuggestions(userCalendars: Set<ProtoUserCalendarEvents>): ProtoUserCalendarEvents {
            return ProtoUserCalendarEvents.getDefaultInstance()
        }
    }
}