package com.marvsystems.fotosoftapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Lab(
    @PrimaryKey val id: Int,
    val compId: Int,
    @ColumnInfo(name = "licenseKey") val licenseKey: String?,
    @ColumnInfo(name = "compName") val compName: String?,
    @ColumnInfo(name = "httpAddressUpload") val httpAddressUpload: String?,
    @ColumnInfo(name = "ftpfolderImages") val ftpfolderImages: String?,
    @ColumnInfo(name = "compAdd") val compAdd: String?,
    @ColumnInfo(name = "compCity") val compCity: String?,
    @ColumnInfo(name = "compState") val compState: String?,
    @ColumnInfo(name = "compLocation") val compLocation: String?,
    @ColumnInfo(name = "compPhone") val compPhone: String?,
    @ColumnInfo(name = "compEmail") val compEmail: String?,
    @ColumnInfo(name = "contactPerson") val contactPerson: String?,
    @ColumnInfo(name = "contactPersonMobile") val contactPersonMobile: String?,
    @ColumnInfo(name = "contactPersonInternalMobile") val contactPersonInternalMobile: String?,
    @ColumnInfo(name = "compLogoImage") val compLogoImage: String?,
    @ColumnInfo(name = "albumUploaderNo") val albumUploaderNo: String?,
    @ColumnInfo(name = "albumBookingNo") val albumBookingNo: String?,
    @ColumnInfo(name = "delivery1No") val delivery1No: String?,
    @ColumnInfo(name = "delivery2No") val delivery2No: String?,
    @ColumnInfo(name = "accountsNo") val accountsNo: String?,
    @ColumnInfo(name = "technicalSupportNo") val technicalSupportNo: String?,
    @ColumnInfo(name = "complainNo") val complainNo: String?,
    @ColumnInfo(name = "othersNo") val othersNo: String?,
    @ColumnInfo(name = "username") val username: String?,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "remember") val remember: Boolean?,
    @ColumnInfo(name = "jwtToken") val jwtToken: String?
) : Serializable