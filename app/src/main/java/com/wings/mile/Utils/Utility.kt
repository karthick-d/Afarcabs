package com.wings.mile.Utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class Utility {

    interface UserPermissionCheck {
        fun getLoginUSerPermission(feature: String?): Boolean
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        lateinit var dialog: AlertDialog

        /**
         * reduces the size of the image
         * @param image
         * @param maxSize
         * @return
         */
        fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap? {
            var width = image?.width
            var height = image?.height
            val bitmapRatio = height?.toFloat()?.div(width!!.toFloat())
            if (bitmapRatio != null) {
                if (bitmapRatio > 1) {
                    width = maxSize
                    height = (width / bitmapRatio).toInt()
                } else {
                    height = maxSize
                    width = (height * bitmapRatio).toInt()
                }
            }
            return Bitmap.createScaledBitmap(image!!, width!!, height!!, true)
        }

        fun getFolderSize(file: File): Long {
            var size: Long = 0
            if (file.isDirectory) {
                for (child in file.listFiles()) {
                    size += getFolderSize(child)
                }
            } else {
                size = file.length()
            }
            return size
        }


        fun getImageUri(inContext: Context, inImage: Bitmap?): Uri {

            val tempFile = File.createTempFile("Title", ".png")
            val bytes = ByteArrayOutputStream()
            inImage!!.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val bitmapData = bytes.toByteArray()

            val fileOutPut = FileOutputStream(tempFile)
            fileOutPut.write(bitmapData)
            fileOutPut.flush()
            fileOutPut.close()
            return Uri.fromFile(tempFile)

            /*val bytes = ByteArrayOutputStream()
            inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                "Title",
                null
            )
            return Uri.parse(path)*/
        }
        @JvmStatic
        fun isAtLeastVersion(version: Int): Boolean {
            return Build.VERSION.SDK_INT >= version
        }

        fun getFolderSizeLabel(context: Context?, selectedImageUri: Uri?): Double {
            val filePath = FileUtils.getPath(context, selectedImageUri)
            val file = File(filePath)
            val size = (Utility.getFolderSize(file) / 1024).toFloat() // Get size and convert bytes into Kb.
            return size.toDouble()
        }

        fun Activity.hideSoftKeyboard() {
            currentFocus?.let {
                val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
        fun isConnected(context: Context): Boolean {
            val cm = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            val netinfo = cm.activeNetworkInfo
            return if (netinfo != null && netinfo.isConnectedOrConnecting) {
                val wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
            } else false
        }
    }

    fun savebitmap(bmp: Bitmap,value : Int, context: Context?): File {

        val pictureFile = getOutputMediaFile(context,value)
        if (pictureFile == null) {
            Log.d(
                "status",
                "Error creating media file, check storage permissions: "
            )

        }
        try {
            val fos = FileOutputStream(pictureFile)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d("status", "File not found: " + e.localizedMessage)
        } catch (e: IOException) {
            Log.d("status", "Error accessing file: " + e.localizedMessage)
        }

        return pictureFile!!
    }

    fun getFileFolder(context: Context?): File {

        return File(
            Environment.getExternalStorageDirectory()
                .toString() + "/Android/data/"
                    + context!!.applicationContext.packageName
                    + "/Files"
        )
    }


    /** Create a File for saving an image or video  */
    private fun getOutputMediaFile(context: Context?, value: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.


        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        val mediaStorageDir : File = getFileFolder(context)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        // Create a media file name
        val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mediaFile: File
        val mImageName = "TestImage_$value.jpg"
        mediaFile = File(mediaStorageDir.path + File.separator + mImageName)
        return mediaFile
    }
}




