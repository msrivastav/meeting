package com.meeting.zoom

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.meeting"])
class ZoomConnectorApplication

fun main(args: Array<String>) {
    runApplication<ZoomConnectorApplication>(*args)
}
