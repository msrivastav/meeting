package com.meeting.google.endpoint.grpc

import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import com.meeting.common.directory.DirectoryService
import com.meeting.common.directory.toProtoUser
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class DirectoryGrpcEndpoint(private val directoryService: DirectoryService) :
    UserRecommendationServiceCoroutineImplBase() {

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest): ProtoUserRecommendationResponse {
        return directoryService.getUserRecommendation(request.orgId, request.namePart, request.limit)
            .map { it.toProtoUser() }
            .let { ProtoUserRecommendationResponse.newBuilder().addAllUsers(it).build() }
    }
}
