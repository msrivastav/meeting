package com.meeting.meetingscheduler.client.calendar

import com.meeting.UserCalendarServiceGrpcKt.UserCalendarServiceCoroutineStub
import com.meeting.common.exception.GrpcClientHostPortNotFound
import com.meeting.meetingscheduler.datastore.ConnectorEndpointsStore
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
class CalendarConnectorProviderResolver(private val connectorEndpointsStore: ConnectorEndpointsStore) {

    private val connectorChannels = ConcurrentHashMap<Int, ManagedChannel>()

    private companion object {
        val grpcClientMonitoringInterceptor: MonitoringClientInterceptor =
            MonitoringClientInterceptor.create(Configuration.cheapMetricsOnly())
    }

    /**
     * Provides gRPC stub to call the server of the provider.
     */
    fun getGrpcClientForProvider(providerId: Int): UserCalendarServiceCoroutineStub {
        return UserCalendarServiceCoroutineStub(getOrCreateChannel(providerId))
    }

    private fun getOrCreateChannel(providerId: Int): ManagedChannel {
        return connectorChannels.computeIfAbsent(providerId) { _ ->
            val hostPort =
                connectorEndpointsStore.getHostPort(providerId) ?: throw GrpcClientHostPortNotFound(providerId)
            ManagedChannelBuilder.forAddress(hostPort.host, hostPort.port)
                .disableRetry()
                .intercept(grpcClientMonitoringInterceptor)
                .usePlaintext().build()
        }
    }
}
