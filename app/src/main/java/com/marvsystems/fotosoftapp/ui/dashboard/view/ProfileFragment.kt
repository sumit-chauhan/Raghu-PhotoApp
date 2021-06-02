package com.marvsystems.fotosoftapp.ui.dashboard.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.room.Room
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.api.ApiServiceImpl
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.model.SignUpRequestModel
import com.marvsystems.fotosoftapp.data.model.State
import com.marvsystems.fotosoftapp.ui.auth.viewmodel.SignUpViewModel
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.ui.dashboard.viewmodel.DashboardViewModel
import com.marvsystems.fotosoftapp.utils.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.address_et
import kotlinx.android.synthetic.main.activity_signup.city_et
import kotlinx.android.synthetic.main.activity_signup.city_til
import kotlinx.android.synthetic.main.activity_signup.email_et
import kotlinx.android.synthetic.main.activity_signup.email_til
import kotlinx.android.synthetic.main.activity_signup.login_id_et
import kotlinx.android.synthetic.main.activity_signup.login_id_til
import kotlinx.android.synthetic.main.activity_signup.mobile_et
import kotlinx.android.synthetic.main.activity_signup.mobile_til
import kotlinx.android.synthetic.main.activity_signup.party_name_et
import kotlinx.android.synthetic.main.activity_signup.party_name_til
import kotlinx.android.synthetic.main.activity_signup.pincode_et
import kotlinx.android.synthetic.main.activity_signup.pincode_til
import kotlinx.android.synthetic.main.activity_signup.spinner_state
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_lab_info_header.*

class ProfileFragment : Fragment() {
    private var signUpRequestModel: SignUpRequestModel? = null
    private lateinit var dashboardViewModel: DashboardViewModel
    private var states = ArrayList<State>()
    private var statesAdapter: ArrayAdapter<State>? = null
    private lateinit var signUpViewModel: SignUpViewModel
    private var dialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        signUpRequestModel = SignUpRequestModel()
        initLoader();
        setLabInfo()
        setProfileInfo();
    }

    private fun initLoader() {
        dialog = Dialog(requireActivity())
        dialog?.setContentView(R.layout.layout_progress)
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun setupViewModel() {
        signUpViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(SignUpViewModel::class.java)
    }

    private fun setProfileInfo() {
        party_name_et.setText(dashboardViewModel.lab?.compName);
        address_et.setText(dashboardViewModel.lab?.compAdd);
        city_et.setText(dashboardViewModel.lab?.compCity);
        pincode_et.setText(dashboardViewModel.lab?.othersNo);
        email_et.setText(dashboardViewModel.lab?.compEmail);
        mobile_et.setText(dashboardViewModel.lab?.compPhone);
        login_id_et.setText(dashboardViewModel.lab?.accountsNo);

        statesAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, states)
        spinner_state.adapter = statesAdapter

        btn_update.setOnClickListener {
            if (isValid()) {
                // TODO - update info
            }
        }

        stateChangeListener()
        signUpViewModel.fetchStates()
    }

    private fun setLabInfo() {
        tv_lab_name.text = dashboardViewModel.lab?.compName
        tv_lab_address.text =
            String.format(
                "%s, %s",
                dashboardViewModel.lab?.compCity,
                dashboardViewModel.lab?.compState
            )
//        val phone = String.format(
//            "%s, %s, %s",
//            dashboardViewModel.lab?.contactPersonMobile,
//            dashboardViewModel.lab?.compPhone,
//            dashboardViewModel.lab?.contactPersonInternalMobile
//        )
//        tv_lab_mobile_number.text = phone

        var phoneNo = ""

        if (dashboardViewModel.lab?.contactPersonMobile != null && dashboardViewModel.lab?.contactPersonMobile != "") {
            phoneNo = String.format("%s", dashboardViewModel.lab?.contactPersonMobile)
        }

        if (dashboardViewModel.lab?.compPhone != null && dashboardViewModel.lab?.compPhone != "") {
            if (phoneNo.length == 0) phoneNo =
                String.format("%s", dashboardViewModel.lab?.compPhone)
            else phoneNo = String.format("%s, %s", phoneNo, dashboardViewModel.lab?.compPhone)
        }

        if (dashboardViewModel.lab?.contactPersonInternalMobile != null && dashboardViewModel.lab?.contactPersonInternalMobile != "") {
            if (phoneNo.length == 0) phoneNo =
                String.format("%s", dashboardViewModel.lab?.contactPersonInternalMobile)
            else phoneNo = String.format(
                "%s, %s",
                phoneNo,
                dashboardViewModel.lab?.contactPersonInternalMobile
            )
        }

        tv_lab_mobile_number.text = phoneNo
        iv_lab_icon.setImageBitmap(AppUtil.convertBase64ToBitmap(dashboardViewModel.lab?.compLogoImage!!))
    }

    private fun isValid(): Boolean {
        var isValid = true

        if (confirm_password_et.text.isNullOrEmpty()) {
            confirm_password_til.error = getString(R.string.error_invalid_confirm_password)
            confirm_password_et.requestFocus()
            isValid = false
        } else if (confirm_password_et.text.toString().length < 5) {
            confirm_password_til.error = getString(R.string.error_invalid_password_length)
            confirm_password_et.requestFocus()
            isValid = false
        } else if (confirm_password_et.text.toString() != password_et.text.toString()) {
            confirm_password_til.error =
                getString(R.string.error_password_confirm_password_does_not_match)
            confirm_password_et.requestFocus()
            isValid = false
        }

        if (password_et.text.isNullOrEmpty()) {
            password_til.error = getString(R.string.error_invalid_password)
            password_et.requestFocus()
            isValid = false
        } else if (password_et.text.toString().length < 5) {
            password_til.error = getString(R.string.error_invalid_password_length)
            password_et.requestFocus()
            isValid = false
        }

        if (login_id_et.text.isNullOrEmpty()) {
            login_id_til.error = getString(R.string.error_invalid_login_id)
            login_id_et.requestFocus()
            isValid = false
        } else if (login_id_et.text.toString().length < 5) {
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
        } else if (city_et.text.toString().length < 3) {
            city_til.error = getString(R.string.error_invalid_city_length)
            city_et.requestFocus()
            isValid = false
        }

        if (signUpRequestModel?.stateID == 0) {
            Toast.makeText(requireActivity(), "Please select a valid State", Toast.LENGTH_LONG)
                .show()
            isValid = false
        }

        if (party_name_et.text.isNullOrEmpty()) {
            party_name_til.error = getString(R.string.error_invalid_party_name)
            party_name_et.requestFocus()
            isValid = false
        } else if (party_name_et.text.toString().length < 5) {
            party_name_til.error = getString(R.string.error_invalid_party_name_length)
            party_name_et.requestFocus()
            isValid = false
        }

        return isValid
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
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
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

    protected fun showProgress() {
        dialog?.show()
    }

    protected fun dismissProgress() {
        dialog?.dismiss()
    }

    protected fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(
            requireActivity(),
            AppDatabase::class.java, Constants.DB_NAME
        ).build()
    }

/*    override fun onResume() {
        CommonFunctions().updateNetworkImage(
            requireActivity(), network_type
        );
        super.onResume()
    }*/
}