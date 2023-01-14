package com.meeting.google.service

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

    private val counterName = "connector.google.directory.userSuggestions"

    override fun getUserRecommendation(orgId: Int, namePart: String, limit: Int): List<DirectoryEntry> {
        val elementsToTake = if (limit <= 0) 100 else limit

        return directoryRepository.getOrgDirectory(orgId)
            .filter {
                it.givenName.contains(namePart, true) ||
                    it.familyName.contains(namePart, true) ||
                    it.email.contains(namePart, true)
            }
            .take(elementsToTake)
            .also {
                log.debug("Suggestions for org: $orgId and name prefix: $namePart are: $it")
                meterRegistry.counter(counterName, "org", orgId.toString()).increment(it.size.toDouble())
            }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
