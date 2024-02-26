package com.mindbyromanzanoni.genrics

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    var apiType: String = "",
    var responseCode: String = "") {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(
        message: String,
        data: T? = null,
        apiType: String = "",
        responseCode: String = "") :
        Resource<T>(data, message, apiType,responseCode)
}


