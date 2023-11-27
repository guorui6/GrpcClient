package com.me.grpcclient

import android.net.Uri
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.example.model.Product
import org.example.model.ProductServiceGrpc
import org.example.model.SizeRequest
import java.io.Closeable

class ProductGrpcService(uri: Uri) : Closeable {

    private val channel by lazy {
        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }
        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val stub by lazy {
        ProductServiceGrpc.newBlockingStub(channel)
            .withMaxInboundMessageSize(Int.MAX_VALUE)
            .withMaxOutboundMessageSize(Int.MAX_VALUE)
    }

    private val _result = MutableSharedFlow<String>()
    val result: SharedFlow<String> = _result

    suspend fun getProducts(size: Int) {
        val request = SizeRequest.newBuilder().setSize(size).build()
        try {
            val response = stub.getProducts(request)
            _result.emit(response.productList.joinToString(", ") { it.name })
        } catch (e: Exception) {
            _result.emit(e.message ?: "error")
        }
    }

    override fun close() {
        channel.shutdown()
    }
}