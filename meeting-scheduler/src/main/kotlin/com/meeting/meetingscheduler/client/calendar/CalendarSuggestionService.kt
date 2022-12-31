package com.meeting.meetingscheduler.client.calendar

import com.meeting.ProtoUserCalendarRequest
import com.meeting.common.calendar.UserCalendarEventsWithSuggestions
import com.meeting.common.calendar.toUserCalendarEvents
import com.meeting.common.datastore.OrgConfigStore
import com.meeting.common.exception.OrgApplicationTypeConfigNotFoundException
import com.meeting.common.type.ApplicationType
import com.meeting.util.datetime.toProtoDateTime
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Provides user calendar and recommendations.
 */
@Service
class CalendarSuggestionService(
    private val orgConfigStore: OrgConfigStore,
    private val calendarConnectorProviderResolver: CalendarConnectorProviderResolver,
    @Value("\${meeting.calendar.fetch-days-before}") private val fetchDaysBefore: Int,
    @Value("\${meeting.calendar.fetch-days-after}") private val fetchDaysAfter: Int,
    meterRegistry: MeterRegistry
) {

    private val orgToCalendarProvider = ConcurrentHashMap<Int, Int>()
    private val responseSize: Counter
    private val suggestionSize: Counter

    init {
        responseSize = Counter.builder("meetingscheduler.response.size").register(meterRegistry)
        suggestionSize = Counter.builder("meetingscheduler.suggestion.size").register(meterRegistry)
    }

    fun getUserCalendarsAndSuggestions(
        orgId: Int,
        calendarIds: List<String>,
        startDate: ZonedDateTime,
        duration: Duration
    ): UserCalendarEventsWithSuggestions {
        val calendarProviderId = getCalendarProviderId(orgId)

        val clientForCalendarConnector = calendarConnectorProviderResolver.getGrpcClientForProvider(calendarProviderId)

        val connectorRequest = ProtoUserCalendarRequest.newBuilder()
            .apply { this.orgId = orgId }
            .apply { this.startDate = startDate.toProtoDateTime() }
            .apply { fetchDaysBefore = this@CalendarSuggestionService.fetchDaysBefore }
            .apply { fetchDaysAfter = this@CalendarSuggestionService.fetchDaysAfter }
            .addAllCalendarId(calendarIds)
            .build()

        val userCalendarEvents =
            runBlocking {
                clientForCalendarConnector.getUserCalendar(connectorRequest)
                    .userCalendarsList
                    .map { it.toUserCalendarEvents() }
            }

        val suggestions = CalendarSuggestionHelper.getMeetingTimeslotSuggestions(
            // For suggestions, consider only the meetings for which end time is after the given start time,
            // because user only needs suggestions for a new meeting it is trying to create
            userCalendarEvents.flatMap { it.calendarEvents }.filter { it.endDateTime.isAfter(startDate) },
            duration,
            startDate.plusDays(fetchDaysAfter.toLong())
        )

        responseSize.increment(userCalendarEvents.size.toDouble())
        responseSize.increment(suggestions.size.toDouble())

        return UserCalendarEventsWithSuggestions(userCalendarEvents, suggestions)
    }

    private fun getCalendarProviderId(orgId: Int): Int {
        return orgToCalendarProvider.computeIfAbsent(orgId) {
            val providerConfig = orgConfigStore.getOrgConfig(orgId)
            if (providerConfig.isEmpty()) {
                throw OrgApplicationTypeConfigNotFoundException(orgId, ApplicationType.CALENDAR)
            }
            providerConfig.entries
                .first { it.value.appType == ApplicationType.CALENDAR }
                .key
        }
    }
}
