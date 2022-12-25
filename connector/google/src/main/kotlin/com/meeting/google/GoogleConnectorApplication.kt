package com.meeting.google

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.meeting"])
class GoogleConnectorApplication

fun main(args: Array<String>) {
    runApplication<GoogleConnectorApplication>(*args)
}
