package com.meeting.identity.endpoint.grpc

import com.google.rpc.Code
import com.meeting.ProtoUserRecommendationRequest
import com.meeting.ProtoUserRecommendationResponse
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineImplBase
import com.meeting.common.directory.toProtoUser
import com.meeting.common.exception.getStatusException
import com.meeting.common.type.ApplicationType
import com.meeting.identity.service.UserRecommendationService
import io.micrometer.core.instrument.MeterRegistry
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory

@GRpcService
class IdentityGrpcEndpoint(
    private val service: UserRecommendationService,
    private val meterRegistry: MeterRegistry
) : UserRecommendationServiceCoroutineImplBase() {

    private val fetchFailedCounterName = "com.meeting.identity.recommendation.failed"

    override suspend fun recommendUsers(request: ProtoUserRecommendationRequest):
        ProtoUserRecommendationResponse {
        log.debug("User Recommendation request: $request")

        return try {
            service.getUserRecommendations(request.orgId, request.namePart, request.limit)
                .map { it.toProtoUser() }
                .let {
                    ProtoUserRecommendationResponse.newBuilder()
                        .addAllUsers(it)
                        .build()
                }
                .also { log.debug("User Recommendation response: $it") }
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
