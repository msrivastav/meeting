package com.meeting.meetingscheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MeetingSchedulerApplication

fun main(args: Array<String>) {
    runApplication<MeetingSchedulerApplication>(*args)
}
