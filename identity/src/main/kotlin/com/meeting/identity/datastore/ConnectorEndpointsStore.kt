package com.meeting.identity.datastore

import com.meeting.common.routing.HostPort
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.concurrent.ConcurrentHashMap

@ConfigurationProperties(prefix = "meeting")
class ConnectorEndpointsStore(private val connectorEndpoints: ConcurrentHashMap<Int, HostPort>) {
    fun getHostPort(providerId: Int) = connectorEndpoints[providerId]
}
