package com.wings.mile.model

class getDistrictDetails : ArrayList<getDistrictDetailsItem>()

data class getDistrictDetailsItem(
    val country_id: Int,
    val country_name: String,
    val district_id: Int,
    val district_name: String,
    val state_id: Int,
    val state_name: String
){
    override fun toString(): String {
        return district_name
    }

}