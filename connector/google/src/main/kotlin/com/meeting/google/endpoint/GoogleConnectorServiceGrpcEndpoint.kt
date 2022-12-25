package com.meeting.google.endpoint

import com.meeting.ProtoCalendarEvent
import com.meeting.ProtoUserCalendarScheduleRequest
import com.meeting.ProtoUserCalendarScheduleResponse
import com.meeting.UserCalendarServiceGrpcKt.UserCalendarServiceCoroutineImplBase
import com.meeting.google.calendar.GoogleCalendarService
import org.lognet.springboot.grpc.GRpcService
import java.time.LocalDate

@GRpcService
class GoogleConnectorServiceGrpcEndpoint(val service: GoogleCalendarService) :
    UserCalendarServiceCoroutineImplBase() {

    override suspend fun getUserCalendarSchedule(request: ProtoUserCalendarScheduleRequest):
            ProtoUserCalendarScheduleResponse {

        val startDate = LocalDate.parse(request.startDate)

        val responseBuilder = ProtoUserCalendarScheduleResponse.newBuilder()

        service.getUserCalendarSchedule(request.orgId, request.calendarId, startDate)
            .map {
                ProtoCalendarEvent.newBuilder().setStartDateTime(it.startDateTime.toString())
                    .setEndDateTime(it.endDateTime.toString())
            }
            .forEach { responseBuilder.addCalendarEvents(it) }

        return responseBuilder.build()

    }
}