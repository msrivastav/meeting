package com.meeting.identity.repository

import com.meeting.identity.entity.ExternalContact
import org.springframework.data.repository.CrudRepository

interface ExternalContactRepository : CrudRepository<ExternalContact, Int> {
    fun findAllByContactOrgIdAndEmailIdPrefixContainsOrderByEmailIdPrefix(
        contactOrgId: Int,
        namePart: String
    ): List<ExternalContact>
}
