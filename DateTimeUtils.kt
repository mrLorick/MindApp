package com.mindbyromanzanoni.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.text.format.DateUtils
import android.util.Log
import android.widget.EditText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
fun changeDateFormat(_datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy")
        val outputFormatDate = SimpleDateFormat("yyyy-MM-dd")
        val dateInput: Date = inputFormat.parse(_datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun customDateFormat(_datetime: String,inputFormat:String,outFormat:String): String {
    return try {
        val inputForDate = SimpleDateFormat(inputFormat)
        val outputFormatDate = SimpleDateFormat(outFormat)
        val dateInput= inputForDate.parse(_datetime )
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}
@SuppressLint("SimpleDateFormat")
fun convertChatDateUtcToLocal(date: String?): String {
    val oldFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    oldFormatter.timeZone = TimeZone.getTimeZone("UTC")
    val value: Date?
    var dueDateAsNormal = ""
    try {
        value = oldFormatter.parse(date)
        val newFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        newFormatter.timeZone = TimeZone.getDefault()
        dueDateAsNormal = newFormatter.format(value)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return dueDateAsNormal
}

fun getCurrentDate():String{
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date())
}

fun getDaysBetweenDates(firstDateValue: String, format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())

    val firstDate = sdf.parse(getCurrentDate())
    val secondDate = sdf.parse(firstDateValue)

    if (firstDate == null || secondDate == null)
        return 0.toString()

    return (((secondDate.time - firstDate.time) / (1000 * 60 * 60 * 24))).toString()
}

fun getTimeAgo(previousTime: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val previousDate = sdf.parse(previousTime)
    val currentTime = System.currentTimeMillis()

    val timeAgo = DateUtils.getRelativeTimeSpanString(
        previousDate.time,
        currentTime,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    )

    return timeAgo.toString()
}


@SuppressLint("StaticFieldLeak", "SimpleDateFormat")
fun getTimeInAgo(date: String?): String {
    val updatedTime = convertDateTimeUtcToLocal(date)
    var time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getCurrentDateTime()).time.minus(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updatedTime).time
    )
    Log.d("samklca8888888",time.toString())

    val days: Long = TimeUnit.MILLISECONDS.toDays(time)
    time -= TimeUnit.DAYS.toMillis(days)

    val hours: Long = TimeUnit.MILLISECONDS.toHours(time)
    time -= TimeUnit.HOURS.toMillis(hours)

    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(time)
    time -= TimeUnit.MINUTES.toMillis(minutes)

    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(time)
    time -= TimeUnit.SECONDS.toMillis(seconds)

    var str = ""
    when {
        days >= 1 -> {
            str = "$days days ago"
        }
        hours >= 1 -> {
            str = "$hours hours ago"
        }
        minutes >= 1 -> {
            str = "$minutes minutes ago"
        }
        seconds >= 1 -> {
            str = "$seconds seconds ago"
        }
    }
    return str
}


@SuppressLint("SimpleDateFormat")
fun convertDateTimeUtcToLocal(_datetime: String?): String {
    val value: Date?
    var dueDateAsNormal: String? = ""
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    outputFormat.timeZone = TimeZone.getDefault()
    value = inputFormat.parse(_datetime)
    dueDateAsNormal = outputFormat.format(value)
    Log.i("samklca8888888", "convertDateTimeUtcToLocal: $dueDateAsNormal")
    return dueDateAsNormal
}



fun getCurrentDateTime(): String {
    val calendar = Calendar.getInstance().time
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())
    return outputFormat.format(calendar)
}


@SuppressLint("StaticFieldLeak", "SimpleDateFormat")
fun getTimeInAgoRecentMessage(date: String): String {
    var time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getCurrentDateTimeWithSec())!!.time
        .minus(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)!!.time)

    val days: Long = TimeUnit.MILLISECONDS.toDays(time)
    time -= TimeUnit.DAYS.toMillis(days)

    val hours: Long = TimeUnit.MILLISECONDS.toHours(time)
    time -= TimeUnit.HOURS.toMillis(hours)

    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(time)
    time -= TimeUnit.MINUTES.toMillis(minutes)

    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(time)
    time -= TimeUnit.SECONDS.toMillis(seconds)

    var str = ""
    when {
        days >= 1 -> {
            str = "$days days ago"
        }
        hours >= 1 -> {
            str = "$hours hours ago"
        }
        minutes >= 1 -> {
            str = "$minutes minutes ago"
        }
        seconds >= 1 -> {
            str = "Just now"
        }
    }
    return str
}

@SuppressLint("SimpleDateFormat")
fun getCurrentDateTimeWithSec(): String {
    val calendar = Calendar.getInstance().time
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return outputFormat.format(calendar)
}


@SuppressLint("SetTextI18n")
fun EditText.showDobPickerDialog() {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
//    c.add(Calendar.DATE, -1)
    val dpd = DatePickerDialog(this.context, { _, year, monthOfYear, dayOfMonth ->
        val date = ("$dayOfMonth-${monthOfYear + 1}-$year")
        this.setText(changeDateFormat(date))
    }, year, month, day)
    dpd.datePicker.maxDate = c.timeInMillis
    dpd.show()
}

// Used to convert 24hr format to 12hr format with AM/PM values
fun updateTime(hour: Int, mins: Int): String {
    var hours = hour
    val timeSet: String
    when {
        hours > 12 -> {
            hours -= 12
            timeSet = "PM"
        }
        hours == 0 -> {
            hours += 12
            timeSet = "AM"
        }
        hours == 12 -> timeSet = "PM"
        else -> timeSet = "AM"
    }
    var minutes = ""
    minutes = if (mins < 10) "0$mins" else mins.toString()

    return StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString()
}

@SuppressLint("SimpleDateFormat")
fun getCurrentTimeWithDate(): String {
    val calendar = Calendar.getInstance().time
    val outputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
    return outputFormat.format(calendar)
}



fun convert24To12Hour(time24: String): String {
    val patternIn = "HH:mm" // Input pattern for 24-hour format
    val patternOut = "hh:mm a" // Output pattern for 12-hour format
    val sdfIn = SimpleDateFormat(patternIn, Locale.getDefault())
    val sdfOut = SimpleDateFormat(patternOut, Locale.getDefault())
    val date = sdfIn.parse(time24)
    return sdfOut.format(date).uppercase()
}

@SuppressLint("SimpleDateFormat")
fun convertDateFormatIntoTime(datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormatDate = SimpleDateFormat("HH:mm")
        val dateInput: Date = inputFormat.parse(datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun convertDateFormatMessage(datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormatDate = SimpleDateFormat("hh:mm a")
        val dateInput: Date = inputFormat.parse(datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}
@SuppressLint("SimpleDateFormat")
fun convertDateFormatMessage(datetime: Date): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
         inputFormat.timeZone = TimeZone.getTimeZone("UTC")
         return inputFormat.format(datetime)
      } catch (e: Exception) {
        ""
    }
}


@SuppressLint("SimpleDateFormat")
fun convertTimeLocalToUtc(datetime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("hh:mm a")
        val outputFormatDate = SimpleDateFormat("hh:mm a")
        outputFormatDate.timeZone = TimeZone.getTimeZone("UTC")
        val dateInput: Date? = inputFormat.parse(datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}
@SuppressLint("SimpleDateFormat")
fun convertMyDateFormatIntoTime(): String {
    return try {
        val calendar = Calendar.getInstance().time
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")
        return outputFormat.format(calendar)
    } catch (e: Exception) {
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun changeEventDateFormat(_datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy")
        val outputFormatDate = SimpleDateFormat("dd-MMM-yyyy")
        val dateInput: Date = inputFormat.parse(_datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}


@SuppressLint("SimpleDateFormat")
fun changeEventDateFormatIntoDate(_datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("dd-MMM-yyyy")
        val outputFormatDate = SimpleDateFormat("yyyy-MM-dd")
        val dateInput: Date = inputFormat.parse(_datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}


@SuppressLint("SimpleDateFormat")
fun changeDateOfBirthFormat(_datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy")
        val outputFormatDate = SimpleDateFormat("yyyy-MM-dd")
        val dateInput: Date = inputFormat.parse(_datetime)
        return outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}


@SuppressLint("SimpleDateFormat")
fun changeDateOfBirthFormatIntoNormal(_datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormatDate = SimpleDateFormat("dd-MM-yyyy")
        val dateInput: Date = inputFormat.parse(_datetime)
        return outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        ""
    }
}


fun changeEventDateIntoDates(_datetime: String?): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormatDate = SimpleDateFormat("EEE dd MMM")
        val dateInput: Date = inputFormat.parse(_datetime)
        outputFormatDate.format(dateInput)
    } catch (e: Exception) {
        _datetime.toString()
    }
}

@SuppressLint("SetTextI18n")
fun EditText.showDatePickerDialog() {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val dpd = DatePickerDialog(this.context, { _, year, monthOfYear, dayOfMonth ->
        val date = ("$dayOfMonth-${monthOfYear + 1}-$year")
        this.setText(changeEventDateFormat(date))
    }, year, month, day)
    dpd.datePicker.minDate = c.timeInMillis
    dpd.show()
}


fun convert12To24Hour(time12: String): String {
    val patternIn = "hh:mm a" // Input pattern for 12-hour format
    val patternOut = "HH:mm" // Output pattern for 24-hour format
    val sdfIn = SimpleDateFormat(patternIn, Locale.getDefault())
    val sdfOut = SimpleDateFormat(patternOut, Locale.getDefault())
    val date = sdfIn.parse(time12)
    return sdfOut.format(date)
}

fun changeHourFormat(hour: String): String {
    return if (hour.length == 1) {
        "0$hour"
    } else {
        hour
    }
}


fun outdoorRB(timeString: String, selectedDate: String): Boolean {
    return try {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val date1 = simpleDateFormat.parse(selectedDate.plus(" ").plus(timeString.lowercase()))
        val date2 = simpleDateFormat.parse(getCurrentTimeWithDate())
        date2.time < date1.time
    } catch (e: Exception) {
        true
    }
}


@SuppressLint("SimpleDateFormat")
fun convertDateToTime(date: String): String? {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val da = df.parse(date)?.let { Date(it.time) }
    val format = SimpleDateFormat("HH:mm")
    return da?.let { format.format(it) }
}

@SuppressLint("SimpleDateFormat")
fun convertTimeToDate(date: String): String? {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val da = df.parse(date)?.let { Date(it.time) }
    val format = SimpleDateFormat("dd")
    return da?.let { format.format(it) }
}

@SuppressLint("SimpleDateFormat")
fun convertTimeToWithMonth(date: String): String? {
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val da = df.parse(date)?.let { Date(it.time) }
    val format = SimpleDateFormat("MMMM")
    return da?.let { format.format(it) }
}

fun convertTimeToMilliSecond(dateTime :String,format:String) : Long {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    val quizStartTime: Date = dateFormat.parse(dateTime) as Date
    return quizStartTime.time
}
fun convertMilliseconds(timeInMilli: Long ,convertedTime:(Long,Long,Long)->Unit){
    val totalSeconds = timeInMilli / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    convertedTime.invoke(hours,minutes,seconds)
}

fun String.convertDateFormat(inputFormat: String, outputFormat: String): String {
    val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())

    val date: Date = inputDateFormat.parse(this) ?: return ""

    return outputDateFormat.format(date)
}

fun String.convertTimeFormat(inputFormat: String, outputFormat: String): String {
    val inputTimeFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    val outputTimeFormat = SimpleDateFormat(outputFormat, Locale.getDefault())

    val time: Date = inputTimeFormat.parse(this) ?: return ""

    return outputTimeFormat.format(time)
}

fun formatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("dd/MM/yy")
    val dateInput: Date = inputFormat.parse(inputDate)
    return outputFormat.format(dateInput)
}

fun changeDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val outputFormat = SimpleDateFormat("yyyy-MM-dd")
    val dateInput: Date = inputFormat.parse(inputDate)
    return outputFormat.format(dateInput)
}

fun getCurrentDateFormat(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Calendar.getInstance().time
    return dateFormat.format(currentDate)
}

