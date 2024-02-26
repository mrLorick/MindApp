package com.mindbyromanzanoni.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.Geocoder
import android.location.Location
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.validators.Validator
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


fun Activity.fullScreen() {

    if (Build.VERSION.SDK_INT in 21..29) {
        window.statusBarColor = Color.TRANSPARENT
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    } else if (Build.VERSION.SDK_INT >= 30) {
        window.statusBarColor = Color.TRANSPARENT
        // Making status bar overlaps with the activity
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

fun Activity.fullScreenWhite() {
    if (Build.VERSION.SDK_INT in 21..29) {
        window.statusBarColor = Color.TRANSPARENT
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false

    } else if (Build.VERSION.SDK_INT >= 30) {
        window.statusBarColor = Color.TRANSPARENT
        // Making status bar overlaps with the activity
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

fun View.slideToLeftAnimation() {
    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_to_left))
    visibility = View.GONE
}

fun View.slideFromLeftAnimation() {
    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_from_left))
    visibility = View.VISIBLE
}

fun AlertDialog.hideKeyboardForAlertDialog(context: InputMethodManager?) {
    val view: View? = this.currentFocus
    if (view != null) {
        context!!.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


fun Activity.transparentStatusBar(isTransparent: Boolean, fullscreen: Boolean) {
    if (isTransparent) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        (this as AppCompatActivity).supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
    } else {
        if (fullscreen) {
            val decorView = window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        } else {
            (Objects.requireNonNull(this) as AppCompatActivity).supportActionBar?.show()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.statusBarColor = Color.WHITE
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                true
        }
    }
}

fun View.slideToRightAnimation() {
    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_to_right))
    visibility = View.GONE
}

fun View.slideFromRightAnimation() {
    startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_from_right))
    visibility = View.VISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}


fun Activity.showToast(message: String?) {
    android.os.Handler(Looper.getMainLooper()).post {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun getCurrentDate(format: String): String {
    val sdf = SimpleDateFormat(format, Locale.ENGLISH)
    return sdf.format(Date())
}

fun Activity.setStatusBarHideBoth(){
    with(window) {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.overrideImageStatusBar(context: Context){
    window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            )
    window.statusBarColor = Color.TRANSPARENT
}


@SuppressLint("SimpleDateFormat")
fun View.showTimePicker(callback: (String) -> Unit) {
    val datetime: Calendar = Calendar.getInstance()
    val timePicker = TimePickerDialog(
        this.context,
        { _, hourOfDay, minute ->
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            datetime.set(Calendar.MINUTE, minute)
            val formatter24Hours = SimpleDateFormat("HH:mm")
            val formatted24Time = formatter24Hours.format(datetime.time)
            callback.invoke(formatted24Time)
        },
        datetime.get(Calendar.HOUR_OF_DAY),
        datetime.get(Calendar.MINUTE),
        false
    )
    timePicker.show()
}


//fun progressBar(context: Context): CircularProgressDrawable {
//    val circularProgressDrawable = CircularProgressDrawable(context)
//    circularProgressDrawable.strokeWidth = 5f
//    circularProgressDrawable.centerRadius = 30f
//    return circularProgressDrawable
//}



/**
 * this function is use to get file path from uri
 * @param uri
 * @param activity
 * */
@SuppressLint("Recycle")
fun getFilePath(activity: Activity, uri: Uri): File {
    val parcelFileDescriptor = activity.contentResolver.openFileDescriptor(uri, "r", null)
    val file = File(activity.cacheDir, activity.contentResolver.getFilename(uri))
    val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    return file
}

@SuppressLint("Range")
fun ContentResolver.getFilename(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

fun String.toToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun EditText.showKeyboard(show: Boolean = true) {
    if (show) {
        requestFocus()
        val imm: InputMethodManager? =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    } else {
        clearFocus()
        val imm: InputMethodManager? =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}


/**
 * get address string from location object
 */
fun Context.getAddressFromLocation(location: Location?): String {
    if (location != null) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            return addresses[0].getAddressLine(0)
        }
        return ""
    }
    return ""
}

fun Context.getAddressFromLatLong(lat: Double?, lng: Double?): String {
    if (lat != null) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(lat, lng!!, 1)
        if (addresses?.isNotEmpty() == true) {
            return addresses[0].getAddressLine(0)
        }
        return ""
    }
    return ""
}


/** Convert to json */
inline fun <reified T>convertToJson() : String{
    return Gson().toJson(T::class.java)
}

inline fun <reified T> jsonToClass(data: String): T? {
    return Gson().fromJson<T>(data, object : TypeToken<T>() {}.type)
}

fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


fun Activity.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as
            InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


/**
 * Compress image
 * @param imageFile
 * @param activity
 * @return file
 * */
suspend fun compressImage(
    imageFile: File?,
    activity: Activity
): File? {
    return try {
        imageFile?.let { Compressor.compress(activity, it) }
    } catch (exception: Exception) {
        imageFile
    }
}


fun launchZoomApp(context: Context) {
    val packageName = "us.zoom.videomeetings" // Zoom app package name

    val appIntent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (appIntent != null) {
        // Zoom app is installed, launch the app
        context.startActivity(appIntent)
        Log.d("ksdnkn","knfknkn")
    } else {
        // Zoom app is not installed, redirect to the Play Store to download it
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Play Store app is not installed, open Play Store in browser
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            context.startActivity(intent)
        }
    }
}

fun startZoomMeeting(context: Context, meetingId: String) {
    val zoomUrl = "zoomus://zoom.us/join?confno=$meetingId"
    val packageName = "us.zoom.videomeetings"

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(zoomUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Play Store app is not installed, open Play Store in browser
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        context.startActivity(intent)
    }
}

// Extension function for EditText to apply the text watcher and filter functionality
fun EditText.applyTextWatcherAndFilter(validator: Validator, image : ImageView) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            validateEmail(validator , image)
        }
    }
    this.addTextChangedListener(watcher)
}

// Function to filter text based on search query
private fun EditText.validateEmail(validator: Validator , image: ImageView) {
    val query = this.text.toString()
    val validColor = ContextCompat.getColor(context, R.color.theme_color_green)
    val invalidColor = ContextCompat.getColor(context, R.color.black)
    if (query.isEmpty()) {
        setTickColor(invalidColor, image)
    } else if (validator.isValidEmailId(query)) {
        setTickColor(validColor, image)
    } else {
        setTickColor(invalidColor, image)
    }
}

// Function to set tick color for validation
private fun setTickColor(color: Int, image: ImageView) {
    image.setColorFilter(color, PorterDuff.Mode.SRC_IN)
}



fun Activity.openChromeTab(url: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

fun getTimeFromAudioOrVideo(url:String): String {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(url)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val duration = durationStr!!.toInt() // Duration in milliseconds
        var seconds = duration / 1000
        val minutes = seconds / 60
        seconds %= 60
        String.format("%d:%02d", minutes, seconds)
    }catch (_:Exception){
        ""
    }
}

fun Activity.copyUrl(url:String) {
    if (url.isNotBlank()) {
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(android.R.attr.label.toString(), url)
        clipboard.setPrimaryClip(clip)
        showSuccessBarAlert(this, "Copy", "Copy to Clipboard ! ")
    }
}
// Extension function for EditText to apply the text watcher and password validation
fun EditText.applyTextWatcherForPasswordValidate(validator: Validator, text : TextView) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            validatePassword(validator , text)
        }
    }
    this.addTextChangedListener(watcher)
}

// Function to hide and display password condition according to password validation
private fun EditText.validatePassword(validator: Validator , text: TextView) {
    val query = this.text.toString()
    if (query.isEmpty()) {
        text.visible()
    } else if (validator.isValidPasswordFormat(query)) {
        text.gone()
    } else {
        text.visible()
    }
}

fun getFirstWordOrInt(text:String) : Int{
    var intValue = 0
    val words = text.split("\\s+".toRegex())
    if (words.isNotEmpty()) {
        val firstWord = words[0]
        val numericValue = firstWord.takeWhile { it.isDigit() } // Extract numeric part
        intValue = numericValue.toIntOrNull() ?: 0 // Convert the extracted string to an integer
    }
    Log.d("knfkdsnsdsdsd",intValue.toString())
    return intValue
}

// Extension function for EditText to set up text watcher
fun EditText.setSearchTextWatcher(callback: (String) -> Unit) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            callback(p0.toString())
        }
    }
    this.addTextChangedListener(watcher)
}

fun ShimmerFrameLayout.shimmerAnimationEffect(status:Boolean){
    if (status) {
        startShimmer()
        visible()
    } else {
        stopShimmer()
        gone()
    }
}


fun Int.isPositive(): Boolean = this > 0

fun Int.isNegative(): Boolean = this < 0

fun Int.isZero(): Boolean = this == 0

fun Double.isPositive(): Boolean = this > 0.0

fun Double.isNegative(): Boolean = this < 0.0

fun Double.isZero(): Boolean = this == 0.0

fun String.removeLeadingAndTrailingSpaces(): String {
    var startIndex = 0
    var endIndex = length - 1

    // Find the index of the first non-space character from the start
    while (startIndex < length && this[startIndex] == ' ') {
        startIndex++
    }

    // Find the index of the first non-space character from the end
    while (endIndex >= 0 && this[endIndex] == ' ') {
        endIndex--
    }

    // Return an empty string if all characters are spaces
    if (startIndex > endIndex) {
        return ""
    }

    // Return the substring with leading and trailing spaces removed
    return substring(startIndex, endIndex + 1)
}

fun getFireBaseToken(data: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        } else {
            data(task.result)
        }
    })
}

// Extension function for EditText to set up text watcher
fun EditText.setAfterTextChangeWatcher(callback: (String) -> Unit) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            callback(p0.toString())
        }
    }
    this.addTextChangedListener(watcher)
}

/** higher-order functions */
fun calculateSum(x: Int, y: Int, operation: (Int, Int) -> Int): Int {
    return operation(x, y)
}

/** higher-order functions */
fun calculateSubtract(x: Int, y: Int): Int {
    return x-y
}
