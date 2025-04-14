package com.plcoding.testingwithktor.data

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String
)
