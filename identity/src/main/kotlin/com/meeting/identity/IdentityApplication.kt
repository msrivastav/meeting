package com.meeting.identity

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.meeting"])
@ConfigurationPropertiesScan
class IdentityApplication

fun main(args: Array<String>) {
    runApplication<IdentityApplication>(*args)
}
