package com.meeting.identity.server

import com.meeting.ProtoUser
import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import org.lognet.springboot.grpc.GRpcService

@GRpcService(interceptors = [])
class IdentityServer : UserRecommendationServiceCoroutineImplBase() {

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest):
            ProtoUserRecommendationResponse {

        return ProtoUserRecommendationResponse.newBuilder()
            .addUsers(ProtoUser.newBuilder()
                .apply { userEmailId = "manoo.srivastav@gmail.com" }
                .apply { isExternal = true }
                .build()
            )
            .build()
    }
}