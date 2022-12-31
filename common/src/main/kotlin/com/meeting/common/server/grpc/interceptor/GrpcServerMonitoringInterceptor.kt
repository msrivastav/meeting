package com.meeting.common.server.grpc.interceptor

import io.grpc.ServerInterceptor
import io.prometheus.client.CollectorRegistry
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor
import org.lognet.springboot.grpc.GRpcGlobalInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// @ConditionalOnBean(BindableService::class)
@Configuration
class GrpcServerMonitoringInterceptor {

    @Bean
    // @ConditionalOnBean
    @GRpcGlobalInterceptor
    fun monitoringInterceptor(collectorRegistry: CollectorRegistry): ServerInterceptor =
        MonitoringServerInterceptor.create(
            me.dinowernli.grpc.prometheus.Configuration.cheapMetricsOnly().withCollectorRegistry(collectorRegistry)
        )
}
