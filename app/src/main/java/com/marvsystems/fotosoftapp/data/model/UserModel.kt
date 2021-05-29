package com.marvsystems.fotosoftapp.data.model

import java.io.Serializable

data class UserModel(
    val activeYn: String,
    val adminWebServiceYn: String,
    val appVersion: String,
    val compId: Int,
    val compressionValue: Int,
    val createdOnDt: String,
    val id: Int,
    val internalUserYn: String,
    val ipAddress: String,
    val lastLoginTime: String,
    val lastUploadTime: String,
    val loginInstance: Int,
    val osbit: String,
    val osname: String,
    val osservicePack: String,
    val partyId: Int,
    val passWd: String,
    val userName: String
) : Serializable