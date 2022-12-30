package com.meeting.meetingscheduler.datastore

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.concurrent.ConcurrentHashMap

@ConfigurationProperties(prefix = "meeting")
class ConnectorEndpointsStore(private val connectorEndpoints: ConcurrentHashMap<Int, HostPort>) {
    fun getHostPort(providerId: Int) = connectorEndpoints[providerId]
}


data class HostPort(val host: String, val port: Int)