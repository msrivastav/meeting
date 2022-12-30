package com.meeting.meetingscheduler.client.calendar

import com.meeting.ProtoUserCalendarRequest
import com.meeting.ProtoUserCalendarsAndSuggestionsResponse
import com.meeting.common.datastore.OrgConfigStore
import com.meeting.common.exception.OrgApplicationTypeConfigNotFoundException
import com.meeting.common.type.ApplicationType
import com.meeting.util.datetime.toProtoDate
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
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
        startDate: LocalDate
    ): ProtoUserCalendarsAndSuggestionsResponse {

        val calendarProviderId = getCalendarProviderId(orgId)

        val clientForCalendarConnector = calendarConnectorProviderResolver.getGrpcClientForProvider(calendarProviderId)

        val connectorRequest = ProtoUserCalendarRequest.newBuilder()
            .apply { this.orgId = orgId }
            .apply { this.startDate = startDate.toProtoDate() }
            .apply { fetchDaysBefore = this@CalendarSuggestionService.fetchDaysBefore }
            .apply { fetchDaysAfter = this@CalendarSuggestionService.fetchDaysAfter }
            .addAllCalendarId(calendarIds)
            .build()

        val userCalendarResponse = runBlocking { clientForCalendarConnector.getUserCalendar(connectorRequest) }

        val suggestions = CalendarSuggestionHelper.getMeetingTimeslotSuggestions(
            userCalendarResponse.userCalendarsMap.values.toSet()
        )

        responseSize.increment(userCalendarResponse.userCalendarsCount.toDouble())
        responseSize.increment(suggestions.calendarEventsCount.toDouble())

        return ProtoUserCalendarsAndSuggestionsResponse.newBuilder()
            .apply { this.userCalendarResponse = userCalendarResponse }
            .apply { this.suggestions = suggestions }
            .build()
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