package com.wings.mile.Utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.DrawableRes

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.wings.mile.R
import com.wings.mile.databinding.ToastLayoutBinding

class CustomToast {
    companion object {
        private fun makeTopToast(
            context: Context,
            text: String,
            @DrawableRes icon: Int = R.drawable.ic_checks,
            type : Int
        ) {
            val binding = ToastLayoutBinding.inflate(LayoutInflater.from(context))
            binding.imgToast.setBackgroundResource(icon)
            binding.txtToast.text = text
            val scope = when (context) {
                is AppCompatActivity -> context.lifecycleScope
                is Fragment -> context.lifecycleScope
                else -> null
            }
            scope?.launchWhenResumed {
                Toast(context.applicationContext).apply {
                    setGravity(Gravity.CENTER or Gravity.FILL_HORIZONTAL, 0, 0)
                    duration = type
                    view = binding.root
                    show()
                }
            }
            if (scope == null) {
                Toast(context.applicationContext).apply {
                    setGravity(Gravity.CENTER or Gravity.FILL_HORIZONTAL, 0, 0)
                    duration = type
                    view = binding.root
                    show()
                }
            }

        }

        @JvmStatic
        fun warningToast(context: Context, text: String, type: Int = Toast.LENGTH_LONG) {
            try {
                if (text.isNotEmpty())
                    makeTopToast(
                            context, text,
                            R.drawable.ic_warning,
                            type
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        @JvmStatic
        fun successToast(context: Context, text: String,  type: Int = Toast.LENGTH_LONG) {
            try {
                if (text.isNotEmpty())
                    makeTopToast(
                            context, text,
                            R.drawable.ic_checks,
                            type
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
