package com.meeting.ui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class UiApplication

fun main(args: Array<String>) {
    runApplication<UiApplication>(*args)
}
