package com.wings.mile

import `in`.shrinathbhosale.preffy.Preffy
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.Utils.Utility
import com.wings.mile.Utils.Utility.Companion.getFolderSizeLabel

import com.wings.mile.activity.*
import com.wings.mile.adapter.AlldriversAdapter
import com.wings.mile.databinding.ActivityDashboardBinding
import com.wings.mile.dialog.CustomSuccessDialogFragment
import com.wings.mile.dialog.OptionDialogFragment
import com.wings.mile.model.*
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File

open class DashboardActivity : BaseActivity(), OptionDialogFragment.OnItemSelect,
    CustomSuccessDialogFragment.OnItemSelectDialog {
    private val TAG = "MainActivity"
    val CAMERA_PERMISSION = "android.permission.CAMERA"
    val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
    val READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES"
    val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
    var Res: String? = ""
    var bundle: Bundle? = null
    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()

    private var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    var photoimageView: ImageView? = null
    var textview: AppCompatTextView? = null
    var bm: Bitmap? = null
    private var stringBase64ImageProfile: String? = ""
    private var stringBase64VehicleImage: String? = ""
    private var stringBase64AadharImage: String? = ""
    private var stringBase64PanImage: String? = ""
    private var stringBase64InsuranceImage: String? = ""
    private var stringBase64LicenseImage: String? = ""
    var value: Int = 0
    var genderlist = gender()
    private lateinit var byteArrayImage: ByteArray

    private lateinit var allAdapter: AlldriversAdapter
    var preffy: Preffy? = null
    protected val REQUIRED_PERMISSION =
        arrayOf(CAMERA_PERMISSION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    protected val REQUIRED_PERMISSION_CAll =
        arrayOf(REQUEST_PHONE_CALL)
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLayout.toolTitle)
        binding.toolbarLayout.toolTitle.visibility=View.GONE
        try {
            val bundle = intent.extras
            // bundle = intent.getBundleExtra("Arguments")
            Pref_storage.setDetail(this,"Pantext","")
            Pref_storage.setDetail(this,"Vehicletext","")
            Pref_storage.setDetail(this,"Insurancetext","")
            Pref_storage.setDetail(this,"adhartext","")
            Pref_storage.setDetail(this,"Licensetext","")
            Pref_storage.setDetail(this,"Licensenumber","")
            Pref_storage.setDetail(this,"adharnumber","")
            Pref_storage.setDetail(this,"Insurancenumber","")
            Pref_storage.setDetail(this,"Vehiclenumber","")
            Pref_storage.setDetail(this,"Pannumber","")
            Pref_storage.setDetail(this,"Vehiclename","")
            Pref_storage.setDetail(this,"Statename","")
            Pref_storage.setDetail(this,"Districtname","")
            Pref_storage.setDetail(this,"driverpdf","")
            Pref_storage.setDetail(this,"idproof","")
            Pref_storage.setDetail(this,"pdfbase64","")
            Pref_storage.setDetail(this,"getdriverdetails","")
            Pref_storage.setDetail(this,"Vehicleid","")
            Pref_storage.setDetail(this,"Districtid","")
            Pref_storage.setDetail(this,"Insuranceexpirydate","")
            Pref_storage.setDetail(this,"Licenseexpirydate","")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.toolbarLayout.logouticon.setOnClickListener {
            showExitAlertDialog()
        }
        binding.toolbarLayout.icon.setOnClickListener {
            it.let {

                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)

            }


        }
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        viewModel.fetchCountryDetails().observe(this, Observer {

            if (!it.isNullOrEmpty()) {
                //preffy?.putString("Country",Gson().toJson(it))
            }

        })

        viewModel.fetchGenderDetails().observe(this, Observer {

        })
        viewModel.fetchVehicleDetails().observe(this, Observer {


        })
        viewModel.fetchStateDetails().observe(this, Observer {


        })

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun pickPhotoImages(imageView: ImageView, imagename: AppCompatTextView, i: Int) {
        photoimageView = imageView
        textview = imagename
        value = i
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                OptionDialogFragment(this, false, 2).show(
                    supportFragmentManager,
                    "OptionDialogFragment"
                )
            }
        } else {
            //system OS is < Marshmallow
            OptionDialogFragment(this, false, 2).show(
                supportFragmentManager,
                "OptionDialogFragment"
            )
        }

    }

    fun pickPhotoImage(imageView: ImageView) {
        photoimageView = imageView
        value = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                OptionDialogFragment(this, false, 2).show(
                    supportFragmentManager,
                    "OptionDialogFragment"
                )
            }
        } else {
            //system OS is < Marshmallow
            OptionDialogFragment(this, false, 2).show(
                supportFragmentManager,
                "OptionDialogFragment"
            )
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val REQUEST_CODE_CAMERA = 1002
        private val PERMISSION_CODE = 1001
    }


    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    OptionDialogFragment(this, false, 2).show(
                        supportFragmentManager,
                        "OptionDialogFragment"
                    )
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data?.data)
            val tempUri = data?.data

           // val afterBitmap = ImageUtils.getInstant().getCompressedBitmap(tempUri!!.encodedPath)
            //Log.e("size---->",""+afterBitmap);
            if (getFolderSizeLabel(this, tempUri) > 20) {
                CustomSuccessDialogFragment(this, R.string.img_size_alert, R.drawable.ic_warning).show(supportFragmentManager, "CustomSuccessDialogFragment")
                stringBase64ImageProfile = null
            } else {
                when (value) {

                    0 -> {

                        photoimageView!!.setImageURI(data?.data)
                        stringBase64ImageProfile = encodeTobase64(bm!!)

                    }
                    1 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this, "adhartext", textview!!.text.toString())
                        textview!!.visibility = View.VISIBLE
                        stringBase64AadharImage = encodeTobase64(bm!!)

                    }
                    2 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this, "Licensetext", textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64LicenseImage = encodeTobase64(bm!!)

                    }
                    3 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this, "Insurancetext", textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64InsuranceImage = encodeTobase64(bm!!)

                    }
                    4 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this, "Pantext", textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64PanImage = encodeTobase64(bm!!)

                    }
                    5 -> {
                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this, "Vehicletext", textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64VehicleImage = encodeTobase64(bm!!)

                    }
                }

            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            // else handle other activity results
            val mFileTemp: File
            try {

                val photo = data!!.extras!!["data"] as Bitmap?
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                var tempUri: Uri? = null
                tempUri = Utility.getImageUri(this, photo)
                if (getFolderSizeLabel(this, tempUri) > 20) {
                    CustomSuccessDialogFragment(this, R.string.img_size_alert, R.drawable.ic_warning).show(supportFragmentManager, "CustomSuccessDialogFragment")
                    stringBase64ImageProfile = null
                } else {
                    if (value != 0) {
                        tempUri = Utility.getImageUri(this, photo)
                    }
                    byteArrayImage = getByteArrayImage(data)

                    when (value) {

                        0 -> {

                            photoimageView!!.setImageBitmap(photo)
                            stringBase64ImageProfile =
                                Base64.encodeToString(byteArrayImage, Base64.DEFAULT)

                        }
                        1 -> {

                            if (tempUri != null) {
                                textview!!.text = getRealPathFromURI(tempUri)
                                Pref_storage.setDetail(this,"adhartext",textview!!.text.toString())

                            } else {
                                Log.e("check", "check data---> ")
                            }
                            stringBase64AadharImage = encodeTobase64(photo!!)

                        }
                        2 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)

                            stringBase64LicenseImage = encodeTobase64(photo!!)
                            Pref_storage.setDetail(this,"Licensetext",textview!!.text.toString())


                        }
                        3 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)
                            stringBase64InsuranceImage = encodeTobase64(photo!!)
                            Pref_storage.setDetail(this,"Insurancetext",textview!!.text.toString())

                        }
                        4 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)
                            stringBase64PanImage = encodeTobase64(photo!!)
                            Pref_storage.setDetail(this,"Pantext",textview!!.text.toString())

                        }
                        5 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)
                            stringBase64VehicleImage = encodeTobase64(photo!!)
                            Pref_storage.setDetail(this,"Vehicletext",textview!!.text.toString())


                        }
                    }
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }


    private fun getByteArrayImage(data: Intent?): ByteArray {
        val stream = ByteArrayOutputStream()
        val bmp = data!!.extras!!["data"] as Bitmap?
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        bmp.recycle()
        return byteArray
    }

    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        val imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)
        Log.e("LOOK", imageEncoded)
        Log.e("LOOK", b.size.toString())
        return imageEncoded
    }

    fun getBase64(): String? {
        return stringBase64ImageProfile
    }


    override fun onGalleryButtonClicked() {
        pickImageFromGallery()
    }

    override fun onCameraButtonClicked() {
        takePictureCamera()

    }

    override fun onOkButtonClicked() {
    }


    private fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (baseContext.contentResolver != null) {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

//    protected fun allPermissionsGrant(): Boolean {
//        for (permission in REQUIRED_PERMISSION) {
//            if (ContextCompat.checkSelfPermission(
//                    baseContext,
//                    permission!!
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            }
//        }
//        return true
//    }

//    protected fun allPermissionsGrantphone(): Boolean {
//        for (permission in REQUIRED_PERMISSION_CAll) {
//            if (ContextCompat.checkSelfPermission(
//                    baseContext,
//                    permission!!
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return true
//            }
//        }
//        return false
//    }

    private fun takePictureCamera() {
        startCamera()
    }

    override fun onStart() {
        super.onStart()
        setLauncherResult()
    }

    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (someActivityResultLauncher != null) {
            someActivityResultLauncher!!.launch(cameraIntent)
        }
        //startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
    }

    private fun setLauncherResult() {
        someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                // Here, no request code
                val data = result.data
                if (data != null) {

                    val photo = data.extras!!["data"] as Bitmap?
                    var tempUri: Uri? = null
                    if (value != 0) {
                        tempUri = Utility.getImageUri(this, photo)
                    }
                    byteArrayImage = getByteArrayImage(data)

                    when (value) {

                        0 -> {

                            photoimageView!!.setImageBitmap(photo)
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            stringBase64ImageProfile =
                                Base64.encodeToString(byteArrayImage, Base64.DEFAULT)

                        }
                        1 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64AadharImage = encodeTobase64(photo)
                            Pref_storage.setDetail(this,"adhartext",textview!!.text.toString())

                        }
                        2 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64LicenseImage = encodeTobase64(photo)
                            Pref_storage.setDetail(this,"Licensetext",textview!!.text.toString())

                        }
                        3 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64InsuranceImage = encodeTobase64(photo!!)
                            Pref_storage.setDetail(this,"Insurancetext",textview!!.text.toString())


                        }
                        4 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64PanImage = encodeTobase64(photo!!)
                            Pref_storage.setDetail(this,"Pantext",textview!!.text.toString())


                        }
                        5 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64VehicleImage = encodeTobase64(photo!!)
                            Pref_storage.setDetail(this,"Vehicletext",textview!!.text.toString())


                        }
                    }
                }
            }
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun call(phone: String) {

        if (!allPermissionsGrantphone()) {
            val num = "tel:" + phone
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(num))
            startActivity(intent)
        } else {

            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION_CAll, 120)
        }
    }

    fun getpanbase64(): String? {

        return stringBase64PanImage
    }

    fun getinsurancebase64(): String? {

        return stringBase64InsuranceImage
    }

    fun getaadharbase64(): String? {

        return stringBase64AadharImage
    }

    fun getlicense(): String? {

        return stringBase64LicenseImage
    }

    fun getvehicles(): String? {

        return stringBase64VehicleImage
    }

    fun getgender(): gender {

        return viewModel.genderlist
    }

    fun icon() {
        binding.toolbarLayout.icon.visibility = View.GONE
        binding.toolbarLayout.logouticon.visibility = View.GONE
    }

    fun icons() {
        binding.toolbarLayout.icon.visibility = View.VISIBLE
        binding.toolbarLayout.logouticon.visibility = View.VISIBLE
        binding.toolbarLayout.logouticon.setImageResource(R.drawable.logout)
        binding.toolbarLayout.logouticon.setOnClickListener {
            showExitAlertDialog()
        }
    }

    fun profileicon(): ImageView {
        return binding.toolbarLayout.icon
    }

    fun iconfeedback() {
        binding.toolbarLayout.icon.visibility = View.GONE
        binding.toolbarLayout.logouticon.visibility = View.VISIBLE
        binding.toolbarLayout.logouticon.setImageResource(R.drawable.chat)
        binding.toolbarLayout.logouticon.setOnClickListener {

            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }
    }

    fun datapass(): Bundle {
        val bundle = Bundle()
        bundle.putString("Gender", Gson().toJson(viewModel.genderlist))
        bundle.putString("State", Gson().toJson(viewModel.statelist))
        bundle.putString("Vehicle", Gson().toJson(viewModel.vehiclelist))
        bundle.putString("Country", Gson().toJson(viewModel.countrylist))
        return bundle
    }


    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    /*Exit Application alert functionality*/
    private fun showExitAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Do You Want to Logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
                startActivity(intent)

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                dialog.dismiss()
            }
            .show()
    }

    fun getvehicle(): vehicle {

        return viewModel.vehiclelist
    }

    fun getstate(): getstate {

        return viewModel.statelist
    }


    fun getcountry(): getCountry {

        return viewModel.countrylist
    }
    fun logout(){
        val intent = Intent(this, ProfileActivity::class.java)

        Log.e("res",""+Res.toString())

        startActivity(intent)
    }

    protected fun allPermissionsGrantphone(): Boolean {
        for (permission in REQUIRED_PERMISSION_CAll) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

}