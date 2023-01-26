package com.wings.mile.model

class loginResponse : ArrayList<loginResponseItem>()

data class loginResponseItem(
    val email_id: String,
    val name: String,
    val phone_num: String,
    val user_id: Int,
    val user_type_flg: String
)