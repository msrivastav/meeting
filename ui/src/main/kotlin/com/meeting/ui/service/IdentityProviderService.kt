package com.meeting.ui.service

import com.meeting.ProtoUserRecommendationRequest
import com.meeting.common.directory.DirectoryEntry
import com.meeting.common.directory.toDirectoryEntry
import com.meeting.ui.client.ApplicationProviderResolver
import io.grpc.StatusException
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class IdentityProviderService(
    @Value("\${meeting.user-recommendation.limit}") private val limit: Int,
    private val applicationProviderResolver: ApplicationProviderResolver
) {

    fun getUserRecommendation(orgId: Int, namePart: String): List<DirectoryEntry> {
        val identityAppStub = applicationProviderResolver.getGrpcClientForIdentityApplication()

        return try {
            ProtoUserRecommendationRequest.newBuilder()
                .apply {
                    this.orgId = orgId
                    this.namePart = namePart
                    limit = this@IdentityProviderService.limit
                }.build()
                .let {
                    runBlocking {
                        identityAppStub.recommendUsers(it).usersList
                            .map { it.toDirectoryEntry() }
                    }
                }
        } catch (e: StatusException) {
            log.error("Failed to get user recommendation for org: $orgId, and name part: $namePart with reason: $e")
            emptyList()
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
