package com.meeting.identity.config

import com.meeting.common.datastore.OrgConfigStore
import com.meeting.common.type.ApplicationType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrgConfiguration {

    @Bean
    fun clientOrgConfigStore(): OrgConfigStore {
        val store = OrgConfigStore()
        store.putOrgConfig(1, 1, "", "", ApplicationType.CALENDAR, false)
        store.putOrgConfig(1, 1, "", "", ApplicationType.IDENTITY, false)
        return store
    }
}
