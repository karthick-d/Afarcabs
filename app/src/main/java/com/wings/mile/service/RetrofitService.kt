package com.wings.mile.service

import bank_details
import com.google.gson.GsonBuilder
import com.wings.mile.ItemList
import com.wings.mile.model.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.logging.Level


interface RetrofitService {

    @GET("api/v1/people")
    suspend fun getAllPeople(): ItemList

    @GET("api/v1/rooms")
    suspend fun getAllRooms(): ItemList

    @GET("api/GetUserDetails")
    suspend fun getLogin(
        @Query("PhoneNumber") PhoneNumber: String,
        @Query("Password") Password: String
    ): loginResponse

    @GET("api/GetUpdatedProfile")
    suspend fun getupdateprofile(
        @Query("userId") userid: Int
    ): loginResponse

    @POST("api/PostUpdatedProfile/SignUpDetails")
    suspend fun postSignUp(
        @Body request: SignUpRequest
    ): ResponseBody

    @POST("api/PostAddReferral/LoginReferralDetails")
    suspend fun postreferall(
        @Body request: ReferallRequest
    ): response
    @POST("api/SaveUserDetails")
    suspend fun postDriverDetails(
        @Body request: savedriver
    ): ResponseBody

    @POST("api/PostDriverRegPaymentDetails")
    suspend fun postrazorDriverDetails(
        @Body request: razorpostdriver
    ): ResponseBody

    @POST("api/PostDriverPaymentDetails")
    suspend fun postaccountDriverDetails(
        @Body request: accounts_driver
    ): ResponseBody

    @GET("api/GetGenderDetails")
    suspend fun getGender(@Query("settingsName") name: String): gender


    @GET("api/GetVehicleDetails")
    suspend fun getVehicle(): vehicle

    @GET("api/GetStateDetails")
    suspend fun getState(): getstate

    @GET("api/GetDistrictDetails")
    suspend fun getDistrict(
        @Query("stateId") stateId: String,
        @Query("countryId") countryId: String
    ): getDistrictDetails

    @GET("api/GetDriverDetails")
    suspend fun getdriverdetails(@Query("phoneNumber") phonenum: String): getdetails


    @GET("api/bankDetails/GetBankDetails/{ifscCode}")
    suspend fun getbankdetails(
        @Path("ifscCode") ifscCode: String): bank_details


    @GET("api/GetCountryDetails")
    suspend fun getCountryDetails(): getCountry

    @GET("api/ValidateUser")
    suspend fun validateuser(@Query("emailId") emailId: String): String

    @GET("api/GetUpdatedProfile")
    suspend fun updateeuser(@Query("userId") userId: Int): loginResponse
    companion object {

        //https: //mileapi2.azurewebsites.net/
        var retrofitService: RetrofitService? = null
        var gson = GsonBuilder()
            .setLenient()
            .create()

        fun getInstance(): RetrofitService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            if (retrofitService == null) {

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://afar-api.azurewebsites.net")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)

            }
            return retrofitService!!
        }
    }

}



