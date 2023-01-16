package com.meeting.google.endpoint.grpc

import com.google.rpc.Code
import com.meeting.ProtoCalendarEvent
import com.meeting.ProtoUserCalendarEvents
import com.meeting.ProtoUserCalendarRequest
import com.meeting.ProtoUserCalendarResponse
import com.meeting.UserCalendarServiceGrpcKt.UserCalendarServiceCoroutineImplBase
import com.meeting.common.calendar.CalendarService
import com.meeting.common.exception.getStatusException
import com.meeting.common.type.ApplicationType
import com.meeting.util.datetime.toProtoDateTime
import com.meeting.util.datetime.toZonedDateTime
import io.micrometer.core.instrument.MeterRegistry
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory

@GRpcService
class CalendarGrpcEndpoint(
    private val service: CalendarService,
    private val meterRegistry: MeterRegistry
) : UserCalendarServiceCoroutineImplBase() {

    private val fetchFailedCounterName = "com.meeting.google.calendar.fetch.failed"
    override suspend fun getUserCalendar(request: ProtoUserCalendarRequest): ProtoUserCalendarResponse {
        try {
            val responseBuilder = ProtoUserCalendarResponse.newBuilder()

            for (userId in request.userIdList) {
                val userCalendarEventsBuilder =
                    ProtoUserCalendarEvents.newBuilder().apply { this.userId = userId }

                service.getUserCalendarSchedule(
                    request.orgId,
                    userId,
                    request.startDate.toZonedDateTime().toLocalDate(),
                    request.fetchDaysBefore,
                    request.fetchDaysAfter
                ).map {
                    ProtoCalendarEvent.newBuilder().apply {
                        startDateTime = it.startDateTime.toProtoDateTime()
                        endDateTime = it.endDateTime.toProtoDateTime()
                    }.build()
                }.forEach { userCalendarEventsBuilder.addCalendarEvents(it) }

                responseBuilder.addUserCalendars(userCalendarEventsBuilder.build())
            }

            return responseBuilder.build()
        } catch (e: Exception) {
            log.error("Exception while fetching user calendar: $e")
            meterRegistry.counter(fetchFailedCounterName, "org", request.orgId.toString()).increment()
            throw getStatusException(e, Code.CANCELLED, ApplicationType.CALENDAR.toString())
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
