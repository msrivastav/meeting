package com.meeting.ui.service

import com.meeting.ProtoUserCalendarsAndSuggestionsRequest
import com.meeting.common.calendar.*
import com.meeting.ui.client.ApplicationProviderResolver
import com.meeting.util.datetime.toProtoDateTime
import com.meeting.util.datetime.toProtoDuration
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZonedDateTime

@Service
class CalendarProviderService(
    private val applicationProviderResolver: ApplicationProviderResolver
) {

    private val stub = applicationProviderResolver.getGrpcClientForMeetingSchedulerApplication()

    /**
     * Provides the user calendar for today.
     */
    fun getUserCalendarForToday(orgId: Int, userId: String, startDateTime: ZonedDateTime): CalendarEvents {
        return try {
            ProtoUserCalendarsAndSuggestionsRequest.newBuilder()
                .apply {
                    this.orgId = orgId
                    this.startDate = startDateTime.toProtoDateTime()
                }.addUserId(userId).build()
                .let {
                    runBlocking {
                        stub.getUserCalendarAndSuggestions(it).userCalendarsList
                            .map { it.toUserCalendarEvents() }
                            .map { keepEventsOfStartDate(it, startDateTime) }
                            .first { it.userId == userId }
                    }
                }
        } catch (e: StatusRuntimeException) {
            log.error("Failed to get user calendar for today for org: $orgId, user id: $userId with reason: $e")
            CalendarEvents(userId, emptyList())
        }
    }

    /**
     * Provides user calendar and suggestion.
     */
    fun getUserCalendarAndSuggestion(
        orgId: Int,
        userIds: List<String>,
        startDateTime: ZonedDateTime,
        duration: Duration = Duration.ZERO
    ): CalendarEventsWithSuggestions {
        val sourceZone = startDateTime.zone

        return try {
            ProtoUserCalendarsAndSuggestionsRequest.newBuilder()
                .apply {
                    this.orgId = orgId
                    this.startDate = startDateTime.toProtoDateTime()
                    this.duration = duration.toProtoDuration()
                }.addAllUserId(userIds).build()
                .let {
                    runBlocking {
                        stub.getUserCalendarAndSuggestions(it)
                            .toUserCalendarEventsWithSuggestions() shiftToZone sourceZone
                    }
                }
        } catch (e: StatusRuntimeException) {
            log.error("Failed to get user calendar and suggestion for org: $orgId, user ids: $userIds with reason: $e")
            CalendarEventsWithSuggestions(emptyList(), emptyList())
        }
    }

    private fun keepEventsOfStartDate(events: CalendarEvents, startDateTime: ZonedDateTime): CalendarEvents {
        val lst: MutableList<CalendarEvent> = mutableListOf()

        events.calendarEvents
            .forEach {
                val zoneShiftedStartTime = it.startDateTime.withZoneSameInstant(startDateTime.zone)
                if (!zoneShiftedStartTime.isBefore(startDateTime)) lst.add(
                    CalendarEvent(
                        zoneShiftedStartTime,
                        it.endDateTime.withZoneSameInstant(startDateTime.zone)
                    )
                )
            }
        return CalendarEvents(events.userId, lst)
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
