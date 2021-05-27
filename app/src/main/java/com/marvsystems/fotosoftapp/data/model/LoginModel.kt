package com.marvsystems.fotosoftapp.data.model

import androidx.room.Ignore
import java.io.Serializable

data class LoginModel(
    var appVersion: String,
    var compId: Int,
    var id: Int,
    var jwtToken: String,
    var password: String,
    var username: String
) : Serializable {
    @Ignore
    var message: String? = null
    @Ignore
    var userDetail: UserModel? = null
}