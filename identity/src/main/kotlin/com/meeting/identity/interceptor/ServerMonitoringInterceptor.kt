package com.meeting.identity.interceptor

import io.grpc.ServerInterceptor
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor
import org.lognet.springboot.grpc.GRpcGlobalInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServerInterceptorConfig {

    @Bean
    @GRpcGlobalInterceptor
    fun monitoringInterceptor(): ServerInterceptor =
        MonitoringServerInterceptor.create(
            me.dinowernli.grpc.prometheus.Configuration.cheapMetricsOnly())
}