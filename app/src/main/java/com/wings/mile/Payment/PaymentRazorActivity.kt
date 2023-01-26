package com.wings.mile.Payment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.razorpay.*
import com.wings.mile.NavigationActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.model.accounts_driver
import com.wings.mile.model.razorpostdriver
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import org.json.JSONObject

class PaymentRazorActivity: AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener, DialogInterface.OnClickListener {
    private var viewModel: MainViewModel? = null
    val TAG: String = PaymentRazorActivity::class.toString()
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private val retrofitService = RetrofitService.getInstance()
    private var postdriver1: razorpostdriver? = null
    private var postaccountdriver: accounts_driver? = null
    var paymentid: String? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)
        setViewModel()
        /*
        * To ensure faster loading of the Checkout form,
        * call this method as early as possible in your checkout flow
        * */
        Checkout.preload(applicationContext)
        alertDialogBuilder = AlertDialog.Builder(this@PaymentRazorActivity)
        alertDialogBuilder.setTitle("Payment Result")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("Ok", this)
        val button: Button = findViewById(R.id.btn_pay)
        button.setOnClickListener {
            startPayment()
        }
    }

    private fun startPayment() {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        val etApiKey = findViewById<EditText>(R.id.et_api_key)
        val etCustomOptions = findViewById<EditText>(R.id.et_custom_options)
        if (!TextUtils.isEmpty(etApiKey.text.toString())) {
            // co.setKeyID(etApiKey.text.toString())
            // co.setKeyID("rzp_test_170VmpmWxQ3dkW")

        }
        co.setKeyID("rzp_test_4h7lOvu2b85gCF")
        try {
            var options = JSONObject()
            if (!TextUtils.isEmpty(etCustomOptions.text.toString())) {
                options = JSONObject(etCustomOptions.text.toString())
            } else {
                options.put("name", "AfarTechnologies")
                options.put("description", "Demoing Charges")
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
                options.put("currency", "INR")
                options.put("amount", "100")
                options.put("send_sms_hash", true);

                val prefill = JSONObject()
                prefill.put("email", "afarcabs@gmail.com")
                prefill.put("contact", Pref_storage.getDetail(this, "phone"))

                options.put("prefill", prefill)
            }

            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try {
            alertDialogBuilder.setMessage("Payment Successful : Payment ID: $p0\nPayment Data: ${p1?.data}")
            alertDialogBuilder.show()
            postcallaccount(p1!!.data.get("razorpay_payment_id").toString(), "S")

            Log.e("data", "" + p1!!.data.get("razorpay_payment_id").toString())
            paymentid = p1!!.data.get("razorpay_payment_id").toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            alertDialogBuilder.setMessage("Payment Failed : Payment Data: ${p2?.data}")
            alertDialogBuilder.show()
            Log.e("data", "" + p2!!.data)
            postcall(p2!!.data.get("razorpay_payment_id").toString(), "F")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try {
            alertDialogBuilder.setMessage("External wallet was selected : Payment Data: ${p1?.data}")
            alertDialogBuilder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
       // Toast.makeText(this, "" + which, Toast.LENGTH_LONG).show()

        val intent = Intent(this, NavigationActivity::class.java)
        startActivity(intent)
    }

    fun postcall(data: String, s: String) {
        postdriver1 = razorpostdriver(
            Pref_storage.getDetail(this, "userid").toInt(),
            Pref_storage.getDetail(this, "phone"),
            1,
            s,
            data,
            "Driver Registration"
        )
        viewModel!!.postrazorDriverDetails(postdriver1!!).observe(this, Observer {
            if (it != null) {

                //dataBinding!!.LoginAvi.hide()
//                AwesomeDialog.build(requireActivity())
//                    .title("Congratulations")

            } else {
                //dataBinding!!.LoginAvi.hide()
                Toast.makeText(this, "Error in Payment", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun postcallaccount(id: String, s1: String) {
        val request = Pref_storage.getDetail(this, "getdriveraccountdetails")
        postaccountdriver = Gson().fromJson(request.toString(), accounts_driver::class.java)
        // postaccountdriver =
        viewModel!!.postaccountDriverDetails(postaccountdriver!!).observe(this, Observer {
            if (it != null) {
                postcall(id, s1)

            } else {
                //dataBinding!!.LoginAvi.hide()
                Toast.makeText(this, "Email-ID/Phone Number already exits", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
    }
}

