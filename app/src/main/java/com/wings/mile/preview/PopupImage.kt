package com.wings.mile.preview

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.wings.mile.R
import com.wings.mile.databinding.PreviewImagesBinding
import java.io.File

class PopupImage(private var activity: FragmentActivity?,fileurl: String) : BaseObservable() {

    private var popupWindow: PopupWindow? = null

    private var fileurl: String? = fileurl

    var downloadID: Long = 0

    fun showFullImageView() {
        if (fileurl!!.isNotEmpty()) {

            val defectImageviewBinding: PreviewImagesBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.preview_images,
                null,
                false
            )
            popupWindow = PopupWindow(
                defectImageviewBinding.root,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                true
            )

//            val imgFile = bitmapImage?.let { File(it) }
//
//            // on below line we are checking if the image file exist or not.
//            if (imgFile!!.exists()) {
//
//                // on below line we are creating an image bitmap variable
//                // and adding a bitmap to it from image file.
//                val imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//
//                // on below line we are setting bitmap to our image view.
//                defectImageviewBinding.fullimageview.setImageBitmap(imgBitmap)
//            }

                    Glide.with(defectImageviewBinding.fullimageview).load(fileurl)
                        .into(defectImageviewBinding.fullimageview)


            defectImageviewBinding.previewViewModels  = this
            popupWindow!!.showAtLocation(defectImageviewBinding.root, Gravity.CENTER, 0, 0)
        }


    }


    fun onClickCloseBottom() {
        if (popupWindow != null) {
            popupWindow!!.isOutsideTouchable = true
            popupWindow!!.dismiss()
        }
    }
    fun downloadFiles() {
        var f: File? = null
        var ads: File? = null
        f = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + "AfarCabs"
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + "AfarCabs")
        }

        // Make sure the path directory exists.
        if (!f.exists()) {
            // Make it, if it doesn't exit
            val success = f.mkdirs()
            if (!success) {
                f = null
            }
        }
        ads = File(f,fileurl)
        if (ads.exists()) {
            ads.delete()
        }
        downloadFile(
            activity,
            fileurl,
            ads.name,
            "image/png"
        )
        Toast.makeText(activity,"File downloading.....",Toast.LENGTH_SHORT).show()
    }

    protected fun downloadFile(
        activity: Context?,
        url: String?,
        fileName: String,
        mimeType: String?
    ) {
        try {
            if (url != null && !url.isEmpty()) {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + "Genesis" + File.separator + fileName
                // File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Genesis");
                //should be local path of downloaded video
                val fileWithinMyDir = File(path)
                fileWithinMyDir.deleteOnExit()
                Log.w("path", url)
                val uri = Uri.parse(url)
                val request = DownloadManager.Request(uri)
                request.setTitle(fileName)
                request.setDescription("Downloading...")
                request.allowScanningByMediaScanner()
                request.setMimeType(mimeType)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/Afarcabs/$fileName"
                )
                val dm = activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                // dm.enqueue(request);
                downloadID = dm.enqueue(request) // enqueue puts the download request in the queue.

            }
        } catch (e: IllegalStateException) {
            Toast.makeText(
                activity,
                "Please insert an SD card to download file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}









