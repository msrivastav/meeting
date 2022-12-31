package com.meeting.zoom.server

import com.meeting.ProtoUser
import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory

@GRpcService
class ZoomConnectorServer :
    UserRecommendationServiceCoroutineImplBase() {

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest):
        ProtoUserRecommendationResponse {
        log.debug("Input prefix value: $request")

        return ProtoUserRecommendationResponse.newBuilder()
            .addUsers(ProtoUser.getDefaultInstance())
            .build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
