package com.meeting.identity.service

import com.meeting.ProtoUserRecommendationRequest
import com.meeting.common.collections.combine
import com.meeting.common.datastore.OrgConfigStore
import com.meeting.common.directory.DirectoryEntry
import com.meeting.common.directory.toDirectoryEntry
import com.meeting.common.exception.OrgApplicationTypeConfigNotFoundException
import com.meeting.common.type.ApplicationType
import com.meeting.identity.client.identity.IdentityConnectorProviderResolver
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * Provides user calendar and recommendations.
 */
@Service
class UserRecommendationService(
    private val orgConfigStore: OrgConfigStore,
    private val identityConnectorProviderResolver: IdentityConnectorProviderResolver,
    private val externalUserRecommendationService: ExternalUserRecommendationService,
    private val meterRegistry: MeterRegistry
) {

    private val orgToDirectoryProvider = ConcurrentHashMap<Int, Int>()
    private val internalUserRecommendationCounterName = "com.meeting.identity.internal.users"
    private val externalUserRecommendationCounterName = "com.meeting.identity.external.users"

    /**
     * Provides a list of directory entries that contains both external and internal user details.
     */
    fun getUserRecommendations(
        orgId: Int,
        namePart: String,
        limit: Int
    ): List<DirectoryEntry> {
        val internalUsers = getInternalUsers(orgId, namePart, limit)
        val externalUserList = getExternalUsers(orgId, namePart, limit)

        return internalUsers.combine(externalUserList)
    }

    private fun getInternalUsers(orgId: Int, namePart: String, limit: Int): List<DirectoryEntry> {
        val clientForIdentityConnector =
            getIdentityProviderId(orgId).let { identityConnectorProviderResolver.getGrpcClientForProvider(it) }

        return runBlocking {
            ProtoUserRecommendationRequest.newBuilder()
                .apply {
                    this.orgId = orgId
                    this.namePart = namePart
                    this.limit = limit
                }.build()
                .let { clientForIdentityConnector.recommendUsers(it) }
                .usersList
                .map { it.toDirectoryEntry() }
                .also {
                    meterRegistry.counter(internalUserRecommendationCounterName, "org", orgId.toString())
                        .increment(it.size.toDouble())
                }
        }
    }

    private fun getExternalUsers(orgId: Int, namePart: String, limit: Int) =
        externalUserRecommendationService.getExternalUserRecommendation(orgId, namePart, limit)
            .also {
                meterRegistry.counter(externalUserRecommendationCounterName, "org", orgId.toString())
                    .increment(it.size.toDouble())
            }

    private fun getIdentityProviderId(orgId: Int): Int {
        return orgToDirectoryProvider.computeIfAbsent(orgId) {
            val providerConfig = orgConfigStore.getOrgConfig(orgId)
            if (providerConfig.isEmpty()) {
                throw OrgApplicationTypeConfigNotFoundException(orgId, ApplicationType.IDENTITY)
            }
            providerConfig.entries.first { it.value.appType == ApplicationType.IDENTITY }.key
        }
    }
}
