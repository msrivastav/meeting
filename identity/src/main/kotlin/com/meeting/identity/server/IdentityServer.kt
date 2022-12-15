package com.meeting.identity.server

import com.meeting.ProtoUser
import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import com.meeting.identity.processor.ExternalContactProcessor
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory

@GRpcService
class IdentityServer(private val externalContactProcessor: ExternalContactProcessor) :
    UserRecommendationServiceCoroutineImplBase() {

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest):
            ProtoUserRecommendationResponse {

        log.debug("Input prefix value: $request")

        val externalUsers = externalContactProcessor.getExternalContactSuggestion(request.userIdPrefix)
            .map { id ->
                ProtoUser.newBuilder()
                    .apply { userEmailId = id }
                    .apply { isExternal = true }
                    .build()
            }
        log.debug("Output id value: $externalUsers")
        return ProtoUserRecommendationResponse.newBuilder()
            .addAllUsers(externalUsers)
            .build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}