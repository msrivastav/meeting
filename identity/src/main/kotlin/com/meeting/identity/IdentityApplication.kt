package com.meeting.identity

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.meeting"])
class IdentityApplication

fun main(args: Array<String>) {
    runApplication<IdentityApplication>(*args)
}
