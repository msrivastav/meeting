package com.meeting.meetingscheduler.helper

import com.meeting.common.calendar.CalendarEvent
import java.time.Duration
import java.time.ZonedDateTime

class CalendarSuggestionHelper {

    companion object {
        private val MIN_60 = Duration.ofHours(1)
        private val MIN_30 = Duration.ofMinutes(30)
        private val MIN_15 = Duration.ofMinutes(15)

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

            val availableSlots = userCalendars
                // sorting the entries in ascending order of date time
                .sortedBy { it.startDateTime }
                .let {
                    // Getting unique meeting blocks
                    resolveOverlappingMeetings(it)
                }
                .let {
                    // Creating the list of the largest possible meeting slots that are available to all users
                    // With given max date limit.
                    findLargestAvailableSlots(it, meetingStartDate, meetingMaxDate)
                }

            /*
             * For final suggestion list pick following:
             * 1. Two earliest slots of given duration with half hour gap
             * 2. One slot of an hour
             * 3. Two slots of half hour with fifteen minutes gap
             *
             * If duration is not specified, then following:
             * 1. Two slots of one hour with half hour gap
             * 2. Three slots of half an hour with fifteen minutes gap
             */
            val result = ArrayList<CalendarEvent>()
            if (duration.isZero) {
                result.addAll(findSlotsOfDurationAndGap(availableSlots, MIN_60, 2, MIN_30))
                result.addAll(findSlotsOfDurationAndGap(availableSlots, MIN_30, 3, MIN_15))
            } else {
                result.addAll(findSlotsOfDurationAndGap(availableSlots, duration, 2, MIN_30))
                result.addAll(findSlotsOfDurationAndGap(availableSlots, MIN_60, 1, MIN_30))
                result.addAll(findSlotsOfDurationAndGap(availableSlots, MIN_30, 2, MIN_15))
            }

            return result
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
            var currentStartTime: ZonedDateTime = meetingStartDate

            val result = ArrayList<CalendarEvent>()

            for (slot in uniqueMeetingBlocks) {
                if (!slot.startDateTime.isAfter(meetingStartDate)) {
                    // If the meeting start time is either before or on meeting start time set by user,
                    // then skip that meeting.
                    currentStartTime = slot.endDateTime
                    continue
                }

                if (!slot.startDateTime.isBefore(meetingMaxDate)) {
                    // If the meeting start time is after meeting max date then the
                    // last open slot ends at meeting max meeting time
                    result.add(CalendarEvent(currentStartTime, meetingMaxDate))
                    break
                }

                result.add(CalendarEvent(currentStartTime, slot.startDateTime))

                currentStartTime = slot.endDateTime
            }

            return result
        }

        /**
         * Finds the slots of given duration with specific gap interval.
         *
         * @param largestAvailableSlots List of largest available open slots.
         * @param duration Duration of each meeting slot.
         * @param numberOfSlots Total number of slots of given duration if possible.
         * @param gapInterval Gap interval between two slots.
         *
         * @return A list of possible open slots of given duration and gap.
         */
        fun findSlotsOfDurationAndGap(
            largestAvailableSlots: List<CalendarEvent>,
            duration: Duration,
            numberOfSlots: Int,
            gapInterval: Duration
        ): List<CalendarEvent> {
            // Filter out smaller slots than given duration.
            val fittingSlots = largestAvailableSlots.filter {
                !Duration.between(it.endDateTime, it.startDateTime).minus(duration).isNegative
            }

            val result = ArrayList<CalendarEvent>()
            var currentSlotNumber: Int = 0

            for (aSlot in fittingSlots) {
                var start = aSlot.startDateTime
                var end = aSlot.startDateTime.plus(duration)

                while (start.isBefore(aSlot.endDateTime) && !end.isAfter(aSlot.endDateTime) && currentSlotNumber < numberOfSlots) {
                    result.add(CalendarEvent(start, aSlot.endDateTime))

                    start = end.plus(gapInterval)
                    end = start.plus(duration)
                    currentSlotNumber++
                }
            }

            return result
        }
    }
}
