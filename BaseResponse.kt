package com.mindbyromanzanoni.base

abstract class BaseResponse {
    val success: Boolean = false
    val message: String? = null
    val status_code: Int? = null

    fun isSuccess(): Boolean {
        return success
    }
}