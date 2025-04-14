package com.plcoding.testingwithktor.data

import com.plcoding.testingwithktor.domain.Product
import com.plcoding.testingwithktor.domain.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

class KtorProductRepository(
    private val client: HttpClient
): ProductRepository {

    override suspend fun getProducts(): List<Product> {
        val response = try {
            client.get(
                urlString = "https://dummyjson.com/products"
            )
        } catch(e: UnresolvedAddressException) {
            return emptyList()
        }

        if(response.status.value >= 400) {
            return emptyList()
        }

        val body = try {
            response.body<ProductsDto>()
        } catch(e: SerializationException) {
            return emptyList()
        }

        return body.products.map {
            Product(
                id = it.id,
                title = it.title,
                description = it.description
            )
        }
    }
}