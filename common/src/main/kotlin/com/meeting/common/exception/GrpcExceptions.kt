package com.meeting.common.exception

import com.google.rpc.Code
import com.google.rpc.ErrorInfo
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto

class GrpcClientHostPortNotFound(providerId: Int) :
    RuntimeException("Grpc client host and port not found for provider: $providerId")

/**
 * Creates a new [StatusRuntimeException] with another exception and additional details.
 */
fun getStatusException(
    exception: Exception,
    code: Code,
    domain: String,
    reason: String = "",
    errorMetadata: Map<String, String> = emptyMap()
): StatusException {
    return ErrorInfo.newBuilder()
        .apply {
            this.reason = reason
            this.domain = domain
        }.putAllMetadata(errorMetadata).build()
        .let {
            com.google.rpc.Status.newBuilder()
                .apply {
                    this.code = code.number
                    message = exception.message ?: ""
                    addDetails(com.google.protobuf.Any.pack(it))
                }.build()
        }
        .let { StatusProto.toStatusException(it) }
}
