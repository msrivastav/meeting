package com.meeting.common.datastore

import com.meeting.common.type.ApplicationType
import java.util.concurrent.ConcurrentHashMap

/**
 * Stores config data related to organisations that use Google services.
 */
class OrgConfigStore {
    /*
     * Map of client org, and the providers it can use
     * <OrgId, <ProviderId, OrgProviderConfig>>
     *
     * OrgId: Client org
     * ProviderId: ID of the provider that client uses for specific service. Such as Gmail, or Zoom.
     * OrgProviderConfig: Metadata of how to connect to provider to get org data.
     */
    private val orgConfigs: ConcurrentHashMap<Int, ConcurrentHashMap<Int, OrgProviderConfig>> = ConcurrentHashMap()

    private companion object {
        val emptyMap = ConcurrentHashMap<Int, OrgProviderConfig>()
    }

    data class OrgProviderConfig(
        val orgAdminId: String,
        val orgAdminOauth2Credentials: String,
        val appType: ApplicationType,

        /**
         * Reflects whether there is a dedicated api for this org and provider.
         * For example, if an or uses Google meet for video call, then this
         * variable will be false as Google does not expose meet api.
         * If an org uses Zoom for video call then this variable will be true.
         */
        val hasDedicatedApi: Boolean
    )

    fun putOrgConfig(
        orgId: Int,
        providerId: Int,
        orgAdminId: String,
        orgAdminOauth2Credentials: String,
        appType: ApplicationType,
        hasDedicatedApi: Boolean
    ) {

        orgConfigs.compute(orgId) { _, v ->
            var providerToConfig = v
            if (providerToConfig == null) {
                providerToConfig = ConcurrentHashMap()
            }
            providerToConfig[providerId] =
                OrgProviderConfig(orgAdminId, orgAdminOauth2Credentials, appType, hasDedicatedApi)
            providerToConfig
        }
    }


    fun removeOrgConfig(orgId: Int) = orgConfigs.remove(orgId)

    fun removeOrgProviderConfig(orgId: Int, providerId: Int): OrgProviderConfig? {
        val providerConfigs = orgConfigs[orgId] ?: return null
        return providerConfigs.remove(providerId)
    }

    fun getOrgConfig(orgId: Int): ConcurrentHashMap<Int, OrgProviderConfig> {
        return ConcurrentHashMap(orgConfigs[orgId] ?: return emptyMap)
    }

    fun getOrgProviderConfig(orgId: Int, providerId: Int): OrgProviderConfig? = getOrgConfig(orgId)[providerId]
}