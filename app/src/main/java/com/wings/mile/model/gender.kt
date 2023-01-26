package com.wings.mile.model

class gender : ArrayList<genderItem>()

data class genderItem(
    val en_flg: String,
    val setting_desc: String,
    val settings_id: Int,
    val settings_name: String,
    val settings_value: String
){
    override fun toString(): String {
        return setting_desc
    }

}