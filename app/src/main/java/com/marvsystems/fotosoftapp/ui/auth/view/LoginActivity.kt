package com.marvsystems.fotosoftapp.ui.auth.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.api.ApiServiceImpl
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.ui.auth.viewmodel.LoginViewModel
import com.marvsystems.fotosoftapp.ui.base.BaseActivity
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.ui.dashboard.view.DashboardActivity
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.CommonFunctions
import com.marvsystems.fotosoftapp.utils.Constants.APP_VERSION
import com.marvsystems.fotosoftapp.utils.Status
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.item_labs.iv_lab_icon
import kotlinx.android.synthetic.main.item_labs.tv_lab_address
import kotlinx.android.synthetic.main.item_labs.tv_lab_name
import kotlinx.android.synthetic.main.layout_helpline.*
import kotlinx.android.synthetic.main.layout_lab_info_header.*

class LoginActivity : BaseActivity() {

    private var data: Lab? = null
    private lateinit var loginViewModel: LoginViewModel
    private var isRemember = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setupViewModel()
        observeLoginAPICall()
        clearError(username_til, username_et)
        clearError(password_til, password_et)
        showBackOnToolbar()
        data = intent.getSerializableExtra("DATA") as Lab?
        setLabInfo()
        isRemember(data!!)
        btn_signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("LAB", data)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this)) {
                login(username_et.text.toString(), password_et.text.toString())
            } else {
                showToast("Please check your internet connectivity")
            }
        }

        checkbox_remember.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener,
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                TODO("Not yet implemented")
            }

            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                Log.e("Checked", "" + isChecked.toString())
                isRemember = isChecked
            }

        })

        btn_reset.setOnClickListener {
            clearFields()
        }
    }

    private fun clearFields() {
        username_et.text = null
        password_et.text = null
        checkbox_remember.isChecked = false
        isRemember = false
    }

    private fun isRemember(lab: Lab) {
        isRemember = lab.remember!!
        if (lab.remember) {
            checkbox_remember.isChecked = lab.remember
            username_et.text = SpannableStringBuilder(lab.username)
            password_et.text = SpannableStringBuilder(lab.password)
        }
    }

    private fun login(userName: String, password: String) {
        if (validate(userName, password)) {
            val loginModel = LoginModel(APP_VERSION, data?.compId!!, 0, "", password, userName)
            loginViewModel.login(loginModel, isRemember)
        }
    }

    private fun validate(userName: String?, password: String?): Boolean {
        return when {
            userName.isNullOrEmpty() -> {
                username_til.error = getString(R.string.error_username)
                false
            }
            password.isNullOrEmpty() -> {
                password_til.error = getString(R.string.error_password)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(LoginViewModel::class.java)
    }

    private fun observeLoginAPICall() {
        loginViewModel.getUser().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { _ ->
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("DATA", it.data)
                        intent.putExtra("LAB", data)
                        startActivity(intent)
                        finishAffinity()
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

    private fun setLabInfo() {
        tv_lab_name.text = data?.compName
        tv_lab_address.text =
            String.format(
                "%s, %s",
                data?.compCity,
                data?.compState
            )
//        val phone = String.format(
//            "%s, %s, %s",
//            data?.contactPersonMobile,
//            data?.compPhone,
//            data?.contactPersonInternalMobile
//        )
//        tv_lab_mobile_number.text = phone

        var phoneNo = ""

        if (data?.contactPersonMobile != null && data?.contactPersonMobile != "") {
            phoneNo = String.format("%s", data?.contactPersonMobile)
        }

        if (data?.compPhone != null && data?.compPhone != "") {
            if (phoneNo.length == 0) phoneNo = String.format("%s", data?.compPhone)
            else phoneNo = String.format("%s, %s", phoneNo, data?.compPhone)
        }

        if (data?.contactPersonInternalMobile != null && data?.contactPersonInternalMobile != "") {
            if (phoneNo.length == 0) phoneNo =
                String.format("%s", data?.contactPersonInternalMobile)
            else phoneNo = String.format("%s, %s", phoneNo, data?.contactPersonInternalMobile)
        }

        tv_lab_mobile_number.text = phoneNo

        iv_lab_icon.setImageBitmap(AppUtil.convertBase64ToBitmap(data?.compLogoImage!!))

        tv_contact_1.text = "+91-${data?.albumUploaderNo}"
        tv_contact_2.text = "+91-${data?.albumBookingNo}"
        tv_delivery.text = "+91-${data?.delivery1No}"
        tv_complaint.text = "+91-${data?.complainNo}"
    }

    override fun onResume() {
        CommonFunctions().updateNetworkImage(
            this, network_type
        );
        super.onResume()
    }
}