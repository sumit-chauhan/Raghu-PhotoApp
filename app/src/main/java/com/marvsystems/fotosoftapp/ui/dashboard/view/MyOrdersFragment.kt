package com.marvsystems.fotosoftapp.ui.dashboard.view

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.ui.base.BaseFragment
import com.marvsystems.fotosoftapp.ui.dashboard.adapter.MyOrdersAdapter
import com.marvsystems.fotosoftapp.ui.dashboard.viewmodel.DashboardViewModel
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.CommonFunctions
import com.marvsystems.fotosoftapp.utils.Status
import kotlinx.android.synthetic.main.fragment_my_orders.*
import kotlinx.android.synthetic.main.layout_lab_info_header.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyOrdersFragment : BaseFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var myOrdersAdapter: MyOrdersAdapter
    private var dateFrom: String? = null
    private var dateTo: String? = null
    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val serverSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private var orders: ArrayList<Order>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
        return inflater.inflate(R.layout.fragment_my_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders = ArrayList()
        myOrdersAdapter = MyOrdersAdapter(requireActivity(), orders!!)
        my_orders_recyclerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        my_orders_recyclerview.adapter = myOrdersAdapter

        setLabInfo()
        observerOrders()
        setDefaultFromDate()
        setDefaultToDate()
        et_date_from.setOnClickListener {
            selectFromDate()
        }

        et_date_to.setOnClickListener {
            selectToDate()
        }

        fetchOrders()

        btn_search_orders.setOnClickListener {
            fetchOrders()
        }
    }

    private fun fetchOrders() {
        dashboardViewModel.fetchPastOrders(
            dashboardViewModel.user?.jwtToken!!,
            dashboardViewModel.user?.compId!!,
            dateFrom!!,
            dateTo!!,
            dashboardViewModel.user?.userDetail?.partyId?.toString()!!
        )
    }


    private fun observerOrders() {
        dashboardViewModel.getPastOrders().observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        bindOrders(result)
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
        })
    }

    private fun bindOrders(result: List<Order>) {
        orders?.clear()
        orders?.addAll(result)
        toggleOrderList(result.isNotEmpty())
        myOrdersAdapter.notifyDataSetChanged()
    }

    private fun toggleOrderList(isVisible: Boolean) {
        if (isVisible) {
            my_orders_recyclerview.visibility = View.VISIBLE
            tv_no_orders_label.visibility = View.GONE
        } else {
            my_orders_recyclerview.visibility = View.GONE
            tv_no_orders_label.visibility = View.VISIBLE
        }
    }


    private fun setDefaultFromDate() {
        val myCalendar: Calendar = Calendar.getInstance()
        myCalendar.add(Calendar.DATE, -30)
        myCalendar.set(Calendar.HOUR_OF_DAY, 23)
        myCalendar.set(Calendar.MINUTE, 59)
        myCalendar.set(Calendar.SECOND, 59)
        et_date_from.setText(sdf.format(myCalendar.time))
        dateFrom = serverSdf.format(myCalendar.time)
    }

    private fun setDefaultToDate() {
        val myCalendar: Calendar = Calendar.getInstance()
        myCalendar.set(Calendar.HOUR_OF_DAY, 23)
        myCalendar.set(Calendar.MINUTE, 59)
        myCalendar.set(Calendar.SECOND, 59)
        et_date_to.setText(sdf.format(myCalendar.time))
        dateTo = serverSdf.format(myCalendar.time)
    }

    private fun selectFromDate() {
        val myCalendar: Calendar = Calendar.getInstance()
        val date =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                myCalendar.set(Calendar.HOUR_OF_DAY, 23)
                myCalendar.set(Calendar.MINUTE, 59)
                myCalendar.set(Calendar.SECOND, 59)
                et_date_from.setText(sdf.format(myCalendar.time))
                dateFrom = serverSdf.format(myCalendar.time)
            }
        val dialog = DatePickerDialog(
            requireActivity(),
            date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.maxDate = Date().time
        dialog.show()
    }

    private fun selectToDate() {
        val myCalendar: Calendar = Calendar.getInstance()
        val date =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                myCalendar.set(Calendar.HOUR_OF_DAY, 23)
                myCalendar.set(Calendar.MINUTE, 59)
                myCalendar.set(Calendar.SECOND, 59)
                et_date_from.setText(sdf.format(myCalendar.time))
                dateTo = serverSdf.format(myCalendar.time)
            }
        val dialog = DatePickerDialog(
            requireActivity(),
            date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.maxDate = Date().time
        dialog.show()

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

    override fun onResume() {
        CommonFunctions().updateNetworkImage(
            requireActivity(), network_type
        );
        super.onResume()
    }
}