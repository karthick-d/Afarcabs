package com.wings.mile.Payment

import android.R.attr.logo
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import bank_details
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.MapsActivity
import com.wings.mile.Payment.PaymentRazorActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivityAccountPageBinding
import com.wings.mile.databinding.ActivitySplashScreenBinding
import com.wings.mile.databinding.AuthSignInBinding
import com.wings.mile.databinding.DriverLoginBinding
import com.wings.mile.databinding.PhonePermissionBinding
import com.wings.mile.firebase.AuthActivity
import com.wings.mile.model.accounts_driver
import com.wings.mile.model.getdriver
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class PaymentAccountActivity : DashboardActivity() {
    var English: ToggleButton? = null
    var Hindi: ToggleButton? = null
    var rl: RelativeLayout? = null
    private val handler = Handler()
    lateinit var dataBinding: ActivityAccountPageBinding

    //    val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
//    protected val REQUIRED_PERMISSION_CAll =
//        arrayOf(REQUEST_PHONE_CALL)
//    lateinit var viewModel: MainViewModel
    lateinit var bankdetails: bank_details
    private val retrofitService = RetrofitService.getInstance()
    var flag: Boolean = false
    private var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var pdfFilePath: String? = null
    private var postaccountdriver: accounts_driver? = null

    private var myFilePath: File? = null;
    private var stringBase64ImageProfile: String? = ""

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataBinding = ActivityAccountPageBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        dataBinding.toolbarLayout.toolbarTitle.text = "Bank Details"
        setLauncherResult()

//        try {
//            val value = Pref_storage.getDetail(this, "phone")
//            if (value == null) {
//
//            }else{
//                apicall()
//            }
//        }catch (e:Exception){
//            e.printStackTrace()
//        }
//        dataBinding.confirm.setOnClickListener(){
//            val value=Pref_storage.getDetail(this,"phone")
//            if(!flag){
//                if(value==null) {
//                    val intent = Intent(this, DriverRegisterActivity::class.java)
//                    startActivity(intent)
//                }else{
//                    val intent = Intent(this, PaymentRazorActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//        }
        dataBinding.ifscEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // this method does nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // this method does nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 11) {
                    apicall()
                }
            }

        })
        dataBinding.finish.setOnClickListener {

            val result = validation(
                dataBinding.ifscEdittext.text.toString(),
                dataBinding.banknameEdittext.text.toString(),
                dataBinding.branchnameEdittext.text.toString(),
                dataBinding.accountnumberEdittext.text.toString(),
                dataBinding.mobilenumberEdittext.text.toString(),
            )
            if (result.isNullOrEmpty()) {
                postaccountdriver = accounts_driver(
                    271,
                    dataBinding.banknameEdittext.text.toString(),
                    dataBinding.branchnameEdittext.text.toString(),
                    dataBinding.ifscEdittext.text.toString(),
                    "",
                    "",
                    stringBase64ImageProfile,
                    dataBinding.accountnumberEdittext.text.toString(),
                    dataBinding.mobilenumberEdittext.text.toString()
                )
                Pref_storage.setDetail(
                    this,
                    "getdriveraccountdetails",
                    Gson().toJson(postaccountdriver)
                )
                val intent = Intent(this, PaymentRazorActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()

            }
        }
        dataBinding.upload.setOnClickListener {
            pickPhotoImage(dataBinding.imgPassbook)
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "application/pdf"
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            if (someActivityResultLauncher != null) {
//                someActivityResultLauncher!!.launch(intent)
//            }

        }
    }

    fun apicall() {
        viewModel!!.getbankdetails(dataBinding.ifscEdittext.text.toString()).observe(this) {
            it.let { resource ->
                when (resource.status) {
                    com.wings.mile.Utils.Status.LOADING -> {
                        dataBinding.banknameEdittext.setText("")
                        dataBinding.branchnameEdittext.setText("")
                    }
                    com.wings.mile.Utils.Status.SUCCESS -> {
                        if (it.data != null) {

                            val request = it.data!!

                            // bankdetails = Gson().fromJson(request.toString(), bank_details::class.java)
                            dataBinding.banknameEdittext.setText(request.bank)
                            dataBinding.branchnameEdittext.setText(request.branch)
                        }


                    }
                    com.wings.mile.Utils.Status.ERROR -> {
                        if (it.data == null) {
                            flag = false
                            dataBinding.banknameEdittext.setText("")
                            dataBinding.branchnameEdittext.setText("")
                        }

                        // binding!!.LoginAvi.hide()

                    }
                }
            }
        }
    }

    fun validation(
        ifsc: String,
        bankname: String,
        branchname: String,
        accountnumber: String,
        mobilenumber: String,

        ): String? {
        if (ifsc.isNullOrEmpty()) {
            dataBinding.ifscEdittext.error = "Ifsccode should not be empty"

            return "Ifsccode should not be empty"
        } else if (bankname.isNullOrEmpty()) {
            dataBinding.banknameEdittext.error = "bankname should not be empty"

            return "bankname should not be empty\""
        } else if (mobilenumber.isNullOrEmpty()) {
            dataBinding.mobilenumberEdittext.error = "Phone number should not be empty"
            return "Phone number should not be empty"
        } else if (dataBinding.accountnumberEdittext.text.toString().isNullOrEmpty()) {
            dataBinding.accountnumberEdittext.error = "AccountNumber should not be empty"

            return "AccountNumber should not be empty"
        } else if (mobilenumber.length < 10) {
            dataBinding.mobilenumberEdittext.error =
                "Phone number not valid must contains 10 Number"
            return "Phone number not valid must contains 10 Number"
        } else if (branchname.isNullOrEmpty()) {
            dataBinding.branchnameEdittext.error = "Branch name should not be empty"

            return "Address should not be empty"
        }
//        else if (stringBase64ImageProfile!!.isEmpty()) {
//            Toast.makeText(this, "Bank Statement Mandatory", Toast.LENGTH_LONG).show()
//            return "Bank Statement Mandatory"
//        }
        else {
            return null
        }


    }

    @SuppressLint("Range", "Recycle")
    private fun setLauncherResult() {
        someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                // Here, no request code
                val data = result.data
                if (data != null) {

                    var displayName: String? = "";

                    // Get the Uri of the selected file
                    val uri: Uri = result.data!!.data!!

                    val inputStream = this!!.contentResolver.openInputStream(uri!!)
                    val pdfInBytes = ByteArray(inputStream!!.available())

                    val fileSizeInKB: Int = pdfInBytes.size / 1024
                    if (fileSizeInKB > 20 || fileSizeInKB < 12) {
                        showAlertDialog()
                        stringBase64ImageProfile = null
                    } else {
                        val uriString: String = uri.toString()


                        myFilePath = File(uriString)
                        pdfFilePath = myFilePath!!.absolutePath
                        getPDFPath(uri)
                        Log.e("bbbbbbbbb", "" + getPDFPath(uri))
                        if (uriString.startsWith("content://")) {
                            var cursor: Cursor? = null
                            try {
                                cursor = this.contentResolver
                                    .query(uri, null, null, null, null)
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName =
                                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                                    dataBinding!!.pdffileTxt.text = displayName
                                    dataBinding!!.pdffileTxt.visibility = View.VISIBLE
                                    dataBinding!!.pdffileTxt.setTextColor(Color.BLACK)
                                    Pref_storage.setDetail(
                                        this,
                                        "driverbankpdf",
                                        dataBinding!!.pdffileTxt.text.toString()
                                    )
                                }
                            } finally {
                                cursor!!.close()
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFilePath!!.name

                            dataBinding!!.pdffileTxt.text = displayName
                            dataBinding!!.pdffileTxt.visibility = View.VISIBLE
                            dataBinding!!.pdffileTxt.setTextColor(Color.BLACK)
                            Pref_storage.setDetail(
                                this,
                                "driverbankpdf",
                                dataBinding!!.pdffileTxt.text.toString()
                            )


                        }

                    }
                }
            }
        }
    }

    /*Exit Application alert functionality*/
    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage(getString(R.string.img_size_alert)) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                dialog!!.dismiss()

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                dialog.dismiss()
            }
            .show()
    }

    fun getPDFPath(uri: Uri?): String? {
        var absolutePath = ""
        try {
            val inputStream = this!!.contentResolver.openInputStream(uri!!)
            val pdfInBytes = ByteArray(inputStream!!.available())
            Log.e("bbbbbbbbb", "" + pdfInBytes)
            inputStream.read(pdfInBytes)
            stringBase64ImageProfile = Base64.encodeToString(pdfInBytes, Base64.DEFAULT)
            Pref_storage.setDetail(this, "bankpdfbase64", stringBase64ImageProfile)
            var offset = 0
            var numRead = 0
            while (offset < pdfInBytes.size && inputStream.read(
                    pdfInBytes,
                    offset,
                    pdfInBytes.size - offset
                ).also {
                    numRead = it
                } >= 0
            ) {
                offset += numRead
            }
            var mPath = ""
            mPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                this!!.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                    .toString() + "/" + Calendar.getInstance().time + ".pdf"
            } else {
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + Calendar.getInstance().time + ".pdf"
            }
            val pdfFile = File(mPath)
            val op: OutputStream = FileOutputStream(pdfFile)
            op.write(pdfInBytes)
            absolutePath = pdfFile.path
        } catch (ae: java.lang.Exception) {
            ae.printStackTrace()
        }
        return absolutePath
    }


}