package com.wings.mile.preview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.wings.mile.R
import com.wings.mile.databinding.PreviewImageBinding
import java.io.File

class PopupImageView(private var activity: FragmentActivity?, imagePath: Bitmap,fileurl: String) : BaseObservable() {

    private var popupWindow: PopupWindow? = null

    private var bitmapImage: Bitmap? = imagePath
    private var fileurl: String? = fileurl



    fun showFullImageView() {
        if (bitmapImage != null) {

            val defectImageviewBinding: PreviewImageBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.preview_image,
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
                if(bitmapImage!=null) {
                    defectImageviewBinding.fullimageview.setImageBitmap(bitmapImage)

                }else{
                    defectImageviewBinding.fullimageview.setImageBitmap(bitmapImage)
                    Glide.with(defectImageviewBinding.fullimageview).load(fileurl)
                        .into(defectImageviewBinding.fullimageview)
                }

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

}
