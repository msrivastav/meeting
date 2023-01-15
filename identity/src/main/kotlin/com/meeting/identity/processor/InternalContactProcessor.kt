package com.meeting.identity.processor

import com.meeting.ProtoUserRecommendationRequest
import com.meeting.common.datastore.OrgConfigStore
import com.meeting.common.directory.DirectoryEntry
import com.meeting.common.directory.toDirectoryEntry
import com.meeting.common.exception.OrgApplicationTypeConfigNotFoundException
import com.meeting.common.type.ApplicationType
import com.meeting.identity.client.identity.IdentityConnectorProviderResolver
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InternalContactProcessor(
    private val orgConfigStore: OrgConfigStore,
    private val identityConnectorProviderResolver: IdentityConnectorProviderResolver,
    private val meterRegistry: MeterRegistry
) {

    private val orgToDirectoryProvider = ConcurrentHashMap<Int, Int>()
    private val internalCounter = "com.meeting.identity.internal.users"

    fun getInternalRecommendation(orgId: Int, namePart: String, limit: Int): List<DirectoryEntry> {
        try {
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
                        meterRegistry.counter(internalCounter, "org", orgId.toString(), "outcome", "success")
                            .increment(it.size.toDouble())
                    }
            }
        } catch (e: Exception) {
            log.error("Exception while trying to fetch internal users: $e")
            meterRegistry.counter(internalCounter, "org", orgId.toString(), "outcome", "fail").increment()
            return emptyList()
        }
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

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
