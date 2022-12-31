package com.meeting.google.endpoint

import com.meeting.ProtoCalendarEvent
import com.meeting.ProtoUserCalendarEvents
import com.meeting.ProtoUserCalendarRequest
import com.meeting.ProtoUserCalendarResponse
import com.meeting.UserCalendarServiceGrpcKt.UserCalendarServiceCoroutineImplBase
import com.meeting.common.calendar.CalendarService
import com.meeting.util.datetime.toProtoDateTime
import com.meeting.util.datetime.toZonedDateTime
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class GoogleConnectorGrpcEndpoint(private val service: CalendarService) :
    UserCalendarServiceCoroutineImplBase() {

    override suspend fun getUserCalendar(request: ProtoUserCalendarRequest):
        ProtoUserCalendarResponse {
        val responseBuilder = ProtoUserCalendarResponse.newBuilder()

        for (calendarId in request.calendarIdList) {
            val userCalendarEventsBuilder = ProtoUserCalendarEvents.newBuilder()
                .apply { this.calendarId = calendarId }

            service.getUserCalendarSchedule(
                request.orgId,
                calendarId,
                request.startDate.toZonedDateTime().toLocalDate(),
                request.fetchDaysBefore,
                request.fetchDaysAfter
            )
                .map {
                    ProtoCalendarEvent.newBuilder()
                        .apply { startDateTime = it.startDateTime.toProtoDateTime() }
                        .apply { endDateTime = it.endDateTime.toProtoDateTime() }
                        .build()
                }
                .forEach { userCalendarEventsBuilder.addCalendarEvents(it) }

            responseBuilder.addUserCalendars(userCalendarEventsBuilder.build())
        }

        return responseBuilder.build()
    }
}
