package com.mindbyromanzanoni.retrofit


import android.content.Context
import com.mindbyromanzanoni.R
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @param context
 * [@ApplicationContext]
 * this Function is Design for handle the network exception
 *
 * */

@Singleton
class NetworkErrorHandlerImpl @Inject constructor(@ApplicationContext var context: Context) {

    fun getErrorMessage(e: Exception): String {
        var error = ""
        try {
            error = when (e) {
                is CancellationException -> e.toString()
                is HttpException -> e.message().toString()
                is ConnectException -> context.getString(R.string.connectErr)
                is SocketTimeoutException -> context.getString(R.string.timeoutErr)
                is UnknownHostException -> context.getString(R.string.noInternetErr)
                is IllegalArgumentException -> context.getString(R.string.something_went_wrong)
                is NullPointerException -> "NullPointerException"
                else -> context.getString(R.string.something_went_wrong)
            }
        } catch (e: IOException) {
            e.printStackTrace()
             return  error
        }
        return error
    }
}