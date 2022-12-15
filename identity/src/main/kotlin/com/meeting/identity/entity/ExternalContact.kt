package com.meeting.identity.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.util.ProxyUtils

@Entity
class ExternalContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var externalContactId: Int = -1
    private var emailIdPrefix: String = ""
    private var contactOrgId: Int = -1

    fun setExternalContactId(externalContactId: Int) {
        this.externalContactId = externalContactId
    }

    fun getExternalContactId() = externalContactId

    fun setEmailIdPrefix(emailIdPrefix: String) {
        this.emailIdPrefix = emailIdPrefix
    }

    fun getEmailIdPrefix() = emailIdPrefix

    fun setContactOrgId(contactOrgId: Int) {
        this.contactOrgId = contactOrgId
    }

    fun getContactOrgId() = contactOrgId

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is ExternalContact) return false
        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false
        return this.getExternalContactId() == other.getExternalContactId()
    }

    override fun hashCode() = externalContactId

    override fun toString(): String {
        return "ExternalContact(organisationId=$externalContactId, " +
                "organisationName='$emailIdPrefix', emailDomain='$contactOrgId')"
    }
}