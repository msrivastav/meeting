package com.meeting.google.config

import com.meeting.common.datastore.OrgConfigStore
import com.meeting.common.type.ApplicationType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrgConfiguration {

    @Bean
    fun clientOrgConfigStore(): OrgConfigStore {
        val credentialString = ""
        val store = OrgConfigStore()
        store.putOrgConfig(1, 1, "manoo@meetingworks.app", credentialString, ApplicationType.CALENDAR, false)
        return store
    }
}
