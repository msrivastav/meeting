package com.meeting.outlook

import io.prometheus.client.CollectorRegistry
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class OutlookConnectorApplicationTests {

    @MockBean
    private lateinit var collectorRegistry : CollectorRegistry

    @Test
    fun contextLoads() {
    }

}
