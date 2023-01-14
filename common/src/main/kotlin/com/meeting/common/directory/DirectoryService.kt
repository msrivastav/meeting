package com.meeting.common.directory

interface DirectoryService {

    /**
     * Fetches entire directory of the given org.
     */
    fun getUserSuggestion(orgId: Int, namePrefix: String, limit: Int): List<DirectoryEntry>
}
