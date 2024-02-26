package com.mindbyromanzanoni.utils

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.imageview.ShapeableImageView
import com.mindbyromanzanoni.R


fun previewImageAlertDialog(activity: Activity, image: String?){
    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(activity, R.drawable.cstm_alert_dialog))
    dialog.setCancelable(true)
    dialog.setContentView(R.layout.dialog_image_preview)
    val dismissBtn = dialog.findViewById(R.id.ivDismiss) as ImageView
    val imageView = dialog.findViewById(R.id.appCompatImageView11) as ShapeableImageView
    val progressBar = dialog.findViewById(R.id.progressBar) as ProgressBar
    imageView.setImageFromUrl(image,progressBar)
    dismissBtn.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}
