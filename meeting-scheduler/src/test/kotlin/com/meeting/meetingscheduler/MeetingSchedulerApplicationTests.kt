package com.meeting.meetingscheduler

import io.prometheus.client.CollectorRegistry
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class MeetingSchedulerApplicationTests {

    @MockBean
    private lateinit var collectorRegistry: CollectorRegistry

    @Test
    fun contextLoads() {
    }
}
