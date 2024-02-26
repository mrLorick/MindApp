package com.mindbyromanzanoni.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Checking permission for use camera to capture image
 * @param context
 * @param permissionGranted callback permission is granted
 * @param permissionNotGranted callback permission not granted and permission list
 * */
fun checkCameraPermissionForCaptureImage(
    context: Context,
    permissionGranted: () -> Unit,
    permissionNotGranted: (permissionList: Array<String>) -> Unit
) {

    val isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    } else (ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED)

    if (isPermissionGranted) {
        permissionGranted.invoke()
        return
    }

    val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.CAMERA
        )
    } else arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    permissionNotGranted.invoke(
        permissionList
    )
}

/** *
 * Checking for gallery permission to pick image from gallery
 * @param context
 * @param permissionGranted callback if permission is granted
 * @param permissionNotGranted callback if permission is not granted and permission list
 */
fun checkingGalleryPermissionForPickImage(
    context: Context,
    permissionGranted: () -> Unit,
    permissionNotGranted: (permissionList: Array<String>) -> Unit
) {

    val isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        true
    } else ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    if (isPermissionGranted) {
        permissionGranted.invoke()
        return
    }

    val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    permissionNotGranted.invoke(
        permissionList
    )
}

/**
 * Record audio permission
 * @param context
 * @param isPermissionGranted callback when permission is granted
 * @param isPermissionNotGranted callback with permission list if permission is not granted
 * */
fun checkingForRecordAudiPermission(
    context: Context,
    isPermissionGranted: () -> Unit,
    isPermissionNotGranted: (permissionList: Array<String>) -> Unit
) {
    val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    } else ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

    if (permissionGranted) {
        isPermissionGranted.invoke()
        return
    }

    val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.RECORD_AUDIO)
    } else arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    isPermissionNotGranted.invoke(permissionList)
}

/**
 * Notification permission
 * @param context
 * @param isPermissionGranted callback when permission is granted
 * @param isPermissionNotGranted callback with permission list if permission is not granted
 * */
fun checkForNotificationPermission(
    context: Context,
    isPermissionGranted: () -> Unit,
    isPermissionNotGranted: (permission: String) -> Unit
) {
    val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true

    if (permissionGranted) {
        isPermissionGranted.invoke()
        return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        isPermissionNotGranted.invoke(Manifest.permission.POST_NOTIFICATIONS)
    }
}

/**
 * Checking for permission pick audio from the external storage
 * @param context
 * @param permissionGranted is permission granted
 * @param permissionNotGranted permission not granted and list of permission
 * */
fun checkForAudioPickerPermission(
    context: Context,
    permissionGranted: () -> Unit,
    permissionNotGranted: (permissionList: String) -> Unit
) {
    val isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    } else ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    if (isPermissionGranted) {
        permissionGranted.invoke()
        return
    }

    val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else Manifest.permission.READ_EXTERNAL_STORAGE

    permissionNotGranted.invoke(
        permissionList
    )
}

/**
 * Checking for permission pick audio from the external storage
 * @param context
 * @param isPermissionGranted is permission granted
 * @param isPermissionNotGranted permission not granted and list of permission
 * */
fun writeExternalStoragePermission(
    context: Context,
    isPermissionGranted: () -> Unit,
    isPermissionNotGranted: (permission: String) -> Unit
) {
    val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        true
    } else ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    if (permissionGranted) {
        isPermissionGranted.invoke()
        return
    }
    isPermissionNotGranted.invoke(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}