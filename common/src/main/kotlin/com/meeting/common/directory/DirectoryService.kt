package com.meeting.common.directory

interface DirectoryService {

    /**
     * Fetches entire directory of the given org.
     */
    fun getUserRecommendation(orgId: Int, namePart: String, limit: Int): List<DirectoryEntry>
}
