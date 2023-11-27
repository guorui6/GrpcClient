package com.me.grpcclient.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val name: String,
    val year: Int,
    val price: Double,
    val quantity: Int,
)
