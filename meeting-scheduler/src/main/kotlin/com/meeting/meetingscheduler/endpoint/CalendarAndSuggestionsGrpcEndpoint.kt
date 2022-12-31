package com.meeting.meetingscheduler.endpoint

import com.meeting.ProtoUserCalendarEventsWithSuggestionsResponse
import com.meeting.ProtoUserCalendarsAndSuggestionsRequest
import com.meeting.UserCalendarsAndSuggestionsServiceGrpcKt.UserCalendarsAndSuggestionsServiceCoroutineImplBase
import com.meeting.common.calendar.toProtoUserCalendarEventsWithSuggestionsResponse
import com.meeting.meetingscheduler.client.calendar.CalendarSuggestionService
import com.meeting.util.datetime.toDuration
import com.meeting.util.datetime.toZonedDateTime
import org.lognet.springboot.grpc.GRpcService
import java.time.Duration

@GRpcService
class CalendarAndSuggestionsGrpcEndpoint(private val calendarSuggestionService: CalendarSuggestionService) :
    UserCalendarsAndSuggestionsServiceCoroutineImplBase() {

    override suspend fun getUserCalendarAndSuggestions(request: ProtoUserCalendarsAndSuggestionsRequest):
        ProtoUserCalendarEventsWithSuggestionsResponse {
        return calendarSuggestionService.getUserCalendarsAndSuggestions(
            request.orgId,
            request.calendarIdList.toList(),
            request.startDate.toZonedDateTime(),
            if (request.hasDuration()) request.duration.toDuration() else Duration.ofSeconds(0)
        ).toProtoUserCalendarEventsWithSuggestionsResponse()
    }
}
