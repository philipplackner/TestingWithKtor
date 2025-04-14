package com.plcoding.testingwithktor.data

import com.plcoding.testingwithktor.data.ProductDto
import kotlinx.serialization.Serializable

@Serializable
data class ProductsDto(
    val products: List<ProductDto>
)
