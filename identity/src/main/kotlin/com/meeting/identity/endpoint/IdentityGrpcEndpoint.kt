package com.meeting.identity.endpoint

import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import com.meeting.common.directory.toProtoUser
import com.meeting.identity.service.UserRecommendationService
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory

@GRpcService
class IdentityGrpcEndpoint(private val service: UserRecommendationService) :
    UserRecommendationServiceCoroutineImplBase() {

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest):
        ProtoUserRecommendationResponse {
        log.debug("User Recommendation request: $request")

        return service.getUserRecommendations(request.orgId, request.namePart, request.limit)
            .map { it.toProtoUser() }
            .let {
                ProtoUserRecommendationResponse.newBuilder()
                    .addAllUsers(it)
                    .build()
            }
            .also { log.debug("User Recommendation response: $it") }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
