package com.wings.mile.firebase

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.Observer
import com.wings.mile.R
import com.wings.mile.databinding.FragmentOtpVerificationBinding


class OtpVerificationFragment: BaseFragment<FragmentOtpVerificationBinding, AuthActivityViewModel>() {

    companion object {
        fun newInstance(): OtpVerificationFragment {
            val args = Bundle()
            val fragment = OtpVerificationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModelClass(): Class<AuthActivityViewModel> = AuthActivityViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_otp_verification

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.processCountdownTick()

        val text: String = resources.getString(R.string.otp_auth_subtitle, viewModel.phone)
        binding.textViewSubtitleOtpAuth.text = text


        val spannable = SpannableStringBuilder("Didn't received OTP? Resend")
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primary_700)),
            spannable.indexOf("Resend"), spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textViewTicker.text = spannable

        viewModel.selectedOtpNumber.observe(
            requireActivity(),
            Observer<String?> { value ->
                binding.etOtp.setText(value ?: "")
            }
        )

        binding.buttonVerifyOtp.setOnClickListener {
            if (viewModel.checkIfOtpIsValid(binding.etOtp.text.toString())) {
                viewModel.verifyOtp(binding.etOtp.text.toString())
            } else {
                showSnackBar("Invalid Otp: Please enter correct OTP")
            }
        }

        viewModel.showResendCodeText.observe(
            requireActivity(),
            Observer<Boolean?> { value ->
                when(value) {
                    true -> binding.textViewTicker.visibility = View.VISIBLE
                    false -> binding.textViewTicker.visibility = View.GONE
                    else -> binding.textViewTicker.visibility = View.GONE
                }
            }
        )

        binding.textViewTicker.setOnClickListener {
            viewModel.resendOtp(requireActivity())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearCountdownTick()
    }
}