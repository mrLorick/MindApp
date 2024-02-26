package com.mindbyromanzanoni.utils


import android.annotation.SuppressLint
import android.app.Activity
import com.mindbyromanzanoni.R
import org.aviran.cookiebar2.CookieBar


fun showBarAlert(activity: Activity?, title: String?, message: String?, icon: Int, gravity: Int) {
    CookieBar.build(activity)
        .setTitle(title)
        .setTitleColor(R.color.white)
        .setBackgroundColor(R.color.app_color)
        .setIcon(icon)
        .setCookiePosition(gravity)
        .setMessage(message)
        .setMessageColor(R.color.white)
        .setDuration(3000) // 3 seconds
        .show()
}
fun showErrorSnack(activity :Activity,message:String?){
    showErrorBarAlert(activity, activity.getString(R.string.error), message)
}
fun showErrorBarAlert(activity: Activity?, title: String?, message: String?) {
    CookieBar.build(activity)
        .setTitle(title)
        .setTitleColor(R.color.white)
        .setBackgroundColor(R.color.red)
        .setIcon(R.drawable.ic_invalid_24)
        .setCookiePosition(CookieBar.TOP)
        .setMessage(message)
        .setMessageColor(R.color.white)
        .setDuration(3000)
        .show()
}

@SuppressLint("ResourceType")
fun showSuccessBarAlert(activity: Activity?, title: String?, message: String?) {
    CookieBar.build(activity)
        .setTitle(title)
        .setTitleColor(R.color.white)
        .setBackgroundColor(R.color.app_color)
        .setCookiePosition(CookieBar.TOP)
        .setMessage(message)
        .setMessageColor(R.color.white)
        .setDuration(3000) // 3 seconds
        .show()
}

/*
fun customSuccessSnackBar(activity: Activity?,title: String?, message: String?){
    CookieBar.build(activity)
        .setCustomView(R.layout.custom_snackbar_error)
        .setSwipeToDismiss(false)
        .setCookiePosition(CookieBar.TOP)
        .setMessage(message)
        .setTitle(title)
        .setMessageColor(R.color.white)
        .setDuration(3000) // 3 seconds
        .show()
}*/
