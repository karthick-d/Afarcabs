package com.wings.mile.Adddrivers


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.activity.ImageUtils
import com.wings.mile.databinding.ActivityMainBinding
import com.wings.mile.model.*
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*
import java.util.regex.Pattern


class Driverdetails : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var dataBinding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    lateinit var genderList: gender
    lateinit var genderItem: genderItem
    lateinit var savedriver: savedriver
    lateinit var savedrivers: getdriver
    lateinit var loginResponse: loginResponseItem
    var imagelocation:String?=""
    var imagelocationname:String?=""
    var proofname: String? = ""
    val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val DATE_FORMAT_PATTERN_1 = "yyyy-MM-dd'T'HH:mm:ss" //2020-11-27T12:18:41
    val DATE_FORMAT_PATTERN_2 = "dd/MM/yyyy hh:mm a"
    val DATE_FORMAT_PATTERN_3 = "yyyy-MM-dd'T'HH:mm:ss.SSS" //2021-01-04T19:23:00.297
    val DATE_FORMAT_PATTERN_4 = "dd/MM/yy"
    val DATE_FORMAT_PATTERN_14 = "MM/dd/yyyy"

    val REGEX_PATTERN_1 = "\\d{2}-\\d{2}-\\d{4}"
    lateinit var pickedDate: LocalDate
    var eighteenYearsAgo = LocalDate.now() - Period.ofYears(18)
    private val REQUIRED_PERMISSIONS = arrayOf(
        "android.permission.CAMERA",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE"
    )
    private val retrofitService = RetrofitService.getInstance()
    lateinit var myContext: Context

    companion object {
        fun newInstance(): Driverdetails {
            val args = Bundle()
            val fragment = Driverdetails()
            fragment.arguments = args
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ActivityMainBinding.inflate(inflater, container, false)
        initializeView()
        setViewModel()
        return dataBinding.root
    }

    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        updateGenderSpinner()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImageUri = data!!.data

        val photo = data.extras!!["data"] as Bitmap?

        val cameraPhoto = getResizedBitmap(photo, 140)

        dataBinding.photoEdittexts.setImageBitmap(cameraPhoto)

    }

    override fun onAttach(context: Context) {
        myContext = context
        super.onAttach(context)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeView() {
        dataBinding.lifecycleOwner = this
        try {
            (activity as DashboardActivity).icon()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val bundle = this.arguments
        val genderValue = Gson().toJson((activity as DashboardActivity).getgender())
        val vehicleValue = bundle?.get("Vehicle")
        val countryValue = bundle?.get("Country")
        val StateValue = bundle?.get("State")
        genderList = Gson().fromJson(genderValue.toString(), gender::class.java)
        dataBinding.birthLabelEdittext.inputType = InputType.TYPE_NULL
        val request = Pref_storage.getDetail(requireActivity(), "getdriverdetails")
        if (request != "") {
            savedrivers = Gson().fromJson(request.toString(), getdriver::class.java)
            dataBinding.firstnameEdittext.setText(savedrivers.first_Name)
            dataBinding.lastnameEdittext.setText(savedrivers.last_Name)
            dataBinding.genderSpinner.setText(savedrivers.gender)
            dataBinding.emailEdittext.setText(savedrivers.email_id)
            dataBinding.addressEdittext.setText(savedrivers.address)
            dataBinding.mobilenumberEdittext.setText(savedrivers.phone_num)
            dataBinding.birthLabelEdittext.setText(savedrivers.date_of_birth!!)
            dataBinding.pincodeEdittext.setText(savedrivers.pincode)
            proofname = savedrivers.id_proof_name
            if ((activity as DashboardActivity).getBase64()
                    .equals("") || dataBinding.firstnameEdittext.text!!.isEmpty()
            ) {
                if (savedrivers.usr_img_file_location.equals("")) {
                    dataBinding.photoEdittexts.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_photo_camera_24
                        )
                    )
                } else {
                    Glide.with(dataBinding.photoEdittexts)
                        .load(savedrivers.usr_img_file_location + savedrivers.usr_img_file_name)
                        .into(dataBinding.photoEdittexts)
                }
            } else {
                val decodedString: ByteArray =
                    Base64.decode(((activity as DashboardActivity).getBase64()), Base64.DEFAULT)
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                dataBinding.photoEdittexts.setImageBitmap(decodedByte)
            }
        }
        dataBinding.birthLabelEdittext.inputType = InputType.TYPE_NULL
        //  dataBinding.driver.visibility=View.GONE
        dataBinding.next.setOnClickListener {


            val result = validation(
                dataBinding.firstnameEdittext.text.toString(),
                dataBinding.lastnameEdittext.text.toString(),
                dataBinding.mobilenumberEdittext.text.toString(),
                dataBinding.addressEdittext.text.toString(),
                dataBinding.emailEdittext.text.toString(),
                dataBinding.birthLabelEdittext.text.toString(),
                dataBinding.genderSpinner.text.toString()
            )


            if (result.isNullOrEmpty()) {

                val imagedata = (activity as DashboardActivity).getBase64()
                Log.e("image--->",""+imagedata)
                if(imagedata!!.isEmpty()){

                    imagelocation=savedrivers.usr_img_file_location
                    imagelocationname=savedrivers.usr_img_file_name
                }else {
                    imagelocation=""
                    imagelocationname=""
                }
                savedriver = savedriver(
                    first_name = dataBinding.firstnameEdittext.text.toString(),
                    last_name = dataBinding.lastnameEdittext.text.toString(),
                    gender = dataBinding.genderSpinner.text.toString(),
                    phone_number = dataBinding.mobilenumberEdittext.text.toString(),
                    user_address = dataBinding.addressEdittext.text.toString(),
                    email_id = dataBinding.emailEdittext.text.toString(),
                    date_of_birth = getdate(dataBinding.birthLabelEdittext.text.toString()),
                    image_data = imagedata,
                    usr_img_file_location = imagelocation,
                    usr_img_file_name = imagelocationname,
                    user_password = "",
                    user_id = savedrivers.user_id,
                    user_type_flg = "D",
                    aadhar_no = "",
                    country = "",
                    district = "",
                    drv_license_no = "",
                    en_flag = "P",
                    pincode = dataBinding.pincodeEdittext.text.toString(),
                    state = "",
                    vehicle_type_id = savedrivers.vehicle_type_id,
                    district_id = 0,
                    doc_file_location = "",
                    doc_file_name = "",
                    doc_data = "",
                    drv_aadhar_no = "",
                    drv_insurance_no = "",
                    license_plate_no = "",
                    aadhar_data = "",
                    aadhar_file_location = "",
                    aadhar_file_name = "",
                    panno_file_location = "",
                    panno_file_name = "",
                    pan_data = "",
                    drv_pan_no = "",
                    license_data = "",
                    licno_file_location = "",
                    licno_file_name = "",
                    insno_file_location = "",
                    insno_file_name = "",
                    insurance_data = "",
                    plateNo_data = "",
                    plateno_file_location = "",
                    plateno_file_name = "",
                    vehicle_name = "",
                    new_userid = savedrivers.user_id,
                    id_Proof_Name = proofname,
                    comments = "",
                    driving_License_Expiry_Date = savedrivers.driving_License_Expiry_Date,
                    vehicle_Insurance_Expiry_Date = savedrivers.vehicle_Insurance_Expiry_Date
                )
                val bundle = Bundle()
                bundle.putString("Vehicle", vehicleValue.toString())
                bundle.putString("Country", countryValue.toString())
                bundle.putString("State", StateValue.toString())
                bundle.putString("State", StateValue.toString())
                bundle.putString("SaveRequest", Gson().toJson(savedriver))

                findNavController().navigate(R.id.action_SecondFragment_to_ThirdFragment, bundle)

                Pref_storage.setDetail(
                    requireActivity(),
                    "Driverdetails",
                    Gson().toJson(savedriver)
                )

                //(activity as DashboardActivity).replaceFragment(driverdetails)
            }


        }


        dataBinding.birthLabelEdittext.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var conversion: String = ""
                    if (dayOfMonth.toInt() <= 9)
                        conversion = "0" + dayOfMonth
                    else
                        conversion = "" + dayOfMonth
                    pickedDate = LocalDate.of(year, month + 1, day)
                    if (pickedDate >= eighteenYearsAgo) {
                        // Picked a date less than 18 years ago
                        dataBinding.birthLabelEdittext.error =
                            "Minimum age must be 18 years. Please re-check"
                    } else {
                        dataBinding.birthLabelEdittext.error = null

                        dataBinding.birthLabelEdittext.setText(conversion + "/" + (monthOfYear + 1) + "/" + year)
                    }

                },
                year,
                month,
                day
            )

            dpd.show()
            val positiveColor = ContextCompat.getColor(
                requireActivity(),
                com.wings.mile.R.color.teal_200
            )
            val negativeColor = ContextCompat.getColor(
                requireActivity(),
                com.wings.mile.R.color.teal_200
            )
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)

        }


        dataBinding.photoEdittexts.setOnClickListener {
            (activity as DashboardActivity).pickPhotoImage(dataBinding.photoEdittexts)

        }


    }


    fun updateGenderSpinner() {
        var adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, genderList)
        dataBinding.genderSpinner.setAdapter(adapter)

        dataBinding.genderSpinner.onItemClickListener =
            object : AdapterView.OnItemClickListener,
                AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {


                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    genderItem = genderList.get(position)
                }


            }
    }


    protected fun getSelectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 102)
    }

    protected fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    private fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap? {
        var width = image?.width
        var height = image?.height
        val bitmapRatio = height?.toFloat()?.div(width!!.toFloat())
        if (bitmapRatio != null) {
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
        }
        return Bitmap.createScaledBitmap(image!!, width!!, height!!, true)
    }

    private fun getByteArrayImage(data: Intent?): ByteArray {
        val stream = ByteArrayOutputStream()
        val bmp = data!!.extras!!["data"] as Bitmap?
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        bmp.recycle()
        return byteArray
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        TODO("Not yet implemented")
    }

    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validation(
        firstname: String,
        lastname: String,
        number: String,
        addres: String,
        email: String,
        dateofBirth: String,
        gender: String
    ): String? {
        if (firstname.isNullOrEmpty()) {
            dataBinding.firstnameEdittext.error = "firstname should not be empty"

            return "firstname number should not be empty"
        } else if (lastname.isNullOrEmpty()) {
            dataBinding.lastnameEdittext.error = "lastname should not be empty"

            return "lastname should not be empty"
        } else if (!isValidEmailId(email)) {
            dataBinding.emailEdittext.error = "Invlaid Email Address"
            return "Invalid Email Address"
        } else if (number.isNullOrEmpty()) {
            dataBinding.mobilenumberEdittext.error = "Phone number should not be empty"
            return "Phone number should not be empty"
        } else if (number.length < 10) {
            dataBinding.mobilenumberEdittext.error =
                "Phone number not valid must contains 10 Number"
            return "Phone number not valid must contains 10 Number"
        } else if (addres.isNullOrEmpty()) {
            dataBinding.addressEdittext.error = "Address should not be empty"

            return "Address should not be empty"
        } else if (dataBinding.pincodeEdittext.text.toString().isNullOrEmpty()) {
            dataBinding.pincodeEdittext.error = "Pincode should not be empty"

            return "Pincode should not be empty"
        } else if (gender.isNullOrEmpty()) {
            dataBinding.genderSpinner.error = "Gender should not be empty"

            return "Gender should not be empty"
        } else if (dateofBirth.isNullOrEmpty()) {
            dataBinding.birthLabelEdittext.error = "Date of birth should not be empty"

            return "Date of birth should not be empty"
        }
//        else if (pickedDate >= eighteenYearsAgo) {
//            // Picked a date less than 18 years ago
//            dataBinding.birthLabelEdittext.setError("Minimum age must be 18 years. Please re-check")
//
//            return "Minimum age must be 18 years. Please re-check"
//        }
        else {
            return null
        }


    }


    fun dateStringToUTC(value: String): String? {
        var date1: Date? = null
        try {
            date1 = SimpleDateFormat(DATE_FORMAT_PATTERN_4).parse(value)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        //Date c = Calendar.getInstance().getTime();
        val df = SimpleDateFormat(DATE_FORMAT_PATTERN_4, Locale.getDefault())
        val formattedDate = df.format(date1)
        val outputformat = SimpleDateFormat(DATE_FORMAT_PATTERN_14)
        var date: Date? = null
        try {
            date = df.parse(formattedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputformat.format(date)
    }

    private fun getdate(date: String): String? {
        var date1: Date? = null
        try {
            date1 = SimpleDateFormat(DATE_FORMAT_PATTERN_4).parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date1 != null) {
            val df = SimpleDateFormat(
                DATE_FORMAT_PATTERN_14,
                Locale.getDefault()
            )
            return df.format(date1)
        }
        return ""
    }
}