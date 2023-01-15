package com.meeting.identity.processor

import com.meeting.common.directory.DirectoryEntry
import com.meeting.common.exception.OrgConfigNotFoundException
import com.meeting.identity.repository.ExternalContactRepository
import com.meeting.identity.repository.OrganisationRepository
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ExternalContactProcessor(
    private val organisationRepository: OrganisationRepository,
    private val externalContactRepository: ExternalContactRepository,
    private val meterRegistry: MeterRegistry
) {

    private val externalCounter = "com.meeting.identity.external.users"

    fun getExternalRecommendation(orgId: Int, namePart: String, limit: Int = 5): List<DirectoryEntry> {
        return try {
            val emailDomain = organisationRepository.findById(orgId)
                .orElseThrow { OrgConfigNotFoundException(orgId) }
                .getEmailDomain()

            val elementsToTake = if (limit <= 0) 100 else limit

            externalContactRepository
                .findAllByContactOrgIdAndEmailIdPrefixContainsOrderByEmailIdPrefix(orgId, namePart)
                .take(elementsToTake)
                .map { it.getEmailIdPrefix().plus("@").plus(emailDomain) }
                .map { toDirectoryEntry(it, orgId) }
                .also {
                    meterRegistry.counter(externalCounter, "org", orgId.toString(), "outcome", "success")
                        .increment(it.size.toDouble())
                }
        } catch (e: Exception) {
            log.error("Error while fetching external contacts for org: $orgId, and namePart: $namePart is: $e")
            meterRegistry.counter(externalCounter, "org", orgId.toString(), "outcome", "fail").increment()
            emptyList()
        }
    }

    private fun toDirectoryEntry(email: String, orgId: Int) = DirectoryEntry("", "", "", email, orgId, true)

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
