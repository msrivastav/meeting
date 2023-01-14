package com.meeting.common.directory

import com.meeting.ProtoUser

data class DirectoryEntry(
    val fullName: String,
    val givenName: String,
    val familyName: String,
    val email: String,
    val orgId: Int = 0,
    val isExternal: Boolean
)

fun ProtoUser.toDirectoryEntry() =
    DirectoryEntry(this.fullName, this.givenName, this.familyName, this.email, isExternal = this.isExternal)

fun DirectoryEntry.toProtoUser(): ProtoUser = ProtoUser.newBuilder()
    .apply {
        fullName = this@toProtoUser.fullName
        givenName = this@toProtoUser.givenName
        familyName = this@toProtoUser.familyName
        email = this@toProtoUser.email
        isExternal = this@toProtoUser.isExternal
    }
    .build()
