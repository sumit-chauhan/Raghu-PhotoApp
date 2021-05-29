package com.marvsystems.fotosoftapp.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SignUpRequestModel : Serializable {
    var userName: String? = null
    var passWd: String? = null
    var appVersion: String? = null
    var osname: String? = null
    var osservicePack: String? = null
    var osbit: String? = null
    var ipAddress: String? = null

    @SerializedName("compId")
    var compID: Int? = null
    var partyType: String? = null
    var partyCode: String? = null
    var partyName: String? = null
    var partyAddress: String? = null
    var city: String? = null

    @SerializedName("emailId")
    var emailID: String? = null
    var phone: String? = null

    @SerializedName("usrId")
    var usrID: Int? = null

    @SerializedName("stateId")
    var stateID: Int? = null

    @SerializedName("districtId")
    var districtID: Int? = null

    @SerializedName("cityId")
    var cityID: Int? = null
    var pinCode: Long? = null
    var message: String? = null
    var hasError: Boolean? = null
}