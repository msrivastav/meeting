package com.meeting.util.server.grpc.interceptor

import io.grpc.BindableService
import io.grpc.ServerInterceptor
import io.prometheus.client.CollectorRegistry
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor
import org.lognet.springboot.grpc.GRpcGlobalInterceptor
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
//@ConditionalOnBean(BindableService::class)
class GrpcServerMonitoringInterceptor {

    @Bean
    //@ConditionalOnBean
    @GRpcGlobalInterceptor
    fun monitoringInterceptor(collectorRegistry: CollectorRegistry): ServerInterceptor =
        MonitoringServerInterceptor.create(
            me.dinowernli.grpc.prometheus.Configuration.cheapMetricsOnly().withCollectorRegistry(collectorRegistry))
}