package com.meeting.meetingscheduler.endpoint.grpc

import com.google.rpc.Code
import com.meeting.ProtoUserCalendarEventsWithSuggestionsResponse
import com.meeting.ProtoUserCalendarsAndSuggestionsRequest
import com.meeting.UserCalendarsAndSuggestionsServiceGrpcKt.UserCalendarsAndSuggestionsServiceCoroutineImplBase
import com.meeting.common.calendar.toProtoUserCalendarEventsWithSuggestionsResponse
import com.meeting.common.exception.getStatusException
import com.meeting.common.type.ApplicationType
import com.meeting.meetingscheduler.client.calendar.CalendarSuggestionService
import com.meeting.util.datetime.toDuration
import com.meeting.util.datetime.toZonedDateTime
import io.micrometer.core.instrument.MeterRegistry
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory
import java.time.Duration

@GRpcService
class CalendarGrpcEndpoint(
    private val calendarSuggestionService: CalendarSuggestionService,
    private val meterRegistry: MeterRegistry
) :
    UserCalendarsAndSuggestionsServiceCoroutineImplBase() {

    private val fetchFailedCounterName = "com.meeting.meetingscheduler.calendar.fetch.failed"

    override suspend fun getUserCalendarAndSuggestions(request: ProtoUserCalendarsAndSuggestionsRequest):
        ProtoUserCalendarEventsWithSuggestionsResponse {
        return try {
            calendarSuggestionService.getUserCalendarsAndSuggestions(
                request.orgId,
                request.calendarIdList.toList(),
                request.startDate.toZonedDateTime(),
                if (request.hasDuration()) request.duration.toDuration() else Duration.ofSeconds(0)
            ).toProtoUserCalendarEventsWithSuggestionsResponse()
        } catch (e: Exception) {
            log.error("Exception while fetching user recommendation: $e")
            meterRegistry.counter(fetchFailedCounterName, "org", request.orgId.toString()).increment()
            throw getStatusException(e, Code.CANCELLED, ApplicationType.CALENDAR.toString())
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
