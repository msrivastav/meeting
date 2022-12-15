package com.meeting.identity.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.util.ProxyUtils

@Entity
class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var organisationId: Int = -1
    private var organisationName: String = ""
    private var emailDomain: String = ""

    fun setOrganisationId(organisationId: Int) {
        this.organisationId = organisationId
    }

    fun getOrganisationId() = organisationId

    fun setOrganisationName(organisationName: String) {
        this.organisationName = organisationName
    }

    fun getOrganisationName() = organisationName

    fun setEmailDomain(emailDomain: String) {
        this.emailDomain = emailDomain
    }

    fun getEmailDomain() = emailDomain

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is Organisation) return false
        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false
        return this.getOrganisationId() == other.getOrganisationId()
    }

    override fun hashCode() = organisationId

    override fun toString(): String {
        return "Organisation(organisationId=$organisationId, " +
                "organisationName='$organisationName', emailDomain='$emailDomain')"
    }
}