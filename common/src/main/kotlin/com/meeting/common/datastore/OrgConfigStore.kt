package com.meeting.common.datastore

import java.util.concurrent.ConcurrentHashMap

/**
 * Stores config data related to organisations that use Google services.
 */
class OrgConfigStore {
    private val orgConfigs: ConcurrentHashMap<Int, OrgConfig> = ConcurrentHashMap()

    data class OrgConfig(
        val orgAdminId: String,
        val orgAdminOauth2Credentials: String,
        val enabled: Boolean
    )

    fun putOrgConfig(orgId: Int, orgAdminId: String, orgAdminOauth2Credentials: String, enabled: Boolean) =
        orgConfigs.put(orgId, OrgConfig(orgAdminId, orgAdminOauth2Credentials, enabled))


    fun removeOrgConfig(orgId: Int) = orgConfigs.remove(orgId)

    fun getOrgConfig(orgId: Int) = orgConfigs[orgId]

    fun disableOrgConfig(orgId: Int) {
        orgConfigs.computeIfPresent(orgId) { _, v -> OrgConfig(v.orgAdminId, v.orgAdminOauth2Credentials, false) }
    }

    fun enableOrgConfig(orgId: Int) {
        orgConfigs.computeIfPresent(orgId) { _, v -> OrgConfig(v.orgAdminId, v.orgAdminOauth2Credentials, true) }
    }
}