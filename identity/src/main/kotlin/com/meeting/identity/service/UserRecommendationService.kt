package com.meeting.identity.service

import com.meeting.common.collections.combine
import com.meeting.common.directory.DirectoryEntry
import com.meeting.identity.processor.ExternalContactProcessor
import com.meeting.identity.processor.InternalContactProcessor
import org.springframework.stereotype.Service

/**
 * Provides user calendar and recommendations.
 */
@Service
class UserRecommendationService(
    private val externalContactProcessor: ExternalContactProcessor,
    private val internalContactProcessor: InternalContactProcessor
) {
    /**
     * Provides a list of directory entries that contains both external and internal user details.
     */
    fun getUserRecommendations(
        orgId: Int,
        namePart: String,
        limit: Int
    ): List<DirectoryEntry> {
        val internalUsers = internalContactProcessor.getInternalRecommendation(orgId, namePart, limit)
        val externalUserList = externalContactProcessor.getExternalRecommendation(orgId, namePart, limit)

        return internalUsers.combine(externalUserList)
    }
}
