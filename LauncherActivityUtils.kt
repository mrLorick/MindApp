package com.mindbyromanzanoni.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mindbyromanzanoni.R

inline fun <reified T : Any> Activity.launchActivity (
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    if(options!=null)
        startActivityForResult(intent.putExtras(options), requestCode)
    else{
        startActivityForResult(intent, requestCode)
    }
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

}


inline fun <reified T : Any> Activity.launchActivityVideoZoom (
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    if(options!=null)
        startActivityForResult(intent.putExtras(options), requestCode)
    else{
        startActivityForResult(intent, requestCode)
    }
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun Activity.finishActivityVideoZoom(){
    finish()
    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
}


inline fun <reified T : Any> Context.launchActivity (
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {})
{
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}


inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)

fun <T> hasDuplicates(arr: Array<T>): Boolean {
    return arr.size != arr.distinct().count()
}

fun Activity.finishActivity(){
    finish()
    overridePendingTransition(R.anim.slide_in_right_launch, R.anim.slide_out_right_launch)
}

fun Activity.finishActivity(key :String,value: Any){
    val resultIntent = Intent()
    when (value) {
        is Int -> resultIntent.putExtra(key, value)
        is Float -> resultIntent.putExtra(key, value)
        is Long -> resultIntent.putExtra(key, value)
        is Boolean -> resultIntent.putExtra(key, value)
        is String -> resultIntent.putExtra(key, value)
    }
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
    overridePendingTransition(R.anim.slide_in_right_launch, R.anim.slide_out_right_launch)
}

