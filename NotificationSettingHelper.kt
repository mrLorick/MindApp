package com.mindbyromanzanoni.utils

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.Field

class NotificationSettingHelper {
    private val CHECK_OP_NO_THROW = "checkOpNoThrow"
    private val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

    fun intentNotificationSetting(activity: Activity,intent:Intent){
        val settingsIntent: Intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
                    .putExtra(Settings.EXTRA_CHANNEL_ID, "MY_CHANNEL_ID")
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.setData(uri)
            }
//        getResult.launch(settingsIntent)

        Log.d("GET_RESULT_NOTIFICATION",isNotificationEnabled(activity.applicationContext).toString())
    }

    private fun isNotificationEnabled(mContext: Context): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            val mAppOps = mContext.getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
            val appInfo = mContext.applicationInfo
            val pkg = mContext.applicationContext.packageName
            val uid = appInfo.uid
            val appOpsClass: Class<*>
            try {
                appOpsClass = Class.forName(AppOpsManager::class.java.name)
                val checkOpNoThrowMethod = appOpsClass.getMethod(
                    CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String::class.java
                )
                val opPostNotificationValue: Field =
                    appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
                val value = opPostNotificationValue.get(Int::class.java) as Int
                return checkOpNoThrowMethod.invoke(
                    mAppOps, value, uid,
                    pkg
                ) as Int == AppOpsManager.MODE_ALLOWED
            } catch (ex: ClassNotFoundException) {
                false
            }
        } else {
            false
        }
    }
}