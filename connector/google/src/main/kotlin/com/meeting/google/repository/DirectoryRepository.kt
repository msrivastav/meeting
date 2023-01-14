package com.meeting.google.repository

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.directory.Directory
import com.google.api.services.directory.model.User
import com.meeting.common.directory.DirectoryEntry
import com.meeting.google.auth.CredentialsProvider
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class DirectoryRepository(
    @Value("\${meeting.google.application-name}") private val applicationName: String,
    @Value("\${meeting.google.directory.cache-ttl}") private val cacheTtl: Duration,
    private val credentialsProvider: CredentialsProvider,
    private val meterRegistry: MeterRegistry
) {

    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val counterName = "connector.google.directory.size"

    private val cache: Cache<Int, List<DirectoryEntry>>

    init {
        cache = Caffeine.newBuilder()
            .expireAfterWrite(cacheTtl)
            .recordStats()
            .build(::loadDirectory)
    }

    /**
     * Returns the List of [DirectoryEntry] of the org, [emptyList] otherwise.
     */
    fun getOrgDirectory(orgId: Int): List<DirectoryEntry> {
        return cache.get(orgId) { emptyList() }
    }

    private fun loadDirectory(orgId: Int): List<DirectoryEntry> {
        val credentials = credentialsProvider.getCredentialsForClientOrg(orgId)

        val service = Directory.Builder(httpTransport, jsonFactory, credentials)
            .apply { applicationName = this@DirectoryRepository.applicationName }
            .build()

        val users = try {
            service.users().list()
                .apply {
                    customer = "my_customer"
                }
                .execute()
                .users
                .map { it.toDirectoryEntry(orgId) }
                .toList()
                .also { meterRegistry.counter(counterName, "orgId", orgId.toString()).increment(it.size.toDouble()) }
        } catch (e: Exception) {
            log.error("Could not retrieve directory for org: $orgId due to: $e")
            emptyList()
        }

        if (users.isNotEmpty()) log.debug("Directory entries fetcher for org: $orgId are: ${users.size}")

        return users
    }

    private fun User.toDirectoryEntry(orgId: Int): DirectoryEntry =
        DirectoryEntry(this.name.fullName, this.name.givenName, this.name.familyName, this.primaryEmail, orgId, false)

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
