package com.mindbyromanzanoni.utils

import android.app.Dialog
import android.content.Context
import android.os.Handler
import com.mindbyromanzanoni.R

object MyProgressBar {
    private var progressDialog: Dialog? = null
    private val mHandler by lazy { Handler() }

    //todo show progress
    fun showProgress(context: Context) {
        if (!isShowProgress()) {
            try {
                progressDialog = Dialog(context, R.style.progressBarDialog)
                progressDialog?.setContentView(R.layout.progress_dialog)
                progressDialog?.setCanceledOnTouchOutside(false)
                progressDialog?.setCancelable(false)
                progressDialog?.show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //todo hide progress
    fun hideProgress() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog?.cancel()
            }
        } catch (i: IllegalArgumentException) {
            i.printStackTrace()
        }
    }

    //todo check progress bar is showing
    private fun isShowProgress(): Boolean {
        return progressDialog != null && progressDialog!!.isShowing
    }

}