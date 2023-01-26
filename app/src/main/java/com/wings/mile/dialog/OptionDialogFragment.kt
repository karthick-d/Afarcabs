package com.wings.mile.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.databinding.FileChooserDialogBinding

class OptionDialogFragment(var onItemClicked: DashboardActivity, var visibility: Boolean, var value: Int) : DialogFragment() {

    private lateinit var dataBinding: FileChooserDialogBinding

    interface OnItemSelect {
        fun onGalleryButtonClicked()
        fun onCameraButtonClicked()
    }

    companion object {
        const val TAG = "OptionDialogFragment"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"

        fun newInstance(title: String, subTitle: String, activity: DashboardActivity): OptionDialogFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = OptionDialogFragment(
                onItemClicked = activity, visibility = false,
                value = 2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.file_chooser_dialog, container, false)
        if(!visibility){
            if(value==2){
                dataBinding.buttonGallery.visibility = View.VISIBLE
            }else {
                dataBinding.buttonGallery.visibility = View.GONE
            }
        }else{
             dataBinding.buttonGallery.visibility = View.VISIBLE
          }
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupView(view)
        setupClickListeners()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    /*  private fun setupView(view: View) {
          view.tvTitle.text = arguments?.getString(KEY_TITLE)
          view.tvSubTitle.text = arguments?.getString(KEY_SUBTITLE)
      }*/

    private fun setupClickListeners() {
        dataBinding.buttonCamera.setOnClickListener {
            onItemClicked.onCameraButtonClicked()
            dismiss()
        }

        dataBinding.buttonGallery.setOnClickListener {
            onItemClicked.onGalleryButtonClicked()
            dismiss()
        }
    }

}