package com.meeting.google.config

import com.meeting.common.datastore.OrgConfigStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrgConfiguration {

    @Bean
    fun clientOrgConfigStore(): OrgConfigStore {
        val credentialString =
            "{\"installed\":{\"client_id\":\"974211070499-sobq56uahibccemdo0vk7nfuvs06dvuv.apps.googleusercontent.com\",\"project_id\":\"core-product-meeting-works\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://oauth2.googleapis.com/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_secret\":\"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\",\"redirect_uris\":[\"http://localhost\"]}}"
        val store = OrgConfigStore()
        store.putOrgConfig(1, "manoo@meetingworks.app", credentialString, true)
        return store
    }
}