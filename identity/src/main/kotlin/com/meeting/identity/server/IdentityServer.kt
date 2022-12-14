package com.meeting.identity.server

import com.meeting.ProtoUser
import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import com.meeting.identity.processor.ExternalContactProcessor
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class IdentityServer(private val externalContactProcessor: ExternalContactProcessor) :
    UserRecommendationServiceCoroutineImplBase() {

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest):
            ProtoUserRecommendationResponse {

        val externalUsers = externalContactProcessor.getExternalContactSuggestion(request.userIdPrefix)
            .map { id ->
                ProtoUser.newBuilder()
                    .apply { userEmailId = id }
                    .apply { isExternal = true }
                    .build()
            }

        return ProtoUserRecommendationResponse.newBuilder()
            .addAllUsers(externalUsers)
            .build()

        /*        return ProtoUserRecommendationResponse.newBuilder()
                    .addUsers(ProtoUser.newBuilder()
                        .apply { userEmailId = "manoo.srivastav@gmail.com" }
                        .apply { isExternal = true }
                        .build()
                    )
                    .build()*/
    }
}