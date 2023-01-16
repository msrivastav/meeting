package com.meeting.ui.datastore

import com.meeting.common.routing.HostPort
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.concurrent.ConcurrentHashMap

@ConfigurationProperties(prefix = "meeting")
class ApplicationEndpointsStore(private val applicationEndpoints: ConcurrentHashMap<String, HostPort>) {
    fun getHostPort(application: String) = applicationEndpoints[application]
}
