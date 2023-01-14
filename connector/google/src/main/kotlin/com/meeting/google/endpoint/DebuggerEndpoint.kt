package com.meeting.google.endpoint

import com.meeting.common.calendar.CalendarEvent
import com.meeting.common.calendar.CalendarService
import com.meeting.common.directory.DirectoryEntry
import com.meeting.common.directory.DirectoryService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("debugger")
class DebuggerEndpoint(
    private val service: CalendarService,
    private val directoryService: DirectoryService
) {

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

    @GetMapping(
        "/get-directory/{orgId}",
        produces = ["application/json"]
    )
    @ResponseBody
    fun getSchedule(
        @PathVariable orgId: Int
    ): List<DirectoryEntry> {
        return directoryService.getDirectory(orgId)
    }
}
