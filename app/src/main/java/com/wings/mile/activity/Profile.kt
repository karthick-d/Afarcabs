package com.wings.mile.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.example.awesomedialog.*
import com.google.gson.Gson
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ProfileBinding
import com.wings.mile.model.SignUpRequest
import com.wings.mile.model.loginResponseItem
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory


class Profile : BaseActivity() {

    private lateinit var dataBinding: ProfileBinding
    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()
    lateinit var myContext: Context
    val token= Pref_storage.getDetail(this,"fcmtoken")

    var request=SignUpRequest("","","","","",0, user_type_flg = "", en_flg = "", notification_token = token)
    var nameCheck:Boolean=false
    var emailCheck:Boolean=false
    var numCheck:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        dataBinding = ProfileBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        initializeView()
    }


    private fun initializeView() {
        dataBinding.lifecycleOwner = this
        val obj = intent

        //val bundle = intent.extras
            // bundle = intent.getBundleExtra("Arguments")

        val res = obj?.getStringExtra("LoginRes")


        val loginResponse = Gson().fromJson(res.toString(), loginResponseItem::class.java)

        //dataBinding.toolbarLayout.icon.visibility=View.GONE
        //dataBinding.toolbarLayout.toolbarTitle.text="Profile"
        request.user_type_flg=loginResponse.user_type_flg
        request.user_id=loginResponse.user_id
        request.first_name=loginResponse.name
        request.email_id=loginResponse.email_id
        request.phone_num=loginResponse.phone_num

        Log.e("res",""+loginResponse.name)


        dataBinding.nameEdittext.setText(loginResponse.name)
        dataBinding.emailEdittext.setText(loginResponse.email_id)
        dataBinding.mobileEdittext.setText(loginResponse.phone_num)

        dataBinding.save.setOnClickListener {
            dataBinding!!.LoginAvi.show()
            val signup = SignUpRequest(dataBinding.emailEdittext.text.toString(), dataBinding.nameEdittext.text.toString(), "", dataBinding.mobileEdittext.text.toString(), "", loginResponse.user_id, user_type_flg = "A", en_flg = "u",
            notification_token = token)
            Log.e("signup",""+signup.toString());
            viewModel.sendSignupRequestOrProfileUpdate(
                signup
            ).observe(this, Observer {
               if(it!=null){
                   dataBinding!!.LoginAvi.hide()
                   AwesomeDialog.build(this)
                       .title("Congratulations")
                       .body("Profile Updated Successfully")
                       .icon(R.drawable.ic_user_metrics_icn)
                       .onPositive("Ok") {
                           //startActivity(Intent(requireContext(),MainActivity::class.java))

                       }
               }else{
                   dataBinding!!.LoginAvi.hide()
                   Toast.makeText(this,it.toString(),Toast.LENGTH_LONG).show()
               }

            })


        }

        dataBinding.nameEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

//                val name=s.toString()
//                if(name!=loginResponse.name){
//                    request.first_name=name
//                     nameCheck=true
//                    enableButton()
//                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        dataBinding.emailEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

//                var email=s.toString()
//                if(email!=loginResponse.email_id){
//                    request.email_id=email
//                    emailCheck=true
//                    enableButton()
//                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        dataBinding.mobileEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                var mobile=s.toString()
//                if(mobile!=loginResponse.phone_num){
//                    request.phone_num=mobile
//                    numCheck=true
//                    enableButton()
//                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


    }

    private fun setViewModel() {

    }

    fun enableButton(){
        dataBinding.save.isEnabled=(nameCheck || numCheck || emailCheck)

    }


    fun LottieProgressDialog.Hide(){
        this.cancel()
    }

    fun LottieProgressDialog.Show(){
        this.show()
    }


    fun Dialog(): LottieProgressDialog {
        return LottieProgressDialog(
            context = myContext,
            isCancel = true,
            dialogWidth = null,
            dialogHeight = null,
            animationViewWidth = null,
            animationViewHeight = null,
            fileName = LottieProgressDialog.SAMPLE_1,
            title = null,
            titleVisible = null
        )
    }


}