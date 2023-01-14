package com.meeting.google.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.meeting.common.calendar.CalendarEvent
import com.meeting.common.calendar.CalendarService
import com.meeting.google.auth.CredentialsProvider
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.*
import java.util.*

@Service
class CalendarServiceImpl(
    @Value("\${meeting.google.application-name}") private val applicationName: String,
    private val credentialsProvider: CredentialsProvider,
    meterRegistry: MeterRegistry
) : CalendarService {

    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val millisInADay = Duration.ofDays(1).toMillis()
    private val numberOfEventsInCalendarDistribution: DistributionSummary

    init {
        numberOfEventsInCalendarDistribution =
            DistributionSummary.builder("connector.google.events.per.calendar").publishPercentileHistogram()
                .percentilePrecision(3).publishPercentiles(.5, .75, .90, .95, .99, .999).register(meterRegistry)
    }

    override fun getUserCalendarSchedule(
        orgId: Int,
        calendarId: String,
        startDate: LocalDate,
        fetchDaysBefore: Int,
        fetchDaysAfter: Int
    ): List<CalendarEvent> {
        val credentials = credentialsProvider.getCredentialsForClientOrg(orgId)

        val service: Calendar = Calendar.Builder(httpTransport, jsonFactory, credentials)
            .apply { applicationName = this@CalendarServiceImpl.applicationName }.build()

        val startDateTime = startDate.minusDays(fetchDaysBefore.toLong()).toEpochDay() * millisInADay
        val endDateTime = startDate.plusDays(fetchDaysAfter.toLong()).toEpochDay() * millisInADay

        val events = try {
            service.events().list(calendarId).apply {
                timeMin = DateTime(startDateTime)
                timeMax = DateTime(endDateTime)
            }
                .execute().items
                .map(::convertToCalendarEvent)
                .also {
                    numberOfEventsInCalendarDistribution.record(it.size.toDouble())
                }
        } catch (e: Exception) {
            log.error(
                "Could not retrieve calendar for org: " +
                    "$orgId, calendarId: $calendarId, startDate: $startDate due to: $e"
            )
            emptyList()
        }

        if (events.isNotEmpty()) log.debug(
            "Calendar event for org:" +
                " $orgId, calendarId: $calendarId, startDate: $startDate are: $events"
        )

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
