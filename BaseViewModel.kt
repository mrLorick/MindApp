
package com.mindbyromanzanoni.base

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job


/**
 * [BaseViewModel]
 * this class is use to perform some common task that is similar for all View Modals
 * */

open class BaseViewModel : ViewModel() {

    val toolbarTitle = ObservableField("Home")

    val toolbarTitleIsVisible = ObservableField(false)

    val isReportIconVisible = ObservableField(false)

    val ivAddButtonVisible = ObservableField(false)

    val onErrorToast = MutableLiveData<String>()

    private var job: Job? = null

    override fun onCleared() {
        super.onCleared()
        if (job != null) {
            job?.cancel()
        }
    }

    var showLoading : MutableLiveData<Boolean> = MutableLiveData()

    open fun onBackPress(view: View) {
        (view.context as AppCompatActivity).onBackPressed()
    }

    open fun onSettingClick(view: View) {}
    open fun onReportClick(view: View) {}
    open fun onNotificationClick(view: View) {}
    open fun onProfileClick(view: View) {}

  /*  fun showProgress(context: Context) {
        MyProgressBar.showProgress(context)
    }

    fun hideProgress() {
        MyProgressBar.hideProgress()
    }*/
}
