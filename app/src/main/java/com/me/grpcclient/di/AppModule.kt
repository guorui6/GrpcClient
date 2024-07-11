package com.me.grpcclient.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.example.model.ProductServiceGrpc
import org.example.model.ProductServiceGrpc.ProductServiceBlockingStub
import org.example.model.ProductServiceGrpc.ProductServiceStub
import javax.inject.Singleton

private const val ADDRESS = "192.168.1.143"
private const val PORT_GRPC = 9090

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideGrpcChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress(ADDRESS, PORT_GRPC).usePlaintext().build()
    }

    @Provides
    @Singleton
    fun provideProductServiceAsync(channel: ManagedChannel): ProductServiceStub {
        return ProductServiceGrpc.newStub(channel)
    }

    @Provides
    @Singleton
    fun provideProductServiceBlock(channel: ManagedChannel): ProductServiceBlockingStub {
        return ProductServiceGrpc.newBlockingStub(channel)
    }
}