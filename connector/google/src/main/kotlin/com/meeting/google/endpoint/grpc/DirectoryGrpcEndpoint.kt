package com.meeting.google.endpoint.grpc

import com.google.rpc.Code
import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import com.meeting.common.directory.DirectoryService
import com.meeting.common.directory.toProtoUser
import com.meeting.common.exception.getStatusException
import com.meeting.common.type.ApplicationType
import io.micrometer.core.instrument.MeterRegistry
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory

@GRpcService
class DirectoryGrpcEndpoint(private val directoryService: DirectoryService, private val meterRegistry: MeterRegistry) :
    UserRecommendationServiceCoroutineImplBase() {

    private val fetchFailedCounterName = "com.meeting.google.directory.fetch.failed"

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest): ProtoUserRecommendationResponse {
        return try {
            directoryService.getUserRecommendation(request.orgId, request.namePart, request.limit)
                .map { it.toProtoUser() }
                .let { ProtoUserRecommendationResponse.newBuilder().addAllUsers(it).build() }
        } catch (e: Exception) {
            log.error("Exception while fetching user recommendation: $e")
            meterRegistry.counter(fetchFailedCounterName, "org", request.orgId.toString()).increment()
            throw getStatusException(e, Code.CANCELLED, ApplicationType.IDENTITY.toString())
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(this::class.java.name)
    }
}
