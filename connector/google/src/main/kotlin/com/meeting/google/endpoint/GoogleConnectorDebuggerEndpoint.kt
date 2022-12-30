package com.meeting.google.endpoint

import com.meeting.common.calendar.CalendarEvent
import com.meeting.google.calendar.GoogleCalendarService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("debugger")
class GoogleConnectorDebuggerEndpoint(private val service: GoogleCalendarService) {

    // startDate format: 2007-12-03
    @GetMapping(
        "/get-calendar/{orgId}/{calendarIds}/{startDate}/{fetchDaysBefore}/{fetchDaysAfter}",
        produces = ["application/json"]
    )
    @ResponseBody
    fun getSchedule(
        @PathVariable orgId: Int,
        @PathVariable calendarIds: List<String>,
        @PathVariable startDate: LocalDate,
        @PathVariable fetchDaysBefore: Int,
        @PathVariable fetchDaysAfter: Int
    ): Map<String, List<CalendarEvent>> {

        val responseMap = HashMap<String, List<CalendarEvent>>()

        calendarIds.forEach {
            responseMap[it] = service.getUserCalendarSchedule(orgId, it, startDate, fetchDaysBefore, fetchDaysAfter)
        }

        return responseMap
    }
}