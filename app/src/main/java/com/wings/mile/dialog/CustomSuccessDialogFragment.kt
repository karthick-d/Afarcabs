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
import com.wings.mile.databinding.FragmentDialogSuccessBinding

class CustomSuccessDialogFragment(var onItemClicked: DashboardActivity, var titleText: Int = R.string.application_has_been_updated_successfully, var imgDialog: Int? = null) : DialogFragment() {

    interface OnItemSelectDialog {
        fun onOkButtonClicked()
    }
    private lateinit var dataBinding: FragmentDialogSuccessBinding

    companion object {

        const val TAG = "CustomSuccessDialogFragment"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"

        fun newInstance(title: String, subTitle: String, activity: DashboardActivity, titleText: Int): CustomSuccessDialogFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = CustomSuccessDialogFragment(onItemClicked = activity, titleText = titleText)
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
            R.layout.fragment_dialog_success, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupClickListeners(view)
    }

    private fun setupView() {
        if(imgDialog!=null) {
            dataBinding.imgDialog.setImageResource(imgDialog!!)
        }
        dataBinding.tvTitle.text = resources.getString(titleText)
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun setupClickListeners(view: View) {
        dataBinding.btnPositive.setOnClickListener {
            onItemClicked.onOkButtonClicked()
            dismiss()
        }
    }

}