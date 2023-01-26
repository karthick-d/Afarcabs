package com.wings.mile.model

class getstate : ArrayList<getstateItem>()

data class getstateItem(
    val country_id: Int,
    val state_id: Int,
    val state_name: String
){
    override fun toString(): String {
        return state_name
    }

}