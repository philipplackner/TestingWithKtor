@file:OptIn(ExperimentalCoroutinesApi::class)

package com.plcoding.testingwithktor.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import com.plcoding.testingwithktor.data.HttpClientFactory
import com.plcoding.testingwithktor.data.KtorProductRepository
import com.plcoding.testingwithktor.data.http_util.HttpResponseData
import com.plcoding.testingwithktor.data.http_util.ProductsResponses
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductsViewModelTest {

    private lateinit var viewModel: ProductsViewModel
    private lateinit var repository: KtorProductRepository
    private lateinit var httpClient: HttpClient
    private val testDispatcher = UnconfinedTestDispatcher()

    private var responseData = HttpResponseData(
        content = ProductsResponses.valid,
        statusCode = HttpStatusCode.OK
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        httpClient = HttpClientFactory.create(
            engine = MockEngine.create {
                dispatcher = testDispatcher
                addHandler { request ->
                    val relativeUrl = request.url.encodedPath
                    when(relativeUrl) {
                        "/products" -> respond(
                            content = responseData.content,
                            status = responseData.statusCode,
                            headers = headers {
                                set("Content-Type", "application/json")
                            }
                        )
                        else -> respond(
                            content = "Not mocked",
                            status = HttpStatusCode.NotFound
                        )
                    }
                }
            }
        )
        repository = KtorProductRepository(httpClient)
        viewModel = ProductsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Products are correctly loaded for successful API call`() = runBlocking {
        viewModel.products.test {
            val initialEmission = awaitItem()
            assertThat(initialEmission).isEmpty()

            val loadedProducts = awaitItem()
            assertThat(loadedProducts).hasSize(30)
        }
    }

    @Test
    fun `API error returns empty products list`() = runBlocking {
        responseData = HttpResponseData(
            content = "error",
            statusCode = HttpStatusCode.Forbidden
        )
        viewModel.products.test {
            val initialProducts = awaitItem()
            assertThat(initialProducts).isEmpty()

            expectNoEvents()
        }
    }
}