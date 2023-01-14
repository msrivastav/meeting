package com.meeting.google.endpoint

import com.meeting.common.calendar.CalendarEvent
import com.meeting.common.calendar.CalendarService
import com.meeting.common.directory.DirectoryEntry
import com.meeting.common.directory.DirectoryService
import com.meeting.google.repository.DirectoryRepository
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("debugger")
class DebuggerEndpoint(
    private val calendarService: CalendarService,
    private val directoryService: DirectoryService,
    private val directoryRepository: DirectoryRepository
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
            responseMap[it] =
                calendarService.getUserCalendarSchedule(orgId, it, startDate, fetchDaysBefore, fetchDaysAfter)
        }

        return responseMap
    }

    @GetMapping(
        "/get-directory/{orgId}/{namePart}/{withCache}",
        produces = ["application/json"]
    )
    @ResponseBody
    fun getDirectoryWithCache(
        @PathVariable orgId: Int,
        @PathVariable namePart: String,
        @PathVariable withCache: Boolean
    ): List<DirectoryEntry> {
        return if (withCache) {
            directoryService.getUserRecommendation(orgId, namePart, 5)
        } else {
            directoryRepository.getOrgDirectory(orgId)
                .filter { it.givenName.contains(namePart) || it.familyName.contains(namePart) }
        }
    }
}
