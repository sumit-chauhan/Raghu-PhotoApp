package com.marvsystems.fotosoftapp.utils

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern


object ValidationManager {

    fun isValidEmail(email: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(email)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
        }
    }

    fun isValidMobileNumber(mobileNumber: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(mobileNumber) && mobileNumber?.length != 10) {
            false
        } else {
            val mobilePattern = Pattern.compile("^[6-9][0-9]{9}$")
            mobilePattern.matcher(mobileNumber!!).matches()
        }
    }

    fun isValidPinCode(pinCode: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(pinCode) && pinCode?.length != 6) {
            false
        } else {
            val mobilePattern = Pattern.compile("^[1-9][0-9]{5}\$")
            mobilePattern.matcher(pinCode!!).matches()
        }
    }
}