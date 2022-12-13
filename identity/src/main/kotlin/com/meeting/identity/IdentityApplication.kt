package com.meeting.identity

import com.meeting.util.UtilBaseConfigurations

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [UtilBaseConfigurations::class])
class IdentityApplication

fun main(args: Array<String>) {
    runApplication<IdentityApplication>(*args)
}
