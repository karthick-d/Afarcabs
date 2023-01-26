package com.wings.mile.Adddrivers

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.Utils.Utility
import com.wings.mile.databinding.ActivityMain1Binding
import com.wings.mile.model.*
import com.wings.mile.preview.PopupImage
import com.wings.mile.preview.PopupImageView
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Driverdetails1 : Fragment() {

    private lateinit var dataBinding: ActivityMain1Binding
    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()
    lateinit var myContext: Context

    lateinit var vehicle1: vehicle
    lateinit var country: getCountry
    lateinit var state: getstate
    lateinit var district: getDistrictDetails
    val DATE_FORMAT_PATTERN_4 = "dd/MM/yy"
    val DATE_FORMAT_PATTERN_14 = "MM/dd/yyyy"

    lateinit var savedriver1: savedriver
    lateinit var getdrivers: getdriver

    lateinit var vehicleItem: vehicleItem
    lateinit var stateItem: getstateItem
    lateinit var districtItem: getDistrictDetailsItem
    var regex = ("^(([A-Z]{2}[0-9]{2})"
            + "( )|([A-Z]{2}-[0-9]"
            + "{2}))((19|20)[0-9]"
            + "[0-9])[0-9]{7}$")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ActivityMain1Binding.inflate(inflater, container, false)
        initializeView()
        setViewModel()
        return dataBinding.root
    }

    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        updateVehicleSpinner()
        updateStateSpinner()
    }

    private fun fetchDistrictDetails(statevalue: String) {
        viewModel.fetchDistrictDetails(statevalue, "2").observe(requireActivity(), Observer {
            if (it != null) {
                district = it
                updateDistrictSpinner(district)
            } else {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onAttach(context: Context) {
        myContext = context
        super.onAttach(context)

    }

    private fun initializeView() {
        dataBinding.lifecycleOwner = this

        val bundle = this.arguments
        val vehicleValue = Gson().toJson((activity as DashboardActivity).getvehicle())
        val countryValue = Gson().toJson((activity as DashboardActivity).getcountry())
        val StateValue = Gson().toJson((activity as DashboardActivity).getstate())
        dataBinding.insuranceExpirytext.inputType = InputType.TYPE_NULL
        dataBinding.licenseExpirydate.inputType = InputType.TYPE_NULL
        val request = bundle?.get("SaveRequest")
        if (request != null) {
            savedriver1 = Gson().fromJson(
                Pref_storage.getDetail(requireActivity(), "Driverdetails"),
                savedriver::class.java
            )

        }
        try {
            (activity as DashboardActivity).icon()
            if (Pref_storage.getDetail(requireActivity(), "getdriverdetails").toString()
                    .equals(""))
            {

                if (Pref_storage.getDetail(requireActivity(), "Vehicletext").toString()
                        .equals("")
                ) {
                    dataBinding.imagenameplate.visibility = View.VISIBLE

                    dataBinding.imagenameplate.text = savedriver1.plateno_file_name

                } else {
                    dataBinding.imagenameplate.visibility = View.VISIBLE
                    dataBinding.imagenameplate.text =
                        Pref_storage.getDetail(requireActivity(), "Vehicletext")
                }
                if (Pref_storage.getDetail(requireActivity(), "Pantext").toString().equals("")) {
                    dataBinding.imagenamepancard.visibility = View.VISIBLE

                    dataBinding.imagenamepancard.text = savedriver1.panno_file_name

                } else {
                    dataBinding.imagenamepancard.visibility = View.VISIBLE
                    dataBinding.imagenamepancard.text =
                        Pref_storage.getDetail(requireActivity(), "Pantext")
                }

                if (Pref_storage.getDetail(requireActivity(), "adhartext").toString().equals("")) {
                    dataBinding.imagenameaadhar.visibility = View.VISIBLE

                    dataBinding.imagenameaadhar.text = savedriver1.aadhar_file_name

                } else {
                    dataBinding.imagenameaadhar.visibility = View.VISIBLE
                    dataBinding.imagenameaadhar.text =
                        Pref_storage.getDetail(requireActivity(), "adhartext")

                }
                if (Pref_storage.getDetail(requireActivity(), "Insurancetext").toString()
                        .equals("")
                ) {
                    dataBinding.imagenameinsurance.visibility = View.VISIBLE

                    dataBinding.imagenameinsurance.text = savedriver1.insno_file_name

                } else {
                    dataBinding.imagenameinsurance.visibility = View.VISIBLE

                    dataBinding.imagenameinsurance.text =
                        Pref_storage.getDetail(requireActivity(), "Insurancetext")
                }
                if (Pref_storage.getDetail(requireActivity(), "Licensetext").toString()
                        .equals("")
                ) {
                    dataBinding.imagenamelicense.visibility = View.VISIBLE

                    dataBinding.imagenamelicense.text = savedriver1.licno_file_name


                } else {
                    dataBinding.imagenamelicense.visibility = View.VISIBLE
                    dataBinding.imagenamelicense.text =
                        Pref_storage.getDetail(requireActivity(), "Licensetext")
                }
                if (Pref_storage.getDetail(requireActivity(), "Statename").toString().equals("")) {
                    dataBinding.stateSpinner.setText(savedriver1.state)


                } else {
                    dataBinding.stateSpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Statename"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Licenseexpirydate").toString()== "") {
                    dataBinding.licenseExpirydate.setText(
                       savedriver1.driving_License_Expiry_Date)
                }else{
                    dataBinding.licenseExpirydate.setText(
                        Pref_storage.getDetail(requireActivity(), "Licenseexpirydate"))
                }
                if (Pref_storage.getDetail(requireActivity(), "Insuranceexpirydate").toString().equals("")) {
                    dataBinding.insuranceExpirytext.setText(
                        savedriver1.vehicle_Insurance_Expiry_Date)
                }else{
                    dataBinding.insuranceExpirytext.setText(
                        Pref_storage.getDetail(requireActivity(), "Insuranceexpirydate"))
                }
                if (Pref_storage.getDetail(requireActivity(), "Vehiclename").toString()
                        .equals("")
                ) {
                    dataBinding.vehileSpinner.setText(savedriver1.vehicle_name)


                } else {
                    dataBinding.vehileSpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Vehiclename"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Districtname").toString()
                        .equals("")
                ) {
                    dataBinding.citySpinner.setText(savedriver1.district)


                } else {
                    dataBinding.citySpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Districtname"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Vehiclenumber").toString()
                        .equals("")
                ) {
                    dataBinding.platenumber.setText(savedriver1.license_plate_no)


                } else {
                    dataBinding.platenumber.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Vehiclenumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "adharnumber").toString()
                        .equals("")
                ) {
                    dataBinding.adharEdittext.setText(savedriver1.aadhar_no)


                } else {
                    dataBinding.adharEdittext.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "adharnumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Pannumber").toString().equals("")) {
                    dataBinding.panEdittext.setText(savedriver1.drv_pan_no)


                } else {
                    dataBinding.panEdittext.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Pannumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Licensenumber").toString()
                        .equals("")
                ) {
                    dataBinding.driverlicenseEdittext.setText(savedriver1.drv_license_no)


                } else {
                    dataBinding.driverlicenseEdittext.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Licensenumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Insurancenumber").toString()
                        .equals("")
                ) {
                    dataBinding.insurancenumber.setText(savedriver1.drv_insurance_no)


                } else {
                    dataBinding.insurancenumber.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Insurancenumber"
                        )
                    )

                }
                savedriver1.vehicle_type_id =
                    Integer.valueOf(Pref_storage.getDetail(requireActivity(), "Vehicleid"))
                savedriver1.district_id =
                    Integer.valueOf(Pref_storage.getDetail(requireActivity(), "Districtid"))


            } else {
                getdrivers = Gson().fromJson(
                    Pref_storage.getDetail(requireActivity(), "getdriverdetails"),
                    getdriver::class.java
                )
                if (Pref_storage.getDetail(requireActivity(), "Licenseexpirydate").toString()== "") {
                    dataBinding.licenseExpirydate.setText(
                        savedriver1.driving_License_Expiry_Date)
                }else{
                    dataBinding.licenseExpirydate.setText(
                        Pref_storage.getDetail(requireActivity(), "Licenseexpirydate"))
                }
                if (Pref_storage.getDetail(requireActivity(), "Insuranceexpirydate").toString().equals("")) {
                    dataBinding.insuranceExpirytext.setText(
                        savedriver1.vehicle_Insurance_Expiry_Date)
                }else{
                    dataBinding.insuranceExpirytext.setText(
                        Pref_storage.getDetail(requireActivity(), "Insuranceexpirydate"))
                }


                if (Pref_storage.getDetail(requireActivity(), "Vehicletext").toString()
                        .equals("")
                ) {
                    dataBinding.imagenameplate.visibility = View.VISIBLE
                    dataBinding.imagenameplate.text = getdrivers.plateno_file_name

                } else {
                    dataBinding.imagenameplate.visibility = View.VISIBLE
                    dataBinding.imagenameplate.text =
                        Pref_storage.getDetail(requireActivity(), "Vehicletext")
                }
                if (Pref_storage.getDetail(requireActivity(), "Pantext").toString().equals("")) {
                    dataBinding.imagenamepancard.visibility = View.VISIBLE

                    dataBinding.imagenamepancard.text = getdrivers.panno_file_name

                } else {
                    dataBinding.imagenamepancard.visibility = View.VISIBLE
                    dataBinding.imagenamepancard.text =
                        Pref_storage.getDetail(requireActivity(), "Pantext")
                }

                if (Pref_storage.getDetail(requireActivity(), "adhartext").toString().equals("")) {
                    dataBinding.imagenameaadhar.visibility = View.VISIBLE

                    dataBinding.imagenameaadhar.text = getdrivers.aadhar_file_name

                } else {
                    dataBinding.imagenameaadhar.visibility = View.VISIBLE
                    dataBinding.imagenameaadhar.text =
                        Pref_storage.getDetail(requireActivity(), "adhartext")

                }
                if (Pref_storage.getDetail(requireActivity(), "Insurancetext").toString()
                        .equals("")
                ) {
                    dataBinding.imagenameinsurance.visibility = View.VISIBLE

                    dataBinding.imagenameinsurance.text = getdrivers.insno_file_name

                } else {
                    dataBinding.imagenameinsurance.visibility = View.VISIBLE

                    dataBinding.imagenameinsurance.text =
                        Pref_storage.getDetail(requireActivity(), "Insurancetext")
                }
                if (Pref_storage.getDetail(requireActivity(), "Licensetext").toString()
                        .equals("")
                ) {
                    dataBinding.imagenamelicense.visibility = View.VISIBLE

                    dataBinding.imagenamelicense.text = getdrivers.licno_file_name


                } else {
                    dataBinding.imagenamelicense.visibility = View.VISIBLE
                    dataBinding.imagenamelicense.text =
                        Pref_storage.getDetail(requireActivity(), "Licensetext")
                }
                if (Pref_storage.getDetail(requireActivity(), "Statename").toString().equals("")) {
                    dataBinding.stateSpinner.setText(getdrivers.usr_state)


                } else {
                    dataBinding.stateSpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Statename"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Vehiclename").toString()
                        .equals("")
                ) {
                    dataBinding.vehileSpinner.setText(getdrivers.vehicle_type_name)


                } else {
                    dataBinding.vehileSpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Vehiclename"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Districtname").toString()
                        .equals("")
                ) {
                    dataBinding.citySpinner.setText(getdrivers.usr_district)


                } else {
                    dataBinding.citySpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Districtname"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Vehiclenumber").toString()
                        .equals("")
                ) {
                    dataBinding.platenumber.setText(getdrivers.license_plate_no)


                } else {
                    dataBinding.platenumber.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Vehiclenumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "adharnumber").toString()
                        .equals("")
                ) {
                    dataBinding.adharEdittext.setText(getdrivers.aadhar_no)


                } else {
                    dataBinding.adharEdittext.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "adharnumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Pannumber").toString().equals("")) {
                    dataBinding.panEdittext.setText(getdrivers.drv_pan_no)


                } else {
                    dataBinding.panEdittext.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Pannumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Licensenumber").toString()
                        .equals("")
                ) {
                    dataBinding.driverlicenseEdittext.setText(getdrivers.license_no)


                } else {
                    dataBinding.driverlicenseEdittext.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Licensenumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Insurancenumber").toString()
                        .equals("")
                ) {
                    dataBinding.insurancenumber.setText(getdrivers.insurance_no)


                } else {
                    dataBinding.insurancenumber.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Insurancenumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Insurancenumber").toString()
                        .equals("")
                ) {
                    dataBinding.insurancenumber.setText(getdrivers.insurance_no)


                } else {
                    dataBinding.insurancenumber.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "Insurancenumber"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "Districtid").toString()
                        .equals("")
                ) {
                    savedriver1.district_id = getdrivers.district_id

                } else {
                    savedriver1.district_id = Pref_storage.getDetail(
                        requireActivity(),
                        "Districtid"
                    ).toInt()


                }
                savedriver1.vehicle_type_id = Pref_storage.getDetail(
                    requireActivity(),
                    "Vehicleid"
                ).toInt()
                if (Pref_storage.getDetail(requireActivity(), "Vehicleid").toString()
                        .equals("")) {
                    savedriver1.vehicle_type_id = getdrivers.vehicle_type_id

                } else {
                    savedriver1.vehicle_type_id = Pref_storage.getDetail(
                        requireActivity(),
                        "Vehicleid"
                    ).toInt()


                }


            }
//            if(Pref_storage.getDetail(requireActivity(),"Driverdetails")!=null) {
//                dataBinding.stateSpinner.setText(Pref_storage.getDetail(requireActivity(),"Statename"))
//                dataBinding.citySpinner.setText(Pref_storage.getDetail(requireActivity(),"Districtname"))
//                dataBinding.platenumber.setText(Pref_storage.getDetail(requireActivity(),"Vehiclenumber"))
//                dataBinding.adharEdittext.setText(Pref_storage.getDetail(requireActivity(),"adharnumber"))
//                dataBinding.panEdittext.setText(Pref_storage.getDetail(requireActivity(),"Pannumber"))
//                dataBinding.driverlicenseEdittext.setText(Pref_storage.getDetail(requireActivity(),"Licensenumber"))
//                dataBinding.insurancenumber.setText(Pref_storage.getDetail(requireActivity(),"Insurancenumber"))
//                dataBinding.vehileSpinner.setText(Pref_storage.getDetail(requireActivity(),"Vehiclename"))
//            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        dataBinding.platenumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // this method does nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // this method does nothing
            }

            override fun afterTextChanged(s: Editable?) {
                Pref_storage.setDetail(requireActivity(), "Vehiclenumber", s.toString())
            }

        })
        dataBinding.adharEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // this method does nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // this method does nothing
            }

            override fun afterTextChanged(s: Editable?) {
                Pref_storage.setDetail(requireActivity(), "adharnumber", s.toString())
            }

        })
        dataBinding.panEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // this method does nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // this method does nothing
            }

            override fun afterTextChanged(s: Editable?) {
                Pref_storage.setDetail(requireActivity(), "Pannumber", s.toString())
            }

        })
        dataBinding.driverlicenseEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // this method does nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // this method does nothing
            }

            override fun afterTextChanged(s: Editable?) {
                Pref_storage.setDetail(requireActivity(), "Licensenumber", s.toString())
            }

        })
        dataBinding.insurancenumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // this method does nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // this method does nothing
            }

            override fun afterTextChanged(s: Editable?) {
                Pref_storage.setDetail(requireActivity(), "Insurancenumber", s.toString())
            }

        })
        dataBinding.licenseExpirydate.setOnClickListener {
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
                    dataBinding.licenseExpirydate.setText(conversion + "/" + (monthOfYear + 1) + "/" + year)

                    Pref_storage.setDetail(requireActivity(), "Licenseexpirydate",  dataBinding.licenseExpirydate.text.toString())

                },
                year,
                month,
                day
            )
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

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
        dataBinding.insuranceExpirytext.setOnClickListener {
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
                    dataBinding.insuranceExpirytext.setText(conversion + "/" + (monthOfYear + 1) + "/" + year)

                    Pref_storage.setDetail(requireActivity(), "Insuranceexpirydate",  dataBinding.insuranceExpirytext.text.toString())

                },
                year,
                month,
                day
            )
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
        vehicle1 = Gson().fromJson(vehicleValue.toString(), vehicle::class.java)
        country = Gson().fromJson(countryValue.toString(), getCountry::class.java)
        state = Gson().fromJson(StateValue.toString(), getstate::class.java)

        dataBinding.next.setOnClickListener {
            val vechicledata = (activity as DashboardActivity).getvehicles()
            val licensedata = (activity as DashboardActivity).getlicense()
            val insurancedata = (activity as DashboardActivity).getinsurancebase64()
            val aadhardata = (activity as DashboardActivity).getaadharbase64()
            val pancarddata = (activity as DashboardActivity).getpanbase64()
            val result = validation(
                dataBinding.platenumber.text.toString(),
                dataBinding.insurancenumber.text.toString(),
                dataBinding.driverlicenseEdittext.text.toString(),
                dataBinding.adharEdittext.text.toString(),
                dataBinding.panEdittext.text.toString(),
                dataBinding.vehileSpinner.text.toString(),
                dataBinding.stateSpinner.text.toString(),
                dataBinding.citySpinner.text.toString(),
            )

            if (result == null) {

                savedriver1.vehicle_name = dataBinding.vehileSpinner.text.toString()
                savedriver1.state = dataBinding.stateSpinner.text.toString()
                savedriver1.district = dataBinding.citySpinner.text.toString()

                savedriver1.aadhar_no = dataBinding.adharEdittext.text.toString()
                savedriver1.en_flag = "P"
                savedriver1.license_plate_no = dataBinding.platenumber.text.toString()
                savedriver1.drv_insurance_no = dataBinding.insurancenumber.text.toString()
                savedriver1.drv_aadhar_no = dataBinding.adharEdittext.text.toString()
                savedriver1.drv_pan_no = dataBinding.panEdittext.text.toString()
                savedriver1.drv_license_no = dataBinding.driverlicenseEdittext.text.toString()
                savedriver1.vehicle_Insurance_Expiry_Date =
                    getdate(dataBinding.insuranceExpirytext.text.toString())
                savedriver1.driving_License_Expiry_Date =
                    getdate(dataBinding.licenseExpirydate.text.toString())
                if (Pref_storage.getDetail(requireActivity(), "getdriverdetails").toString()
                        .equals("")
                ) {
                    savedriver1.plateNo_data = vechicledata
                    savedriver1.plateno_file_name = ""
                    savedriver1.plateno_file_location = ""
                } else {
                    if (getdrivers.plateno_file_location!!.isNotEmpty() && vechicledata!!.isEmpty()) {
                        savedriver1.plateno_file_location = getdrivers.plateno_file_location
                        savedriver1.plateno_file_name = getdrivers.plateno_file_name
                    } else {
                        savedriver1.plateNo_data = vechicledata
                        savedriver1.plateno_file_name = ""
                        savedriver1.plateno_file_location = ""
                    }
                }

                if (Pref_storage.getDetail(requireActivity(), "getdriverdetails").toString()
                        .equals("")
                ) {
                    savedriver1.pan_data = pancarddata
                    savedriver1.panno_file_location = ""
                    savedriver1.panno_file_name = ""
                } else {
                    if (getdrivers.panno_file_location!!.isNotEmpty() && pancarddata!!.isEmpty()) {
                        savedriver1.panno_file_location = getdrivers.panno_file_location
                        savedriver1.panno_file_name = getdrivers.panno_file_name
                    } else {
                        savedriver1.pan_data = pancarddata
                        savedriver1.panno_file_location = ""
                        savedriver1.panno_file_name = ""
                    }
                }
                if (Pref_storage.getDetail(requireActivity(), "getdriverdetails").toString()
                        .equals("")
                ) {
                    savedriver1.aadhar_data = aadhardata
                    savedriver1.aadhar_file_location = ""
                    savedriver1.aadhar_file_name = ""
                } else {
                    if (getdrivers.aadhar_file_location!!.isNotEmpty() && aadhardata!!.isEmpty()) {
                        savedriver1.aadhar_file_location = getdrivers.aadhar_file_location
                        savedriver1.aadhar_file_name = getdrivers.aadhar_file_name
                    } else {
                        savedriver1.aadhar_data = aadhardata
                        savedriver1.aadhar_file_location = ""
                        savedriver1.aadhar_file_name = ""
                    }
                }

                if (Pref_storage.getDetail(requireActivity(), "getdriverdetails").toString()
                        .equals("")
                ) {
                    savedriver1.license_data = licensedata
                    savedriver1.licno_file_location = ""
                    savedriver1.licno_file_name = ""
                } else {
                    if (getdrivers.licno_file_location!!.isNotEmpty() && licensedata!!.isEmpty()) {
                        savedriver1.licno_file_location = getdrivers.licno_file_location
                        savedriver1.licno_file_name = getdrivers.licno_file_name
                    } else {
                        savedriver1.license_data = licensedata
                        savedriver1.licno_file_location = ""
                        savedriver1.licno_file_name = ""
                    }
                }


                if (Pref_storage.getDetail(requireActivity(), "getdriverdetails").toString()
                        .equals("")
                ) {
                    savedriver1.insurance_data = insurancedata
                    savedriver1.licno_file_location = ""
                    savedriver1.licno_file_name = ""
                } else {
                    if (getdrivers.insno_file_location!!.isNotEmpty() && insurancedata!!.isEmpty()) {
                        savedriver1.insno_file_location = getdrivers.insno_file_location
                        savedriver1.insno_file_name = getdrivers.insno_file_name
                    } else {
                        savedriver1.insurance_data = insurancedata
                        savedriver1.licno_file_location = ""
                        savedriver1.licno_file_name = ""
                    }
                }


                //            val bundle = this.arguments
//            val request = bundle?.get("DriverDetails")
//            val res = bundle?.get("LoginRes")
//
//            if (request != null) {
//                savedriver1 = Gson().fromJson(request.toString(), savedriver::class.java)
//                // savedriver1!!.doc_data=stringBase64ImageProfile
//                Log.e("postdata", "" + request)
//            }

                // Log.e("postdata", "" + Gson().toJson(request))

                val bundle = Bundle()
//                bundle.putString("DriverDetails", Gson().toJson(savedriver1))
//                bundle.putString("LoginRes", res.toString())
                findNavController().navigate(R.id.action_ThirdFragment_to_FourthFragment)
                Pref_storage.setDetail(
                    requireActivity(),
                    "Driverdetails",
                    Gson().toJson(savedriver1)
                )
            }

        }

        dataBinding.imgAadharcard.setOnClickListener {
            (activity as DashboardActivity).pickPhotoImages(
                dataBinding.imgAadharcard,
                dataBinding.imagenameaadhar,
                1
            )

        }
        dataBinding.imgDriverLicense.setOnClickListener {
            (activity as DashboardActivity).pickPhotoImages(
                dataBinding.imgDriverLicense,
                dataBinding.imagenamelicense,
                2
            )

        }
        dataBinding.imgInsurance.setOnClickListener {
            (activity as DashboardActivity).pickPhotoImages(
                dataBinding.imgInsurance,
                dataBinding.imagenameinsurance,
                3
            )

        }
        dataBinding.imgPancard.setOnClickListener {
            (activity as DashboardActivity).pickPhotoImages(
                dataBinding.imgPancard,
                dataBinding.imagenamepancard,
                4
            )

        }
        dataBinding.imgLicensePlate.setOnClickListener {
            (activity as DashboardActivity).pickPhotoImages(
                dataBinding.imgLicensePlate,
                dataBinding.imagenameplate,
                5
            )

        }

        dataBinding.imagenameplate.setOnClickListener {
            if (dataBinding.imagenameplate.text.toString().isNotEmpty()) {

                val fileFolder = Utility().getFileFolder(context)

                val mediaFile =
                    File(fileFolder.path + File.separator + dataBinding.imagenameplate.text.toString())

                val decodedString: ByteArray = Base64.decode(
                    (activity as DashboardActivity).getvehicles(),
                    Base64.DEFAULT
                )
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                PopupImageView(requireActivity(), decodedByte, "").showFullImageView()
            }
        }

        dataBinding.imagenameplate.setOnClickListener {
            if (dataBinding.imagenameplate.text.toString().isNotEmpty()) {

                val decodedString: ByteArray = Base64.decode((activity as DashboardActivity).getvehicles(), Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                if(decodedByte==null){
                    PopupImage(requireActivity(),savedriver1.plateno_file_location+savedriver1.plateno_file_name).showFullImageView()

                }else{

                    PopupImageView(requireActivity(),decodedByte,savedriver1.plateno_file_location+savedriver1.plateno_file_name).showFullImageView()            }
            }
        }


        dataBinding.imagenamelicense.setOnClickListener {
            if (dataBinding.imagenamelicense.text.toString().isNotEmpty()) {

                try{
                    val fileFolder = Utility().getFileFolder(context)

                    val mediaFile = File(fileFolder.path + File.separator + dataBinding.imagenamelicense.text.toString())

                    val decodedString: ByteArray = Base64.decode((activity as DashboardActivity).getlicense(), Base64.DEFAULT)
                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    if(decodedByte==null){
                        PopupImage(requireActivity(),savedriver1.licno_file_location+savedriver1.licno_file_name).showFullImageView()

                    }else{

                        PopupImageView(requireActivity(),decodedByte,savedriver1.licno_file_location+savedriver1.licno_file_name).showFullImageView()
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }

        }
        dataBinding.imagenameinsurance.setOnClickListener {
            if (dataBinding.imagenameinsurance.text.toString().isNotEmpty()) {
                val decodedString: ByteArray = Base64.decode((activity as DashboardActivity).getinsurancebase64(), Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                if(decodedByte==null){
                    PopupImage(requireActivity(),savedriver1.insno_file_location+savedriver1.insno_file_name).showFullImageView()

                }else{

                    PopupImageView(requireActivity(),decodedByte,savedriver1.insno_file_location+savedriver1.insno_file_name).showFullImageView()
                }
            }
        }


        dataBinding.imagenameaadhar.setOnClickListener {
            if (dataBinding.imagenameaadhar.text.toString().isNotEmpty()) {
                val decodedString: ByteArray = Base64.decode(
                    (activity as DashboardActivity).getinsurancebase64(),
                    Base64.DEFAULT
                )
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                if (decodedByte == null) {
                    PopupImage(
                        requireActivity(),
                        savedriver1.aadhar_file_location + savedriver1.aadhar_file_name
                    ).showFullImageView()

                } else {

                    PopupImageView(
                        requireActivity(),
                        decodedByte,
                        savedriver1.aadhar_file_location + savedriver1.aadhar_file_name
                    ).showFullImageView()
                }
            }
        }

        dataBinding.imagenamepancard.setOnClickListener {
            if (dataBinding.imagenamepancard.text.toString().isNotEmpty()) {
                val decodedString: ByteArray = Base64.decode(
                    (activity as DashboardActivity).getinsurancebase64(),
                    Base64.DEFAULT
                )
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                if (decodedByte == null) {
                    PopupImage(
                        requireActivity(),
                        savedriver1.panno_file_location + savedriver1.panno_file_name
                    ).showFullImageView()

                } else {

                    PopupImageView(
                        requireActivity(),
                        decodedByte,
                        savedriver1.panno_file_location + savedriver1.panno_file_name
                    ).showFullImageView()
                }
            }
        }

    }


    fun updateVehicleSpinner() {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, vehicle1)
        dataBinding.vehileSpinner.setAdapter(adapter)

        dataBinding.vehileSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                vehicleItem = vehicle1.get(position)
                Pref_storage.setDetail(
                    requireActivity(),
                    "Vehiclename",
                    vehicleItem.vehicle_type_name
                )
                Pref_storage.setDetail(
                    requireActivity(),
                    "Vehicleid",
                    vehicleItem.vehicle_type_id.toString()
                )
            }
    }

    fun updateStateSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, state)
        dataBinding.stateSpinner.setAdapter(adapter)

        dataBinding.stateSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                stateItem = state.get(position)
                Pref_storage.setDetail(requireActivity(), "Statename", stateItem.state_name)
                fetchDistrictDetails(stateItem.state_id.toString())

            }
    }


    fun updateDistrictSpinner(district: getDistrictDetails) {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, district)
        dataBinding.citySpinner.setAdapter(adapter)
        dataBinding.citySpinner.onItemClickListener =
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
                    districtItem = this@Driverdetails1.district.get(position)
                    Pref_storage.setDetail(
                        requireActivity(),
                        "Districtname",
                        districtItem.district_name
                    )
                    Pref_storage.setDetail(
                        requireActivity(),
                        "Districtid",
                        districtItem.district_id.toString()
                    )

                }


            }

    }

    fun validation(
        plate: String, insurance: String, license: String, adhar: String, pan: String,
        type: String, state: String, city: String
    ): String? {
        if (plate.isNullOrEmpty()) {
            dataBinding.platenumber.error = "Vehicle number should not be empty"

            return "Vehicle number should not be empty"
        } else if (insurance.isNullOrEmpty()) {
            dataBinding.insurancenumber.error = "Insurance number should not be empty"

            return "Insurance number should not be empty"
        } else if (license.isNullOrEmpty()) {
            dataBinding.driverlicenseEdittext.error = "license number should not be empty"

            return "license number should not be empty"
        } else if (adhar.isNullOrEmpty()) {
            dataBinding.adharEdittext.error = "Adhar number should not be empty"

            return "Adhar number should not be empty"
        } else if (pan.isNullOrEmpty()) {
            dataBinding.panEdittext.error = "Pan number should not be empty"

            return "Pan number should not be empty"
        } else if (type.isNullOrEmpty()) {
            dataBinding.vehileSpinner.error = "Vehicle Type should not be empty"

            return "Vehicle Type should not be empty"
        } else if (state.isNullOrEmpty()) {
            dataBinding.stateSpinner.error = "State should not be empty"

            return "State should not be empty"
        } else if (city.isNullOrEmpty()) {
            dataBinding.citySpinner.error = "City should not be empty"

            return "City should not be empty"
        } else if (dataBinding.platenumber.text!!.length < 10) {
            dataBinding.platenumber.error = "Vehicle number not valid must contains 10 Number"
            return "Vehicle number not valid must contains 10 Number"
        } else if (dataBinding.insurancenumber.text!!.length < 20) {
            dataBinding.insurancenumber.error = "Insurance number not valid must contains 20 Number"
            return "Insurance number not valid must contains 20 Number"
        } else if (dataBinding.driverlicenseEdittext.text!!.length < 16) {
            dataBinding.driverlicenseEdittext.error =
                "License number not valid must contains 16 Number"
            return "License number not valid must contains 16 Number"
        } else if (dataBinding.adharEdittext.text!!.length < 12) {
            dataBinding.adharEdittext.error = "Aadhar number not valid must contains 12 Number"
            return "Phone number not valid must contains 10 Number"
        } else if (dataBinding.panEdittext.text!!.length < 10) {
            dataBinding.panEdittext.error = "Pan number not valid must contains 10 Number"
            return "Pan number not valid must contains 10 Number"
        } else if (dataBinding.insuranceExpirytext.text.toString().isNullOrEmpty()) {
            dataBinding.insuranceExpirytext.error = "Insurance Expiry Date should not be empty"
            return "Insurance Expiry Date should not be empty"
        } else if (dataBinding.licenseExpirydate.text.toString().isNullOrEmpty()) {
            dataBinding.insuranceExpirytext.error = "License Expiry Date should not be empty"
            return "License Expiry Date should not be empty"
        } else {
            return null
        }

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

