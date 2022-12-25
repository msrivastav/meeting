package com.meeting.google.endpoint

import com.meeting.common.calendar.CalendarEvent
import com.meeting.google.calendar.GoogleCalendarService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("debugger")
class GoogleConnectorServiceDebuggerEndpoint(val service: GoogleCalendarService) {

    // startDate format: 2007-12-03
    @GetMapping("/get-schedule/{orgId}/{calendarId}/{startDate}", produces = ["application/json"])
    fun getSchedule(
        @PathVariable orgId: Int,
        @PathVariable calendarId: String,
        @PathVariable startDate: LocalDate): List<CalendarEvent> {
        return service.getUserCalendarSchedule(orgId, calendarId, startDate)
    }
}