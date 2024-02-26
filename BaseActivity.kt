package com.mindbyromanzanoni.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mindbyromanzanoni.utils.finishActivity

abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {
    fun hideKeyboard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
    @LayoutRes
    abstract fun getLayoutRes(): Int
    val binding by lazy {
        DataBindingUtil.setContentView(this, getLayoutRes()) as DB
    }
    abstract fun initView()
    abstract fun viewModel()
    @RequiresApi(Build.VERSION_CODES.R)
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        initView()
        viewModel()
    }
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishActivity()
        }
    }
}