package com.wings.mile.firebase

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.awesomedialog.*
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.wings.mile.NavigationActivity
import com.wings.mile.R
import com.wings.mile.Utils.CustomToast
import com.wings.mile.Utils.Pref_storage

import com.wings.mile.databinding.FragmentPhoneVerificationBinding
import com.wings.mile.model.*
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory


class PhoneVerificationFragment :
    BaseFragment<FragmentPhoneVerificationBinding, AuthActivityViewModel>() {

    companion object {
        fun newInstance(): PhoneVerificationFragment {
            val args = Bundle()
            val fragment = PhoneVerificationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var viewModels: MainViewModel
    lateinit var savedrivers: getdriver
    lateinit var response: responseItem
    var flag: Boolean = false
    var flagcode: Boolean = false
    private val retrofitService = RetrofitService.getInstance()
    override fun getViewModelClass(): Class<AuthActivityViewModel> =
        AuthActivityViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_phone_verification

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.toolbarLayout.toolbarTitle.text = "Login with OTP"
        binding.buttonVerifyPhone.isEnabled = true

        viewModels =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        viewModel.selectedPhoneNumber.observe(
            requireActivity(),
            Observer<String?> { value ->
                binding.textInputEditTextPhone.setText(value ?: "")
            })
        if (Pref_storage.getDetail(context, "login").equals("login")) {
            binding.referallCode.visibility = View.VISIBLE
        } else {
            binding.referallCode.visibility = View.GONE
        }
        binding.referallCode.setOnClickListener {
            openSuccessDialog("")

        }
        binding.textInputEditTextPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // this method does nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // this method does nothing
            }

            override fun afterTextChanged(s: Editable?) {
                Pref_storage.setDetail(activity, "phone", s!!.substring(3))
                apicall()
            }

        })

        binding.buttonVerifyPhone.setOnClickListener {
            activity?.hideKeyboard()
            if (viewModel.checkIfPhoneIsValid(binding.textInputEditTextPhone.text.toString())) {
                viewModels =
                    ViewModelProvider(
                        this,
                        MyViewModelFactory(MainRepository(RetrofitService.retrofitService!!))
                    ).get(
                        MainViewModel::class.java
                    )
                if (Pref_storage.getDetail(context, "login").equals("login")) {
                    if (flag) {
                        viewModel.sendOtpToPhone(
                            binding.textInputEditTextPhone.text.toString(),
                            requireActivity()
                        )
                    }
                    else {
                         CustomToast.warningToast(requireActivity(), requireActivity().getString(R.string.login_earn_message))


                    }
                } else {
                    viewModel.sendOtpToPhone(
                        binding.textInputEditTextPhone.text.toString(),
                        requireActivity()
                    )
                }


            } else {
                binding.textInputLayoutPhone.error =
                    "Invalid Phone: Please enter phone number with country code"
            }
        }

//        binding.buttonVerifyPhone.setOnClickListener {
//
//            val number = binding.textInputEditTextPhone.text.toString()
//            val password = binding.passwordEdit.text.toString()
//
//            val message = validation(number, password);
//
//            if (message.isNullOrBlank()) {
//                binding!!.LoginAvi.show()
//                binding.login.isEnabled = false
//                viewModels.sendLoginRequest(
//                    binding.textInputEditTextPhone.text.toString(),
//                    binding.passwordEdit.text.toString()
//                ).observe(requireActivity(), Observer {
//                    if (it != null) {
//                        binding!!.LoginAvi.hide()
//                        activity?.hideKeyboard()
//                        if (viewModel.checkIfPhoneIsValid(binding.textInputEditTextPhone.text.toString())) {
//                            viewModel.sendOtpToPhone(binding.textInputEditTextPhone.text.toString(),requireActivity())
//                        } else {
//                            binding.textInputLayoutPhone.error = "Invalid Phone: Please enter phone number with country code"
//                        }
////                        val bundle = Bundle()
////                        bundle.putString("LoginRes", Gson().toJson(it.get(0)))
////                        bundle.putString("phone", binding.textInputEditTextPhone.text.toString())
////                        val intent = Intent(requireActivity(), Verifyphone::class.java)
////                        intent.putExtra("Arguments", bundle)
////                        binding.login.isEnabled = true
////
////                        startActivity(intent)
//
//                    } else {
//                        binding.login.isEnabled = true
//                        binding!!.LoginAvi.hide()
//
//                        Toast.makeText(
//                            requireActivity(),
//                            "Entered Email-ID / Password is invalid",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//
//
//                })
//            } else {
//                binding!!.LoginAvi.hide()
//                Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
//            }
//        }
//
//
//        binding.signup.setOnClickListener {
//            it.let {
//
//                val intent = Intent(requireActivity(), SignupActivity::class.java)
//                startActivity(intent)
//
//            }
//
//
//        }
//
//
//        binding.forgetpassword.setOnClickListener {
//            it.let {
//
//                val intent = Intent(requireActivity(), ForgotPasswordActivity::class.java)
//                startActivity(intent)
//
//            }
//
//
//        }

    }

    fun validation(number: String, password: String): String? {
        if (number.isNullOrEmpty()) {
            binding.textInputEditTextPhone.setError("Phone number should not be empty")
            return "Phone number should not be empty"
        } else if (number.length < 10) {
            binding.textInputEditTextPhone.setError("Phone number not valid must contains 10 Number")
            return "Phone number not valid must contains 10 Number"
        } else {
            return null
        }


    }

    fun openSuccessDialog(message: String?) {
        val alertDialog = AlertDialog.Builder(requireContext())
        val rowList = layoutInflater.inflate(R.layout.fragment_refer_popup, null)
        val button = rowList.findViewById<Button>(R.id.btnPositive)
        val messages = rowList.findViewById<TextInputEditText>(R.id.referall_edittext)

        alertDialog.setView(rowList)
        val dialog = alertDialog.create()
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        button.setOnClickListener {
            apicall(messages.text.toString(), dialog)
            dialog.dismiss()
            //requireActivity().finish()

        }
    }

    fun apicall(value: String, dialog: AlertDialog) {
        lateinit var loginResponse: loginResponseItem
        val signup = ReferallRequest(
            value,
            binding.textInputEditTextPhone.toString(),
            Pref_storage.getDetail(requireActivity(), "userid").toInt()
        )
        Log.e("signup", "" + Gson().toJson(signup));
        viewModels.sendreferllcode(signup).observe(requireActivity()) {
            it.let { resource ->
                when (resource.status) {
                    com.wings.mile.Utils.Status.LOADING -> {
                    }
                    com.wings.mile.Utils.Status.SUCCESS -> {
                        // binding!!.LoginAvi.hide()
                        val value = Gson().toJson(it.data!!.get(0))
                        response = Gson().fromJson(value.toString(), responseItem::class.java)
                        if (response.error_desc.contains("Invalid Referral Code")) {
                            Toast.makeText(
                                requireActivity(),
                                "Invalid Referral Code",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            AwesomeDialog.build(requireActivity())
                                .title("Congratulations", null, R.color.labelrecyclerview)
                                .body(
                                    "Refer code Applied Successfully",
                                    null,
                                    R.color.labelrecyclerview
                                )
                                .icon(R.drawable.user)
                                .onPositive(
                                    "Ok",
                                    R.drawable.login_border_theme

                                ) {
                                    dialog.dismiss()
                                }
                        }
                    }
                    com.wings.mile.Utils.Status.ERROR -> {
                        // binding!!.LoginAvi.hide()
                        Toast.makeText(
                            requireActivity(),
                            "Profile updated Error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    fun apicall() {
        viewModels!!.getdriverdetails(Pref_storage.getDetail(requireActivity(), "phone"))
            .observe(this) {
                it.let { resource ->
                    when (resource.status) {
                        com.wings.mile.Utils.Status.LOADING -> {
                        }
                        com.wings.mile.Utils.Status.SUCCESS -> {

                            Pref_storage.setDetail(
                                requireActivity(),
                                "getdriverdetails",
                                Gson().toJson(it.data!!.get(0))
                            )
                            val request =
                                Pref_storage.getDetail(requireActivity(), "getdriverdetails")
                            Pref_storage.setDetail(
                                requireActivity(),
                                "userid",
                                it.data?.get(0)?.user_id.toString()
                            )
                            savedrivers = Gson().fromJson(request.toString(), getdriver::class.java)
                            if (savedrivers.en_flag.equals("A")) {
                                flag = true
                            } else {
//                            val intent = Intent(this, DashboardActivity::class.java)
//                            startActivity(intent)
                            }

                        }
                        com.wings.mile.Utils.Status.ERROR -> {
                            if (it.data == null) {
                                flag = false
                            }

                            // binding!!.LoginAvi.hide()

                        }
                    }
                }
            }
    }
}
