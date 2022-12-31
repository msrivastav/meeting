package com.meeting.meetingscheduler.endpoint

import com.meeting.meetingscheduler.client.calendar.CalendarSuggestionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.ZonedDateTime

@RestController
@RequestMapping("debugger")
class CalendarAndSuggestionsDebuggerEndpoint(private val service: CalendarSuggestionService) {

    @GetMapping(
        "/get-calendar-and-suggestions/{orgId}/{calendarIds}/{startDate}/{duration}",
        produces = ["application/json"]
    )
    fun getCalendarAndSuggestions(
        @PathVariable orgId: Int,
        @PathVariable calendarIds: List<String>,
        @PathVariable startDate: ZonedDateTime,
        @PathVariable duration: Duration
    ) = service.getUserCalendarsAndSuggestions(orgId, calendarIds, startDate, duration)
}
