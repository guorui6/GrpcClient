package com.me.grpcclient.di

import com.lime.supply.grpc.model.MapViewServiceGrpc
import com.lime.supply.grpc.model.MapViewServiceGrpc.MapViewServiceStub
import com.me.grpcclient.rest.RestService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.example.model.ProductServiceGrpc
import org.example.model.ProductServiceGrpc.ProductServiceBlockingStub
import org.example.model.ProductServiceGrpc.ProductServiceStub
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val ADDRESS = "192.168.1.143"
private const val PORT_GRPC = 9090
private const val PORT_REST = 8080

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
    fun provideMapViewServiceAsync(channel: ManagedChannel): MapViewServiceStub {
        return MapViewServiceGrpc.newStub(channel)
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

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://$ADDRESS:$PORT_REST/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProductRestfulApi(retrofit: Retrofit): RestService{
        return retrofit.create(RestService::class.java)
    }
}