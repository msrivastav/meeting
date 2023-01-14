package com.meeting.google.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.meeting.common.directory.DirectoryEntry
import com.meeting.common.directory.DirectoryService
import com.meeting.google.repository.DirectoryRepository
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DirectoryServiceImpl(
    private val directoryRepository: DirectoryRepository,
    private val meterRegistry: MeterRegistry
) : DirectoryService {

    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val counterName = "connector.google.directory.size"

    override fun getUserSuggestion(orgId: Int, namePrefix: String, limit: Int): List<DirectoryEntry> {
        return directoryRepository.getOrgDirectory(orgId)
            // TODO Fix this
            .filter { it.fullName.startsWith('1') }
            .take(limit)
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
