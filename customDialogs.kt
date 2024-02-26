package com.mindbyromanzanoni.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.data.response.meditation.MeditationTypeListResponse
import com.mindbyromanzanoni.databinding.ComingSoonDialogBinding
import com.mindbyromanzanoni.databinding.MediaImagePickerBottomSheetBinding
import com.mindbyromanzanoni.databinding.MessageDialogBinding
import com.mindbyromanzanoni.databinding.SaveJournalDataDialogBinding
import com.mindbyromanzanoni.view.activity.messageList.MessageListActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


fun Activity.openMessageListDialog(
    viewModal: HomeViewModel, activity: MessageListActivity,
    callBack: (Any) -> Unit
) {
    val builder = AlertDialog.Builder(this, R.style.FullHeightDialog)
    val binding = MessageDialogBinding.inflate(layoutInflater)
    builder.setView(binding.root)
    val alertDialog = builder.create()
    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.setCancelable(true)
    alertDialog.setCanceledOnTouchOutside(true)
    alertDialog.show()
    getWatcherSearchMeditation(binding)
    binding.mainLayer.setOnClickListener {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        alertDialog.hideKeyboardForAlertDialog(imm)
    }
    binding.ivImg.setOnClickListener {
        binding.etSearch.text?.clear()
    }
}

/** Text watcher when user search Message list*/

fun getWatcherSearchMeditation(binding: MessageDialogBinding) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            filter(p0.toString(), binding)
        }


    }
    binding.etSearch.addTextChangedListener(watcher)
}

private fun filter(query: String, binding: MessageDialogBinding) {
    val list: ArrayList<MeditationTypeListResponse>
    if (query.isEmpty()) {
        binding.ivImg.setImageResource(R.drawable.search_normal)
    } else {
        binding.ivImg.setImageResource(R.drawable.rating_close)
    }

}

fun Activity.openComingSoonDialog(callBack: (status: Boolean) -> Unit) {
    val builder = AlertDialog.Builder(this, R.style.FullHeightDialog)
    val binding = ComingSoonDialogBinding.inflate(layoutInflater)
    builder.setView(binding.root)
    val alertDialog = builder.create()
    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.setCancelable(true)
    alertDialog.setCanceledOnTouchOutside(true)
    alertDialog.show()

    binding.ivCross.setOnClickListener {
        alertDialog.dismiss()
    }

}


fun Activity.openAlertSaveJournalData(callBack: (status: Boolean) -> Unit) {
    val builder = AlertDialog.Builder(this, R.style.FullHeightDialog)
    val binding = SaveJournalDataDialogBinding.inflate(layoutInflater)
    builder.setView(binding.root)
    val alertDialog = builder.create()
    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.setCancelable(true)
    alertDialog.setCanceledOnTouchOutside(true)
    alertDialog.show()

    binding.btnCancel.setOnClickListener {
        callBack(false)
        alertDialog.dismiss()
    }
    binding.btnSave.setOnClickListener {
        callBack(true)
        alertDialog.dismiss()
    }

}


/**
 * Image picker bottom sheet
 * @param context
 * @param select selected type callback
 * */
fun imagePicker(
    context: Context,
    select: (selected: Int) -> Unit,
    title: String? = null,
    text1: String? = null,
    text2: String? = null
) {
    val bottomSheet = BottomSheetDialog(context)
    val view = MediaImagePickerBottomSheetBinding.inflate(LayoutInflater.from(context))
    bottomSheet.setContentView(view.root)
    bottomSheet.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val width = (context.resources.displayMetrics.widthPixels / 1.1).toInt()
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    bottomSheet.window?.setLayout(width, height)

    if (title != null) view.tvHeading.text = title
    if (text1 != null) view.tvCamera.text = text1
    if (text2 != null) view.tvGallery.text = text2


    view.tvCamera.setOnClickListener {
        select(0)
        bottomSheet.dismiss()
    }

    view.tvGallery.setOnClickListener {
        select(1)
        bottomSheet.dismiss()
    }
    bottomSheet.show()
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(selectedDate.time)
            // Call the callback function with the selected date
            onDateSelected(formattedDate)
        },
        year,
        month,
        day
    )
    datePickerDialog.show()
}
fun showDatePickerJournal(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(selectedDate.time)
            // Call the callback function with the selected date
            onDateSelected(formattedDate)
        },
        year,
        month,
        day)
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()
    datePickerDialog.show()
}


fun showDatePickerDate(
    context: Context,
    onDateSelected: (String, String, String) -> Unit,
    cancelCallBack: (Boolean) -> Unit
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)

            val years = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = years.format(selectedDate.time)
            // Call the callback function with the selected date
            onDateSelected(
                years.format(selectedDate.time),
                years.format(selectedDate.time),
                years.format(selectedDate.time)
            )
        },
        year,
        month,
        day
    )
    datePickerDialog.setOnCancelListener {
        cancelCallBack(true)
    }
    datePickerDialog.show()
}

fun Context.showTimePicker(
    hourOfDay: Int,
    minute: Int,
    is24HourView: Boolean = true,
    onTimeSetListener: (hourOfDay: Int, minute: Int) -> Unit
) {
    val timePickerDialog = TimePickerDialog(
        this, { _, selectedHour, selectedMinute ->
            onTimeSetListener(selectedHour, selectedMinute)
        },
        hourOfDay,
        minute,
        is24HourView
    )
    timePickerDialog.show()
}