package com.meeting.meetingscheduler.client.calendar

import com.meeting.common.calendar.CalendarEvent
import java.time.Duration
import java.time.ZonedDateTime

class CalendarSuggestionHelper {

    companion object {

        /**
         * Provides possible meeting slot suggestions that are suitable for all users.
         *
         * @param userCalendars A set containing calendar events of all users.
         * @param duration Duration of the meeting. 0 means all possible durations.
         *
         * @return Proto containing list of slots that can be suggested to all users.
         */
        fun getMeetingTimeslotSuggestions(
            userCalendars: List<CalendarEvent>,
            duration: Duration,
            meetingStartDate: ZonedDateTime,
            meetingMaxDate: ZonedDateTime
        ): List<CalendarEvent> {
            if (userCalendars.isEmpty()) return emptyList()

            // sorting the entries in ascending order of date time
            val sortedEvents = userCalendars.sortedBy { it.startDateTime }

            // Getting unique meeting blocks
            val uniqueMeetingBlocks = resolveOverlappingMeetings(sortedEvents)

            // Creating the list of the largest possible meeting slots that are available to all users
            // With given max date limit.
            val largestAvailableSlots = findLargestAvailableSlots(uniqueMeetingBlocks, meetingStartDate, meetingMaxDate)

            // Filtering out the slots that are smaller than given duration
            val meetingBlocksOfValidMinDuration =
                if (duration.isZero) largestAvailableSlots
                else largestAvailableSlots.filter {
                    !Duration.between(it.endDateTime, it.startDateTime).minus(duration).isNegative
                }

            return emptyList()
        }

        /**
         * Combines all overlapping meetings and returns a list of unique meetings that cover all
         * input meetings.
         *
         * Example: A(9:00 - 10:00), B(9:30 - 10:30), c(4:00 - 5:00) -> A(9:00 - 10:30), B(4:00 - 5:00)
         *
         * @param input A list of meeting slots sorted in ascending order of start time.
         *
         * @return A list of [CalendarEvent] that do not contain any overlapping meeting.
         *          All overlapping meetings are combined in one slot. Returned list is sorted
         *          based on ascending order of start time
         */
        fun resolveOverlappingMeetings(input: List<CalendarEvent>): List<CalendarEvent> {
            if (input.size == 1) return input

            var currentStartTime: ZonedDateTime = input.first().startDateTime
            var currentEndTime: ZonedDateTime = input.first().endDateTime

            val result = ArrayList<CalendarEvent>()
            for (i in 1 until input.size) {
                if (input[i].startDateTime.isAfter(currentEndTime)) {
                    // If the next meeting starts after the current meeting ends,
                    // then current meeting is unique
                    result.add(CalendarEvent(currentStartTime, currentEndTime))
                    currentStartTime = input[i].startDateTime
                    currentEndTime = input[i].endDateTime
                } else {
                    // This condition will be reached if next meeting start time is either
                    // before or at same time as current meeting end time
                    // It means that current and next meeting are overlapping, and should be combined
                    // setting the current end time to larger of end times of both overlapping meetings
                    currentEndTime = if (input[i].endDateTime.isAfter(currentEndTime)) {
                        input[i].endDateTime
                    } else currentEndTime
                }
            }
            result.add(CalendarEvent(currentStartTime, currentEndTime))

            return result
        }

        /**
         * Returns the largest blocks of available meeting slots within given time date range.
         *
         * @param uniqueMeetingBlocks A list of unique meeting slots sorted in ascending order of start time.
         */
        fun findLargestAvailableSlots(
            uniqueMeetingBlocks: List<CalendarEvent>,
            meetingStartDate: ZonedDateTime,
            meetingMaxDate: ZonedDateTime
        ): List<CalendarEvent> {
            val firstMeetingStartDate = uniqueMeetingBlocks.first().startDateTime
            var currentStartTime: ZonedDateTime = meetingStartDate
            var currentEndTime: ZonedDateTime = meetingStartDate

            val result = ArrayList<CalendarEvent>()

            for (slot in uniqueMeetingBlocks) {
                if (!slot.startDateTime.isBefore(meetingMaxDate)) {
                    currentEndTime = meetingMaxDate
                    break
                }

                result.add(CalendarEvent(currentStartTime, slot.startDateTime))

            }

            result.add(CalendarEvent(currentStartTime, currentEndTime))
            return emptyList()
        }
    }
}
