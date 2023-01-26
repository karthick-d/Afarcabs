package com.wings.mile.model

data class SignUpRequest(
    var email_id: String,
    var first_name: String,
    var last_name: String,
    var phone_num: String,
    var user_Password: String,
    var user_id: Int,
    var en_flg:String,
    var user_type_flg: String,
    var notification_token: String
)