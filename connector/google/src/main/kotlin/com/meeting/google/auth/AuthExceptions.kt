package com.meeting.google.auth

class ClientOrgNotConfiguredException(orgId: Int):
    RuntimeException("Client org with id: $orgId not configured")

class ClientOrgDisabledException(orgId: Int):
    RuntimeException("Client org with id: $orgId is disabled")