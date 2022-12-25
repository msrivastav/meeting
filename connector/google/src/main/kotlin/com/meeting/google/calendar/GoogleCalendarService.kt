package com.meeting.google.calendar

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.meeting.common.calendar.CalendarEvent
import com.meeting.common.calendar.CalendarService
import com.meeting.google.auth.CredentialsProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.*
import java.util.*

@Service
class GoogleCalendarService(
    @Value("\${meeting.calendar.application-name}") val calendarApplicationName: String,
    @Value("\${meeting.calendar.fetch-days-before}") val fetchDaysBefore: Long,
    @Value("\${meeting.calendar.fetch-days-after}") val fetchDaysAfter: Long,
    val credentialsProvider: CredentialsProvider
) : CalendarService {

    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val millisInADay = Duration.ofDays(1).toMillis()

    override fun getUserCalendarSchedule(
        orgId: Int,
        calendarId: String,
        startDate: LocalDate
    ): List<CalendarEvent> {

        val credentials = credentialsProvider.getCredentialsForClientOrg(orgId)

        val service: Calendar =
            Calendar.Builder(httpTransport, jsonFactory, credentials)
                .setApplicationName(calendarApplicationName)
                .build()

        val startDateTime = startDate.minusDays(fetchDaysBefore).toEpochDay() * millisInADay
        val endDateTime = startDate.plusDays(fetchDaysAfter).toEpochDay() * millisInADay

        val events = service.events().list(calendarId)
            .setTimeMin(DateTime(startDateTime))
            .setTimeMax(DateTime(endDateTime))
            .execute()
            .items
            .map { convertToCalendarEvent(it) }

        log.debug("Calendar event for org: $orgId, calendarId: $calendarId, startDate: $startDate are: $events")

        return events
    }

    private fun convertToCalendarEvent(event: Event): CalendarEvent {
        val startZonedTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(event.start.dateTime.value), ZoneId.of(event.start.timeZone))
        val endZonedTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(event.end.dateTime.value), ZoneId.of(event.end.timeZone))
        return CalendarEvent(startZonedTime, endZonedTime)
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}