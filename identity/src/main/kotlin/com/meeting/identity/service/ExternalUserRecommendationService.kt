package com.meeting.identity.service

import com.meeting.common.directory.DirectoryEntry
import com.meeting.identity.processor.ExternalContactProcessor
import org.springframework.stereotype.Service

@Service
class ExternalUserRecommendationService(
    private val externalContactProcessor: ExternalContactProcessor
) {

    /**
     * Provides the email ids of external users that may have been used by this org user.
     */
    fun getExternalUserRecommendation(orgId: Int, namePart: String, limit: Int) =
        externalContactProcessor.getExternalContactSuggestion(orgId, namePart, limit)
            .map { toDirectoryEntry(it, orgId) }

    private fun toDirectoryEntry(email: String, orgId: Int) = DirectoryEntry("", "", "", email, orgId, true)
}
