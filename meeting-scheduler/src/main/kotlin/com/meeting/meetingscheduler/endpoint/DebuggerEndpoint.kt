package com.meeting.meetingscheduler.endpoint

import com.meeting.meetingscheduler.service.CalendarService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.ZonedDateTime

@RestController
@RequestMapping("debugger")
class DebuggerEndpoint(private val service: CalendarService) {

    @GetMapping(
        "/get-calendar/{orgId}/{userIds}/{startDate}/{duration}",
        produces = ["application/json"]
    )
    fun getCalendarAndSuggestions(
        @PathVariable orgId: Int,
        @PathVariable userIds: List<String>,
        @PathVariable startDate: ZonedDateTime,
        @PathVariable duration: Duration
    ) = service.getUserCalendarsAndSuggestions(orgId, userIds, startDate, duration)
}
