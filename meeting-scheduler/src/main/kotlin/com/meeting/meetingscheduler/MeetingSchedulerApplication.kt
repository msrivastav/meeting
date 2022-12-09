package com.meeting.meetingscheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MeetingSchedulerApplication

fun main(args: Array<String>) {
    runApplication<MeetingSchedulerApplication>(*args)
}
