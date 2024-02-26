package com.mindbyromanzanoni.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String.createPartFromString(): RequestBody {
    return this.toRequestBody(MultipartBody.FORM)
}

fun toRequestBodyProfileNullImage(value: String?, key: String): MultipartBody.Part {
    val data = value!!.toString().toRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(key, null, data)
}
 fun String.createPartFromJsonString(): RequestBody {
    val mediaType: MediaType? = "application/json".toMediaTypeOrNull()
    return this.toRequestBody(mediaType)
}
