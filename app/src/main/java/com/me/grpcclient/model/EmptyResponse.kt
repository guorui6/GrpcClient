package com.me.grpcclient.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ProductsResponse(
    val products: List<Product>
)