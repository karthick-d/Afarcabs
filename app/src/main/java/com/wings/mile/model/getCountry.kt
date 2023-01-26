package com.wings.mile.model

class getCountry : ArrayList<getCountryItem>()

data class getCountryItem(
    val country_id: Int,
    val country_name: String
){
    override fun toString(): String {
        return country_name
    }

}