package com.plcoding.testingwithktor.data.http_util

import io.ktor.http.HttpStatusCode

data class HttpResponseData(
    val content: String,
    val statusCode: HttpStatusCode
)
