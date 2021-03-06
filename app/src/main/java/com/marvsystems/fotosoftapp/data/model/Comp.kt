package com.marvsystems.fotosoftapp.data.model

data class Comp(
    val accountHolderName: String,
    val accountNo: String,
    val activeYn: Boolean,
    val address: String,
    val adminWebServiceYn: String,
    val allowed: Boolean,
    val amountEditableYn: String,
    val bankName: String,
    val branchName: String,
    val compAdd: String,
    val compCity: String,
    val compEmail: String,
    val compId: Int,
    val compLocation: String,
    val compLogoImage: String,
    val compName: String,
    val compPhone: String,
    val compPinCode: String,
    val compSname: String,
    val compState: String,
    val compStateId: Int,
    val compWebSite: String,
    val contactPerson: String,
    val contactPersonInternalMobile: String,
    val contactPersonMobile: String,
    val copyDatabaseYn: String,
    val ifscCode: String,
    val mobileAppAllowed: Boolean,
    val ownerMobile: String,
    val ownerName: String,
    val paymentMinAmount: Double,
    val paymentUrlType: String,
    val productSoldBy: String,
    val productSoldByDispYn: String,
    val productSoldById: Int,
    val showAddress: String,
    val showCity: String,
    val showCompName: String,
    val showEmail: String,
    val showLocation: String,
    val showState: String,
    val smsAllowed: Boolean,
    val smsNo: String,
    val smsQuota: Int,
    val smsServiceYn: String,
    val smsUsed: Int,
    val supportDetails: List<Any>,
    val surchargeAmount: Double,
    val surchargeType: String,
    val userMasts: List<Any>
)