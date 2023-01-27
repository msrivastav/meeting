package com.meeting.ui.client

import com.meeting.UserCalendarsAndSuggestionsServiceGrpcKt.UserCalendarsAndSuggestionsServiceCoroutineStub
import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineStub
import com.meeting.common.exception.ApplicationGrpcHostPortNotFound
import com.meeting.ui.datastore.ApplicationEndpointsStore
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import me.dinowernli.grpc.prometheus.Configuration
import me.dinowernli.grpc.prometheus.MonitoringClientInterceptor
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * Provides the correct gRPC client for the org.
 */
@Component
class ApplicationProviderResolver(private val applicationEndpointsStore: ApplicationEndpointsStore) {

    private val applicationChannels = ConcurrentHashMap<String, ManagedChannel>()

    private companion object {
        val grpcClientMonitoringInterceptor: MonitoringClientInterceptor =
            MonitoringClientInterceptor.create(Configuration.cheapMetricsOnly())
    }

    /**
     * Provides gRPC stub to call the server of the identity application.
     */
    fun getGrpcClientForIdentityApplication() =
        UserRecommendationServiceCoroutineStub(getOrCreateChannel("identity"))

    /**
     * Provides gRPC stub to call the server of the meeting scheduler application.
     */
    fun getGrpcClientForMeetingSchedulerApplication() =
        UserCalendarsAndSuggestionsServiceCoroutineStub(getOrCreateChannel("meeting-scheduler"))

    private fun getOrCreateChannel(application: String): ManagedChannel {
        return applicationChannels.computeIfAbsent(application) { _ ->
            val hostPort =
                applicationEndpointsStore.getHostPort(application) ?: throw ApplicationGrpcHostPortNotFound(application)
            ManagedChannelBuilder.forAddress(hostPort.host, hostPort.port)
                .disableRetry()
                .intercept(grpcClientMonitoringInterceptor)
                .usePlaintext().build()
        }
    }
}
