package com.wings.mile.model

class vehicle : ArrayList<vehicleItem>()

data class vehicleItem(
    val en_flg: String,
    val vehicle_type_id: Int,
    val vehicle_type_name: String
){
    override fun toString(): String {
        return vehicle_type_name
    }

}