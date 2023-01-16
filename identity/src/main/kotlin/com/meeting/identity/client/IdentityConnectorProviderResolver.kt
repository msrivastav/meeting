package com.meeting.identity.client

import com.meeting.UserRecommendationServiceGrpcKt.UserRecommendationServiceCoroutineStub
import com.meeting.common.exception.ProviderGrpcHostPortNotFound
import com.meeting.identity.datastore.ConnectorEndpointsStore
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
class IdentityConnectorProviderResolver(private val connectorEndpointsStore: ConnectorEndpointsStore) {

    // <provider id, grpc channel to the  provider connector>
    private val connectorChannels = ConcurrentHashMap<Int, ManagedChannel>()

    private companion object {
        val grpcClientMonitoringInterceptor: MonitoringClientInterceptor =
            MonitoringClientInterceptor.create(Configuration.cheapMetricsOnly())
    }

    /**
     * Provides gRPC stub to call the server of the provider.
     */
    fun getGrpcClientForProvider(providerId: Int): UserRecommendationServiceCoroutineStub {
        return UserRecommendationServiceCoroutineStub(getOrCreateChannel(providerId))
    }

    private fun getOrCreateChannel(providerId: Int): ManagedChannel {
        return connectorChannels.computeIfAbsent(providerId) { _ ->
            val hostPort =
                connectorEndpointsStore.getHostPort(providerId) ?: throw ProviderGrpcHostPortNotFound(providerId)
            ManagedChannelBuilder.forAddress(hostPort.host, hostPort.port)
                .disableRetry()
                .intercept(grpcClientMonitoringInterceptor)
                .usePlaintext().build()
        }
    }
}
