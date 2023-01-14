package com.meeting.identity.processor

import com.meeting.common.exception.OrgConfigNotFoundException
import com.meeting.identity.repository.ExternalContactRepository
import com.meeting.identity.repository.OrganisationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ExternalContactProcessor(
    private val organisationRepository: OrganisationRepository,
    private val externalContactRepository: ExternalContactRepository
) {

    fun getExternalContactSuggestion(orgId: Int, namePart: String, limit: Int = 5): List<String> {
        return try {
            val emailDomain = organisationRepository.findById(orgId)
                .orElseThrow { OrgConfigNotFoundException(orgId) }
                .getEmailDomain()

            externalContactRepository
                .findAllByContactOrgIdAndEmailIdPrefixContainsOrderByEmailIdPrefix(orgId, namePart)
                .take(limit)
                .map { it.getEmailIdPrefix() }
                .map { it.plus("@").plus(emailDomain) }
        } catch (e: Exception) {
            log.error("Error while fetching external contacts for org: $orgId, and namePart: $namePart is: $e")
            emptyList()
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
