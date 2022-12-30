package com.meeting.common.exception

import com.meeting.common.type.ApplicationType

class OrgConfigNotFoundException(orgId: Int) :
    RuntimeException("Org: $orgId not configured")

class OrgProviderConfigNotFoundException(orgId: Int, providerId: Int) :
    RuntimeException("Org: $orgId, provider: $providerId not configured")

class OrgApplicationTypeConfigNotFoundException(orgId: Int, applicationType: ApplicationType) :
    RuntimeException("Org: $orgId, application: $applicationType not configured")
