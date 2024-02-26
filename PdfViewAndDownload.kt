package com.mindbyromanzanoni.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.util.Locale

class PdfViewAndDownload {
    fun startDownload(context: Context,pdfUrl:String,pdfFileName:String) {
       val newFileName = pdfFileName.replace(" ","_").trim().lowercase(Locale.ROOT)
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
            .setTitle("${newFileName}.pdf")
            .setDescription("Downloading a PDF file")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true) // Set to true if you want to allow download over mobile data
            .setAllowedOverRoaming(true) // Set to true if you want to allow download while roaming

        // Specify the local destination for the downloaded PDF
        val destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(destination, pdfFileName)
        request.setDestinationUri(Uri.fromFile(file))

        // Get the DownloadManager service and enqueue the request
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)
        // You can save the downloadId and use it to track the download progress or retrieve the downloaded file later.
    }

}