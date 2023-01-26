package com.wings.mile.Adddrivers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Base64.encodeToString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.awesomedialog.*
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.Utils.Utility
import com.wings.mile.databinding.ActivityMain2Binding
import com.wings.mile.model.getdriver
import com.wings.mile.model.savedriver
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.io.*
import java.util.*


class Driverdetails2 : Fragment() {

    private var dataBinding: ActivityMain2Binding? = null
    private var viewModel: MainViewModel? = null

    private val retrofitService = RetrofitService.getInstance()
    private var myContext: Context? = null

    private var savedriver1:savedriver? = null
    private var getdrivers:getdriver? = null

    private var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    private var pdfFilePath: String? = null

    private var myFilePath: File? = null;
    private var stringBase64ImageProfile: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ActivityMain2Binding.inflate(inflater, container, false)
        initializeView()
        setViewModel()
        return dataBinding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLauncherResult()

    }

    override fun onStart() {
        super.onStart()
    }

    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
    }

    override fun onAttach(context: Context) {
        myContext = context
        super.onAttach(context)

    }

    private fun initializeView() {
        dataBinding!!.lifecycleOwner = this
        try {
            (activity as DashboardActivity).icon()
            if(Pref_storage.getDetail(requireActivity(),"getdriverdetails").toString().equals("")) {
                if (Pref_storage.getDetail(requireActivity(), "driverpdf").isNotEmpty()) {
                    dataBinding!!.pdffileTxt.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "driverpdf"
                        )
                    )
                }

                if(Pref_storage.getDetail(requireActivity(),"idproof").isNotEmpty()) {
                    dataBinding!!.proofSpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "idproof"
                        )
                    )
                }
            }else {


                getdrivers = Gson().fromJson(
                    Pref_storage.getDetail(requireActivity(), "getdriverdetails"),
                    getdriver::class.java
                )

                if (Pref_storage.getDetail(requireActivity(), "idproof").toString().equals("")) {
                    dataBinding!!.proofSpinner.setText(getdrivers!!.id_proof_name)
                } else {
                    dataBinding!!.proofSpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "idproof"
                        )
                    )

                }
                if (Pref_storage.getDetail(requireActivity(), "driverpdf").toString().equals("")) {
                    dataBinding!!.pdffileTxt.text =  getdrivers!!.doc_file_name

                } else {

                    dataBinding!!.pdffileTxt.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "driverpdf"
                        )
                    )

                }

            }

        }catch (e:Exception){
            e.printStackTrace()
        }

        dataBinding!!.finish.setOnClickListener {
            savedriver1 = Gson().fromJson(Pref_storage.getDetail(requireActivity(),"Driverdetails"), savedriver::class.java)
            savedriver1!!.doc_data=Pref_storage.getDetail(requireActivity(),"pdfbase64")
            savedriver1!!.id_Proof_Name= dataBinding!!.proofSpinner.text.toString()
            if(Pref_storage.getDetail(requireActivity(),"getdriverdetails").toString().equals("")) {
                savedriver1!!.doc_data = stringBase64ImageProfile
                savedriver1!!.doc_file_name = ""
                savedriver1!!.doc_file_location = ""
                savedriver1!!.en_flag = "P"
                savedriver1!!.id_Proof_Name = dataBinding!!.proofSpinner.text.toString()
            }else {
                if (getdrivers!!.doc_file_location!!.isNotEmpty() && stringBase64ImageProfile!!.toString()
                        .isEmpty()
                ) {
                    savedriver1!!.doc_file_location = getdrivers!!.doc_file_location
                    savedriver1!!.doc_file_name = getdrivers!!.doc_file_name
                    savedriver1!!.en_flag = "P"
                    savedriver1!!.id_Proof_Name = dataBinding!!.proofSpinner.text.toString()
                } else {
                    savedriver1!!.doc_data = stringBase64ImageProfile
                    savedriver1!!.doc_file_name = ""
                    savedriver1!!.doc_file_location = ""
                    savedriver1!!.en_flag = "P"
                    savedriver1!!.id_Proof_Name = dataBinding!!.proofSpinner.text.toString()
                }
            }
            // savedriver1!!.plateNo_data=(activity as DashboardActivity).encodeTobase64(StringToBitMap(Pref_storage.getDetail(requireActivity(),"Vehiclebitmap"))!!)
            Log.e("savedriver",""+savedriver1.toString())
            dataBinding!!.LoginAvi.show()
            viewModel!!.postDriverDetails(savedriver1!!).observe(requireActivity(), Observer {
                if(it!=null) {
                    dataBinding!!.LoginAvi.hide()
                    AwesomeDialog.build(requireActivity())
                        .title("Congratulations")
                        .body("Driver Details Added Successfully")
                        .icon(R.drawable.ic_user_metrics_icn)
                        .onPositive("Ok") {
                            val intent = Intent(context, DashboardActivity::class.java)
                            intent.putExtra("LoginRes", Pref_storage.getDetail(requireActivity(),"LoginRes"))
                            intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                            Pref_storage.setDetail(requireActivity(),"Driverdetails","")
                            Pref_storage.setDetail(requireActivity(),"pdfbase64","")
                            Pref_storage.setDetail(requireActivity(),"idproof","")
                            startActivity(intent)
                        }
                }else{
                    dataBinding!!.LoginAvi.hide()
                    Toast.makeText(context,"Email-ID/Phone Number already exits",Toast.LENGTH_LONG).show()
                }
            })
        }
        dataBinding!!.uploadfile.setOnClickListener {
            //(activity as MainActivity).pickPhotoImage(dataBinding.photoEdittexts)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            if (someActivityResultLauncher != null) {
                someActivityResultLauncher!!.launch(intent)
            }

        }

        dataBinding!!.pdffileTxt.setOnClickListener {
            if (pdfFilePath != null) {

                val target = Intent(Intent.ACTION_VIEW)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {

                    val pdfUri = Uri.fromFile(myFilePath)

                    target.setDataAndType(pdfUri, "application/pdf")
                    target.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val intent = Intent.createChooser(target, getString(R.string.open_file))
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {

                    }
                } else {

                    val contentUri = FileProvider.getUriForFile(
                        Objects.requireNonNull(requireActivity().applicationContext),
                        "com.wings.mile.provider", myFilePath!!
                    )
                    target.setDataAndType(contentUri, "application/pdf")
                    target.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val intent = Intent.createChooser(target, "Open File")
                    startActivity(intent)

                }
            }
        }
        updateproofSpinner()

    }
    fun updateproofSpinner(){
        val list: MutableList<String> = ArrayList()
        list.add("Vehicle Documents")
        list.add("Driver Documents")
        var adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, list)
        dataBinding!!.proofSpinner.setAdapter(adapter)

        dataBinding!!.proofSpinner.onItemClickListener= object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {}

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Pref_storage.setDetail(requireActivity(),"idproof",dataBinding!!.proofSpinner.text.toString())
            }


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

                    val inputStream = requireActivity()!!.contentResolver.openInputStream(uri!!)
                    val pdfInBytes = ByteArray(inputStream!!.available())

                    val fileSizeInKB: Int = pdfInBytes.size / 1024
                    if (fileSizeInKB > 20 || fileSizeInKB < 12) {
                        showAlertDialog()
                        stringBase64ImageProfile = null
                    } else {
                        val uriString : String = uri.toString()


                        myFilePath = File(uriString)
                        pdfFilePath = myFilePath!!.absolutePath
                        getPDFPath(uri)
                        Log.e("bbbbbbbbb",""+getPDFPath(uri))
                        if (uriString.startsWith("content://")) {
                            var cursor: Cursor? = null
                            try {
                                cursor = requireActivity().contentResolver
                                    .query(uri, null, null, null, null)
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName =
                                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                                    dataBinding!!.pdffileTxt.text = displayName
                                    dataBinding!!.pdffileTxt.visibility = View.VISIBLE
                                    dataBinding!!.pdffileTxt.setTextColor(Color.BLACK)
                                    Pref_storage.setDetail(requireActivity(),"driverpdf",dataBinding!!.pdffileTxt.text.toString())
                                }
                            } finally {
                                cursor!!.close()
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFilePath!!.name

                            dataBinding!!.pdffileTxt.text = displayName
                            dataBinding!!.pdffileTxt.visibility = View.VISIBLE
                            dataBinding!!.pdffileTxt.setTextColor(Color.BLACK)
                            Pref_storage.setDetail(requireActivity(),"driverpdf",dataBinding!!.pdffileTxt.text.toString())


                        }

                    }
                }
            }
        }
    }

    private fun encodeFileToBase64Binary(yourFile: File): String? {
        val size = yourFile.length().toInt()
        val bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(yourFile))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    fun getPDFPath(uri: Uri?): String? {
        var absolutePath = ""
        try {
            val inputStream = requireActivity()!!.contentResolver.openInputStream(uri!!)
            val pdfInBytes = ByteArray(inputStream!!.available())
            Log.e("bbbbbbbbb",""+pdfInBytes)
            inputStream.read(pdfInBytes)
            stringBase64ImageProfile = encodeToString(pdfInBytes, Base64.DEFAULT)
            Pref_storage.setDetail(requireActivity(),"pdfbase64",stringBase64ImageProfile)
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
                requireActivity()!!.getExternalFilesDir(Environment.DIRECTORY_DCIM)
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
    /*Exit Application alert functionality*/
    private fun showAlertDialog() {
        AlertDialog.Builder(requireActivity())
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



}
