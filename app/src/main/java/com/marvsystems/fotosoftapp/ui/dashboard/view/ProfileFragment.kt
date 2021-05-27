package com.marvsystems.fotosoftapp.ui.dashboard.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.ui.dashboard.viewmodel.DashboardViewModel
import com.marvsystems.fotosoftapp.utils.AppUtil
import kotlinx.android.synthetic.main.layout_lab_info_header.*

class ProfileFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

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
        setLabInfo()
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

        var phoneNo  = ""

        if (dashboardViewModel.lab?.contactPersonMobile != null && dashboardViewModel.lab?.contactPersonMobile != ""){
            phoneNo = String.format("%s", dashboardViewModel.lab?.contactPersonMobile)
        }

        if (dashboardViewModel.lab?.compPhone != null && dashboardViewModel.lab?.compPhone != ""){
            if (phoneNo.length == 0)    phoneNo = String.format("%s", dashboardViewModel.lab?.compPhone)
            else                        phoneNo = String.format("%s, %s", phoneNo, dashboardViewModel.lab?.compPhone)
        }

        if (dashboardViewModel.lab?.contactPersonInternalMobile != null && dashboardViewModel.lab?.contactPersonInternalMobile != ""){
            if (phoneNo.length == 0)    phoneNo = String.format("%s", dashboardViewModel.lab?.contactPersonInternalMobile)
            else                        phoneNo = String.format("%s, %s", phoneNo, dashboardViewModel.lab?.contactPersonInternalMobile)
        }

        tv_lab_mobile_number.text = phoneNo
        iv_lab_icon.setImageBitmap(AppUtil.convertBase64ToBitmap(dashboardViewModel.lab?.compLogoImage!!))
    }
}