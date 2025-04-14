package com.plcoding.testingwithktor.domain

import com.plcoding.testingwithktor.domain.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
}