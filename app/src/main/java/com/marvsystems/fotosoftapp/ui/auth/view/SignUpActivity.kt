package com.marvsystems.fotosoftapp.ui.auth.view

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.api.ApiServiceImpl
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.data.model.SignUpRequestModel
import com.marvsystems.fotosoftapp.data.model.State
import com.marvsystems.fotosoftapp.ui.auth.viewmodel.SignUpViewModel
import com.marvsystems.fotosoftapp.ui.base.BaseActivity
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.Status
import com.marvsystems.fotosoftapp.utils.ValidationManager
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_lab_info_header.*

class SignUpActivity : BaseActivity() {

    private var lab: Lab? = null
    private lateinit var signUpViewModel: SignUpViewModel
    private var states = ArrayList<State>()
    private var statesAdapter: ArrayAdapter<State>? = null
    private var signUpRequestModel: SignUpRequestModel? = null
    private var isUserNameAvailable: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        showBackOnToolbar()

        clearError(party_name_til, party_name_et)
        clearError(address_til, address_et)
        clearError(city_til, city_et)
        clearError(pincode_til, pincode_et)
        clearError(email_til, email_et)
        clearError(mobile_til, mobile_et)
        clearError(login_id_til, login_id_et)
        clearError(password_til, password_et)
        clearError(confirm_password_til, confirm_password_et)


        setupViewModel()
        signUpRequestModel = SignUpRequestModel()
        isUserNameAvailable = false
        statesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, states)
        spinner_state.adapter = statesAdapter

        lab = intent.getSerializableExtra("LAB") as Lab
        setLabInfo()
        btn_cancel.setOnClickListener {
            finish()
        }

        btn_signup.setOnClickListener {
            if (isValid()) {
                signUpRequestModel?.userName = login_id_et.text.toString()
                signUpRequestModel?.passWd = password_et.text.toString()
                signUpRequestModel?.partyName = party_name_et.text.toString()
                signUpRequestModel?.partyAddress = address_et.text.toString()
                signUpRequestModel?.city = city_et.text.toString()
                if (!pincode_et.text.toString().isBlank()) {
                    signUpRequestModel?.pinCode = pincode_et.text.toString().toLong()
                }
                signUpRequestModel?.emailID = email_et.text.toString()
                signUpRequestModel?.phone = mobile_et.text.toString()
                signUpRequestModel?.compID = lab?.compId
                signUpRequestModel?.ipAddress = AppUtil.getIPAddress(true)
                signUpRequestModel?.partyType = "Lab"
                signUpRequestModel?.partyCode = ""
                signUpRequestModel?.usrID = 0
                signUpRequestModel?.districtID = 0
                signUpRequestModel?.cityID = 0
                signUpRequestModel?.osname = AppUtil.getOSName()
                signUpRequestModel?.osservicePack = AppUtil.getOSVersion()
                signUpRequestModel?.appVersion = AppUtil.getAppVersion()
                signUpRequestModel?.osbit = if (AppUtil.is64Bit()) "64" else "32"

                if (AppUtil.isNetworkAvailable(this)) {
                    signUpViewModel.signUp(signUpRequestModel!!)
                } else {
                    showToast("Please check your internet connectivity")
                }

            }
        }

        tv_check_user_name_availability.setOnClickListener {
            if (!login_id_et.text.isNullOrEmpty()) {
                if (AppUtil.isNetworkAvailable(this)) {
                    signUpViewModel.checkUserName(login_id_et.text.toString(), lab?.compId!!)
                } else {
                    showToast("Please check your internet connectivity")
                }
            } else {
                login_id_til.error = getString(R.string.error_invalid_login_id)
            }
        }

        observeStates()
        observeUserName()
        observeSignUp()
        stateChangeListener()
        toggleAvailability(isUserNameAvailable!!)
        signUpViewModel.fetchStates()
        loginIdChangeListener()

    }

    private fun loginIdChangeListener() {
        login_id_et?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tv_check_user_name_availability.visibility = View.GONE
//                isUserNameAvailable = false
//                toggleAvailability(isUserNameAvailable!!)
            }

        })

        login_id_et.setOnFocusChangeListener { view, hasFocus ->
//            if (!hasFocus && !login_id_et.text.isNullOrEmpty()) {
            if (!hasFocus && !login_id_et.text.isNullOrEmpty() && login_id_et.text.toString().length >= 5) {
                if (AppUtil.isNetworkAvailable(this)) {
                    signUpViewModel.checkUserName(login_id_et.text.toString(), lab?.compId!!)
                } else {
                    showToast("Please check your internet connectivity")
                }
            }
        }
    }

    private fun observeStates() {
        signUpViewModel.getStates().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        states.clear()
                        states.add(State("Select State *", 0))
                        states.addAll(result)
                        statesAdapter?.notifyDataSetChanged()
                    }
                }
                Status.LOADING -> {
                    showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    dismissProgress()
                    showToast(it.message)
                }
            }
        }
    }

    private fun observeUserName() {
        signUpViewModel.isUserNameAvailable().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        isUserNameAvailable = !result
                        toggleAvailability(isUserNameAvailable!!)
                    }
                }
                Status.LOADING -> {
                    showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    dismissProgress()
                    isUserNameAvailable = false
                    showToast(it.message)
                }
            }
        }
    }

    private fun observeSignUp() {
        signUpViewModel.getSignUp().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        if (result.hasError != null && result.hasError!!) {
                            showToast(result.message)
                        } else {
                            showToast("Registered Successfully")
                            finish()
                        }
                    }
                }
                Status.LOADING -> {
                    showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    dismissProgress()
                    showToast(it.message)
                }
            }
        }
    }

    private fun stateChangeListener() {
        spinner_state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    if (position != 0) {
                        signUpRequestModel?.stateID = states[position].id
                    } else {
                        signUpRequestModel?.stateID = 0
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setLabInfo() {
        tv_lab_name.text = lab?.compName
        tv_lab_address.text =
            String.format(
                "%s, %s",
                lab?.compCity,
                lab?.compState
            )
//        val phone = String.format(
//            "%s, %s, %s",
//            lab?.contactPersonMobile,
//            lab?.compPhone,
//            lab?.contactPersonInternalMobile
//        )
//        tv_lab_mobile_number.text = phone

        var phoneNo  = ""

        if (lab?.contactPersonMobile != null && lab?.contactPersonMobile != ""){
            phoneNo = String.format("%s", lab?.contactPersonMobile)
        }

        if (lab?.compPhone != null && lab?.compPhone != ""){
            if (phoneNo.length == 0)    phoneNo = String.format("%s", lab?.compPhone)
            else                        phoneNo = String.format("%s, %s", phoneNo, lab?.compPhone)
        }

        if (lab?.contactPersonInternalMobile != null && lab?.contactPersonInternalMobile != ""){
            if (phoneNo.length == 0)    phoneNo = String.format("%s", lab?.contactPersonInternalMobile)
            else                        phoneNo = String.format("%s, %s", phoneNo, lab?.contactPersonInternalMobile)
        }

        tv_lab_mobile_number.text = phoneNo


        iv_lab_icon.setImageBitmap(AppUtil.convertBase64ToBitmap(lab?.compLogoImage!!))
    }

    private fun setupViewModel() {
        signUpViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(SignUpViewModel::class.java)
    }



// Show all fields one go
    private fun isValid(): Boolean {
        var isValid = true

    if (confirm_password_et.text.isNullOrEmpty()) {
        confirm_password_til.error = getString(R.string.error_invalid_confirm_password)
        confirm_password_et.requestFocus()
        isValid = false
    }
    else if (confirm_password_et.text.toString().length < 5) {
        confirm_password_til.error = getString(R.string.error_invalid_password_length)
        confirm_password_et.requestFocus()
        isValid = false
    }
    else if (confirm_password_et.text.toString() != password_et.text.toString()) {
        confirm_password_til.error = getString(R.string.error_password_confirm_password_does_not_match)
        confirm_password_et.requestFocus()
        isValid = false
    }


    if (password_et.text.isNullOrEmpty()) {
        password_til.error = getString(R.string.error_invalid_password)
        password_et.requestFocus()
        isValid = false
    }
    else if (password_et.text.toString().length < 5) {
        password_til.error = getString(R.string.error_invalid_password_length)
        password_et.requestFocus()
        isValid = false
    }


    if (login_id_et.text.isNullOrEmpty()) {
        login_id_til.error = getString(R.string.error_invalid_login_id)
        login_id_et.requestFocus()
        isValid = false
    }
    else if (login_id_et.text.toString().length < 5) {
        login_id_til.error = getString(R.string.error_invalid_login_id_length)
        login_id_et.requestFocus()
        isValid = false
    }



    if (mobile_et.text.isNullOrEmpty() || !ValidationManager.isValidMobileNumber(mobile_et.text)) {
        mobile_til.error = getString(R.string.error_invalid_mobile)
        mobile_et.requestFocus()
        isValid = false
    }


    if (!email_et.text.isNullOrEmpty() && !ValidationManager.isValidEmail(email_et.text)) {
        email_til.error = getString(R.string.error_invalid_email)
        email_et.requestFocus()
        isValid = false
    }


    if (!pincode_et.text.isNullOrEmpty() && !ValidationManager.isValidPinCode(pincode_et.text)) {
        pincode_til.error = getString(R.string.error_invalid_pincode)
        pincode_et.requestFocus()
        isValid = false
    }

    if (city_et.text.isNullOrEmpty()) {
        city_til.error = getString(R.string.error_invalid_city)
        city_et.requestFocus()
        isValid = false
    }
    else if (city_et.text.toString().length < 3) {
        city_til.error = getString(R.string.error_invalid_city_length)
        city_et.requestFocus()
        isValid = false
    }


    if (signUpRequestModel?.stateID == 0) {
        showToast("Please select a valid State")
        isValid = false
    }


    if (party_name_et.text.isNullOrEmpty()) {
        party_name_til.error = getString(R.string.error_invalid_party_name)
        party_name_et.requestFocus()
        isValid = false
    }
    else if (party_name_et.text.toString().length < 5) {
        party_name_til.error = getString(R.string.error_invalid_party_name_length)
        party_name_et.requestFocus()
        isValid = false
    }

        return isValid
    }


    // shhow one field at a time
//    private fun isValid(): Boolean {
//        var isValid = false
//        if (party_name_et.text.isNullOrEmpty()) {
//            party_name_til.error = getString(R.string.error_invalid_party_name)
//            party_name_et.requestFocus()
//        }
//        else if (party_name_et.text.toString().length < 5) {
//            party_name_til.error = getString(R.string.error_invalid_party_name_length)
//            party_name_et.requestFocus()
//        }
//        else if (signUpRequestModel?.stateID == 0) {
//            showToast("Please select a valid State")
//        }
//        else if (city_et.text.isNullOrEmpty()) {
//            city_til.error = getString(R.string.error_invalid_city)
//            city_et.requestFocus()
//        }
//        else if (city_et.text.toString().length < 3) {
//            city_til.error = getString(R.string.error_invalid_city_length)
//            city_et.requestFocus()
//        }
//        else if (!pincode_et.text.isNullOrEmpty() && !ValidationManager.isValidPinCode(pincode_et.text)) {
//            pincode_til.error = getString(R.string.error_invalid_pincode)
//            pincode_et.requestFocus()
//        }
//        else if (!email_et.text.isNullOrEmpty() && !ValidationManager.isValidEmail(email_et.text)) {
//            email_til.error = getString(R.string.error_invalid_email)
//            email_et.requestFocus()
//        }
//        else if (mobile_et.text.isNullOrEmpty() || !ValidationManager.isValidMobileNumber(mobile_et.text)) {
//            mobile_til.error = getString(R.string.error_invalid_mobile)
//            mobile_et.requestFocus()
//        }
//        else if (login_id_et.text.isNullOrEmpty()) {
//            login_id_til.error = getString(R.string.error_invalid_login_id)
//            login_id_et.requestFocus()
//        }
//        else if (login_id_et.text.toString().length < 5) {
//            login_id_til.error = getString(R.string.error_invalid_login_id_length)
//            login_id_et.requestFocus()
//        }
//        else if (password_et.text.isNullOrEmpty()) {
//            password_til.error = getString(R.string.error_invalid_password)
//            password_et.requestFocus()
//        }
//        else if (password_et.text.toString().length < 5) {
//            password_til.error = getString(R.string.error_invalid_password_length)
//            password_et.requestFocus()
//        }
//        else if (confirm_password_et.text.isNullOrEmpty()) {
//            confirm_password_til.error = getString(R.string.error_invalid_confirm_password)
//            confirm_password_et.requestFocus()
//        }
//        else if (confirm_password_et.text.toString().length < 5) {
//            confirm_password_til.error = getString(R.string.error_invalid_password_length)
//            confirm_password_et.requestFocus()
//        }
//        else if (confirm_password_et.text.toString() != password_et.text.toString()) {
//            confirm_password_til.error = getString(R.string.error_password_confirm_password_does_not_match)
//            confirm_password_et.requestFocus()
//        }
//        else {
//            isValid = true
//        }
//        return isValid
//    }

    fun toggleAvailability(isAvailable: Boolean) {
        if (!login_id_et.text.isNullOrEmpty() && !isAvailable) {
            tv_check_user_name_availability.visibility = View.VISIBLE
            tv_check_user_name_availability.text = getString(R.string.unavailable)
            tv_check_user_name_availability.setTextColor(Color.RED)
        } else if (isAvailable) {
            tv_check_user_name_availability.visibility = View.VISIBLE
            tv_check_user_name_availability.text = getString(R.string.available)
            tv_check_user_name_availability.setTextColor(Color.GREEN)
        } else {
            tv_check_user_name_availability.visibility = View.GONE
        }
    }

}