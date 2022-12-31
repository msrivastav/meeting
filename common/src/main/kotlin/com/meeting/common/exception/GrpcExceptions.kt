package com.meeting.common.exception

class GrpcClientHostPortNotFound(providerId: Int) :
    RuntimeException("Grpc client host and port not found for provider: $providerId")
