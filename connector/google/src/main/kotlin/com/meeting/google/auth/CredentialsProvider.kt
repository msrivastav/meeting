package com.meeting.google.auth

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.CalendarScopes
import com.meeting.common.datastore.OrgConfigStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
class CredentialsProvider(
    @Value("\${meeting.auth.token-directory}") val tokenDirectory: String,
    @Qualifier("clientOrgConfigStore") val orgConfigStore: OrgConfigStore) {

    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    companion object GoogleCredentialGlobals {
        val scopes = listOf(CalendarScopes.CALENDAR_READONLY)
    }

    /**
     * Provides the Google credentials for a given client org
     */
    fun getCredentialsForClientOrg(orgId: Int): Credential {
        val orgConfig = orgConfigStore.getOrgConfig(orgId) ?: throw ClientOrgNotConfiguredException(orgId)

        val clientSecrets =
            GoogleClientSecrets.load(jsonFactory, orgConfig.orgAdminOauth2Credentials.reader())

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecrets,
            GoogleCredentialGlobals.scopes
        )
            .setDataStoreFactory(FileDataStoreFactory(File(tokenDirectory)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().build()

        //returns an authorized Credential object.
        return AuthorizationCodeInstalledApp(flow, receiver).authorize(orgConfig.orgAdminId)

    }
}