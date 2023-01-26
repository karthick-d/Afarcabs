package com.wings.mile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.activity.OtpActivity
import com.wings.mile.adapter.AlldriversAdapter
import com.wings.mile.adapter.AlldriversAdapter.OnItemClicked
import com.wings.mile.databinding.FragmentFirstBinding
import com.wings.mile.model.getdriver
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnItemClicked {
    private var viewModel: MainViewModel? = null
    private var _binding: FragmentFirstBinding? = null
    private lateinit var allAdapter: AlldriversAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val retrofitService = RetrofitService.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setViewModel()
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Pref_storage.setDetail(requireActivity(),"Pantext","")
        Pref_storage.setDetail(requireActivity(),"Vehicletext","")
        Pref_storage.setDetail(requireActivity(),"Insurancetext","")
        Pref_storage.setDetail(requireActivity(),"adhartext","")
        Pref_storage.setDetail(requireActivity(),"Licensetext","")
        Pref_storage.setDetail(requireActivity(),"Licensenumber","")
        Pref_storage.setDetail(requireActivity(),"adharnumber","")
        Pref_storage.setDetail(requireActivity(),"Insurancenumber","")
        Pref_storage.setDetail(requireActivity(),"Vehiclenumber","")
        Pref_storage.setDetail(requireActivity(),"Pannumber","")
        Pref_storage.setDetail(requireActivity(),"Vehiclename","")
        Pref_storage.setDetail(requireActivity(),"Statename","")
        Pref_storage.setDetail(requireActivity(),"Districtname","")
        Pref_storage.setDetail(requireActivity(),"driverpdf","")
        Pref_storage.setDetail(requireActivity(),"idproof","")
        Pref_storage.setDetail(requireActivity(),"pdfbase64","")
        Pref_storage.setDetail(requireActivity(),"getdriverdetails","")
        Pref_storage.setDetail(requireActivity(),"Vehicleid","")
        Pref_storage.setDetail(requireActivity(),"Districtid","")
        binding.adddriver.setOnClickListener {
            //(activity as DashboardActivity).datapass()
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,(activity as DashboardActivity).datapass())
        }


        try {
            (activity as DashboardActivity).icons()
            //(activity as DashboardActivity).logout()

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        viewModel!!.getdriverdetails(Pref_storage.getDetail(requireActivity(),"phone")).observe(requireActivity()) {
            it.let { resource ->
                when (resource.status) {
                    com.wings.mile.Utils.Status.LOADING -> {
                    }
                    com.wings.mile.Utils.Status.SUCCESS -> {
                        binding!!.LoginAvi.hide()
                        Pref_storage.setDetail(requireActivity(),"getdriverdetails", Gson().toJson(it.data!!.get(0)))


                    }
                    com.wings.mile.Utils.Status.ERROR -> {

                        // binding!!.LoginAvi.hide()

                    }
                }
            }
        }
    }
    private fun initializeRecyclerView(driverdetails: java.util.ArrayList<getdriver>) {

        if (driverdetails.size == 0) {
            binding.textViewNoRecordFound2.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.textViewNoRecordFound2.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.isNestedScrollingEnabled = false
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            allAdapter = AlldriversAdapter(
                context!!,
                binding.textViewNoRecordFound2,
                driverdetails,
                this,
                1
            )
            binding.recyclerView.adapter = allAdapter
            allAdapter.notifyDataSetChanged()
        }
    }

    override fun onDriverdetailsclick(driversItem: getdriver) {
        val bundle = Bundle()
        bundle.putString("getdetails", Gson().toJson(driversItem))
        findNavController().navigate(R.id.action_FirstFragment_to_FifthFragment,bundle)
    }

}