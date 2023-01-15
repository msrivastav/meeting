package com.meeting.identity.endpoint

import com.meeting.identity.processor.ExternalContactProcessor
import com.meeting.identity.processor.InternalContactProcessor
import com.meeting.identity.service.UserRecommendationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("debugger")
class DebuggerEndpoint(
    private val internalContactProcessor: InternalContactProcessor,
    private val externalContactProcessor: ExternalContactProcessor,
    private val userRecommendationService: UserRecommendationService
) {

    @GetMapping(
        "/get-user-recommendation/{orgId}/{partName}/{limit}/{scope}",
        produces = ["application/json"]
    )
    fun getCalendarAndSuggestions(
        @PathVariable orgId: Int,
        @PathVariable limit: Int,
        @PathVariable partName: String,
        @PathVariable scope: String = "combined"
    ) = when (scope) {
        "internal" -> internalContactProcessor.getInternalRecommendation(orgId, partName, limit)
        "external" -> externalContactProcessor.getExternalRecommendation(orgId, partName, limit)
        else -> userRecommendationService.getUserRecommendations(orgId, partName, limit)
    }
}
