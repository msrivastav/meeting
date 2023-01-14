package com.meeting.common.directory

data class DirectoryEntry(
    val fullName: String,
    val givenName: String,
    val familyName: String,
    val email: String,
    val orgId: Int
)
