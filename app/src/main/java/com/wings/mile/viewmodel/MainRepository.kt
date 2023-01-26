package com.wings.mile.viewmodel

import com.wings.mile.model.*
import com.wings.mile.service.RetrofitService

class MainRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getAll() = retrofitService.getAllPeople()

    suspend fun getLoginRequest(phonenumber:String,password:String) = retrofitService.getLogin(phonenumber,password)

    suspend fun getupdateprofile(userid:Int) = retrofitService.getupdateprofile(userid)


    suspend fun getUserdetails(phonenumber:String) = retrofitService.getdriverdetails(phonenumber)

    suspend fun getBankdetails(ifscCode:String) = retrofitService.getbankdetails(ifscCode)

    suspend fun sendSignUpRequest(request: SignUpRequest) = retrofitService.postSignUp(request)

    suspend fun sendreferall(request: ReferallRequest) = retrofitService.postreferall(request)

    suspend fun fetchGender() = retrofitService.getGender("Gender")

    suspend fun fetchVehicle() = retrofitService.getVehicle()

    suspend fun fetchState() = retrofitService.getState()

    suspend fun fetchCountry() = retrofitService.getCountryDetails()

    suspend fun fetchDistrict(stateid:String,countryid:String) = retrofitService.getDistrict(stateid,countryid)

    suspend fun saveDriverDetails(request: savedriver) = retrofitService.postDriverDetails(request)

    suspend fun postrazorDriverDetails(request: razorpostdriver) = retrofitService.postrazorDriverDetails(request)

    suspend fun postaccountDriverDetails(request: accounts_driver) = retrofitService.postaccountDriverDetails(request)

    suspend fun validateuser(phonenumber:String) = retrofitService.validateuser(phonenumber)

    suspend fun updateuser(userid:Int) = retrofitService.updateeuser(userid)
}