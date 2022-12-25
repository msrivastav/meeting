package com.meeting.outlook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.meeting"])
class OutlookConnectorApplication

fun main(args: Array<String>) {
    runApplication<OutlookConnectorApplication>(*args)
}
