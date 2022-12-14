package com.meeting.identity.processor

import com.meeting.identity.repository.ExternalContactRepository
import com.meeting.identity.repository.OrganisationRepository
import org.springframework.stereotype.Component

@Component
class ExternalContactProcessor(
    private val organisationRepository: OrganisationRepository,
    private val externalContactRepository: ExternalContactRepository
) {

    fun getExternalContactSuggestion(prefix: String) = externalContactRepository.findAll()
            .map { it.getEmailIdPrefix() }
            .filter { it.startsWith(prefix, true) }
            .take(5)
}