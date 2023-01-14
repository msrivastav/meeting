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
import com.google.api.services.directory.DirectoryScopes
import com.meeting.common.datastore.OrgConfigStore
import com.meeting.common.exception.OrgProviderConfigNotFoundException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
class CredentialsProvider(
    @Value("\${meeting.google.auth.token-directory}") private val tokenDirectory: String,
    @Value("\${meeting.google.calendar.application-id}") private val googleApplicationId: Int,
    @Qualifier("clientOrgConfigStore") private val orgConfigStore: OrgConfigStore
) {

    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    companion object GoogleCredentialGlobals {
        val scopes = listOf(CalendarScopes.CALENDAR_READONLY, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY)
    }

    /**
     * Provides the Google credentials for a given client org
     */
    fun getCredentialsForClientOrg(orgId: Int): Credential {
        val orgConfig =
            orgConfigStore.getOrgProviderConfig(orgId, googleApplicationId) ?: throw OrgProviderConfigNotFoundException(
                orgId,
                googleApplicationId
            )

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, orgConfig.orgAdminOauth2Credentials.reader())

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecrets,
            scopes
        ).setDataStoreFactory(FileDataStoreFactory(File(tokenDirectory))).setAccessType("offline").build()

        val receiver = LocalServerReceiver.Builder().build()

        // returns an authorized Credential object.
        return AuthorizationCodeInstalledApp(flow, receiver).authorize(orgConfig.orgAdminId)
    }
}
