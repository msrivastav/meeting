package com.meeting.meetingscheduler.endpoint

import com.meeting.ProtoUserCalendarsAndSuggestionsRequest
import com.meeting.ProtoUserCalendarsAndSuggestionsResponse
import com.meeting.UserCalendarsAndSuggestionsServiceGrpcKt.UserCalendarsAndSuggestionsServiceCoroutineImplBase
import com.meeting.meetingscheduler.client.calendar.CalendarSuggestionService
import com.meeting.util.datetime.toLocalDate
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class CalendarAndSuggestionsGrpcEndpoint(private val calendarSuggestionService: CalendarSuggestionService) :
    UserCalendarsAndSuggestionsServiceCoroutineImplBase() {

    override suspend fun getUserCalendarAndSuggestions(request: ProtoUserCalendarsAndSuggestionsRequest):
            ProtoUserCalendarsAndSuggestionsResponse {

        return calendarSuggestionService.getUserCalendarsAndSuggestions(
            request.orgId,
            request.calendarIdList.toList(),
            request.startDate.toLocalDate()
        )
    }
}