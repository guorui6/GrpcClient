package com.me.grpcclient.rest

import com.me.grpcclient.model.EmptyResponse
import com.me.grpcclient.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RestService {
    @GET("/products/{size}")
    suspend fun getProducts(@Path("size") size: Int): Response<EmptyResponse>
}