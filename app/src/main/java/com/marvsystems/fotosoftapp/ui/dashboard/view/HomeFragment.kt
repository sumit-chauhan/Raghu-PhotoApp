package com.marvsystems.fotosoftapp.ui.dashboard.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.ImageUploadStatus
import com.marvsystems.fotosoftapp.data.model.MasterDataModel
import com.marvsystems.fotosoftapp.data.model.OrderStatus
import com.marvsystems.fotosoftapp.data.model.Product
import com.marvsystems.fotosoftapp.ui.album.adapter.ProductAdapter
import com.marvsystems.fotosoftapp.ui.album.listeners.ProductClickListener
import com.marvsystems.fotosoftapp.ui.album.view.CreateAlbumActivity
import com.marvsystems.fotosoftapp.ui.album.view.SelectProductActivity
import com.marvsystems.fotosoftapp.ui.dashboard.RetryListener
import com.marvsystems.fotosoftapp.ui.dashboard.adapter.OrderImagesAdapter
import com.marvsystems.fotosoftapp.ui.dashboard.viewmodel.DashboardViewModel
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.CommonFunctions
import com.marvsystems.fotosoftapp.utils.NetworkUtils
import com.marvsystems.fotosoftapp.utils.Status
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_home_orders.*
import kotlinx.android.synthetic.main.item_orders.tv_files
import kotlinx.android.synthetic.main.item_orders.tv_order_date
import kotlinx.android.synthetic.main.item_orders.tv_order_number
import kotlinx.android.synthetic.main.item_orders.tv_paper
import kotlinx.android.synthetic.main.item_orders.tv_product_name
import kotlinx.android.synthetic.main.item_orders.tv_size
import kotlinx.android.synthetic.main.layout_lab_info_header.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), ProductClickListener, RetryListener {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var productAdapter: ProductAdapter? = null
    private var orderImagesAdapter: OrderImagesAdapter? = null
    private var productList = ArrayList<Product>()
    private var orderImages = ArrayList<OrderImages>()
    private var masterDataModel: MasterDataModel? = null
    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val serverSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private var dialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = Dialog(requireActivity())
        dialog?.setContentView(R.layout.layout_progress)
        dialog?.setCanceledOnTouchOutside(false)
        setLabInfo()

        productAdapter = ProductAdapter(requireActivity(), productList, this)
        orderImagesAdapter = OrderImagesAdapter(requireActivity(), orderImages, this)

        products_recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        products_recyclerview.adapter = productAdapter

        photos_recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        photos_recyclerview.adapter = orderImagesAdapter

        observeMasterDataAPICall()

        observeRecentOrder()

        observeOrderImages()

        observeOrderCreatedApiCall()

        observeOrderImageApiCall()

        observePendingImage()

        createOrderClickListener()

        cancelOrderClickListener()

        if (AppUtil.isNetworkAvailable(requireContext())) {
            dashboardViewModel.fetchMasterData(
                dashboardViewModel.user?.jwtToken!!,
                dashboardViewModel.lab?.compId!!
            )
        } else {
            dashboardViewModel.fetchRecentOrder()
        }

        Handler().postDelayed({
            try {
                handleNetworkChanges()
            } catch (exception: Exception) {
            }
        }, 2000)

    }

    private fun cancelOrderClickListener() {
        tv_cancel_upload.setOnClickListener {

            val alertBuilder = AlertDialog.Builder(requireActivity())
            alertBuilder.setTitle("Cancel Upload")
            alertBuilder.setMessage("Are you sure you want to cancel the upload?")
            alertBuilder.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                dashboardViewModel.updateOrderStatus(
                    dashboardViewModel.recentOrder?.localOrderId?.toInt()!!,
                    "COMPLETED"
                )
                dashboardViewModel.cancelRequest()

            }
            alertBuilder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()

            }
            alertBuilder.create().show()

        }
    }

    private fun createOrderClickListener() {
        btn_create_order.setOnClickListener {
            if (dashboardViewModel.recentOrder != null) {
                AppUtil.alertDialog(
                    requireActivity(),
                    "Create Order",
                    "An order already is in progress, Please create another one once it gets completed"
                )
            } else {
                val intent = Intent(requireActivity(), SelectProductActivity::class.java)
                intent.putExtra("DATA", dashboardViewModel.user)
                intent.putExtra("LAB", dashboardViewModel.lab)
                startActivity(intent)
            }
        }
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

    override fun onProductClick(product: Product) {
        val intent = Intent(requireActivity(), CreateAlbumActivity::class.java)
        intent.putExtra("DATA", dashboardViewModel.user)
        intent.putExtra("LAB", dashboardViewModel.lab)
        intent.putExtra("PRODUCT", product)
        startActivity(intent)
    }

    private fun updateOrderInfo(order: Order) {
        val orderDate = serverSdf.parse(order.orderDate!!)
        val date = sdf.format(orderDate!!)

        tv_order_date.text = date
        tv_order_number.text = order.orderNo
        tv_product_name.text = order.products
        tv_paper.text = order.paper
        tv_size.text = order.photoSize
        tv_files.text = order.noOfFilesSelected.toString()

        if (order.isPause) {
            tv_pause_upload.visibility = View.GONE
            tv_resume_upload.visibility = View.VISIBLE
        } else {
            tv_pause_upload.visibility = View.VISIBLE
            tv_resume_upload.visibility = View.GONE
        }

        tv_pause_upload.setOnClickListener {
            togglePause(true)
            dashboardViewModel.markImageAsPause(dashboardViewModel.recentOrder?.localOrderId?.toInt()!!)
        }

        tv_resume_upload.setOnClickListener {
            if (AppUtil.isNetworkAvailable(requireContext())) {
                togglePause(false)

            } else {
                showToast("Please check your internet connectivity")
            }
        }
    }

    private fun togglePause(isPause: Boolean) {
        dashboardViewModel.toggleOrderUploadStatus(
            dashboardViewModel.recentOrder?.localOrderId?.toInt()!!,
            isPause
        )
        if (isPause) {
            tv_pause_upload.visibility = View.GONE
            tv_resume_upload.visibility = View.VISIBLE
            dashboardViewModel.markImageAsPause(dashboardViewModel.recentOrder?.localOrderId?.toInt()!!)
            dashboardViewModel.cancelRequest()
        } else {
            tv_pause_upload.visibility = View.VISIBLE
            tv_resume_upload.visibility = View.GONE
            dashboardViewModel.cancelRequest()
            dashboardViewModel.apiCalling = false
            dashboardViewModel.markImagePending(dashboardViewModel.recentOrder?.localOrderId?.toInt()!!)
        }
    }


    private fun observeMasterDataAPICall() {
        dashboardViewModel.getMasterData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        bindMasterData(result)
                        dashboardViewModel.fetchRecentOrder()
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

    private fun observeRecentOrder() {

        dashboardViewModel.getRecentOrder().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    if (it.data != null && it.data.localOrderId?.toInt() != 0) {
                        Log.e("Recent Order", Gson().toJson(it.data))
                        dashboardViewModel.recentOrder = it.data
                        toggleRecentOrders(true)
                        updateOrderInfo(it.data)
                        dashboardViewModel.fetchOrderImages(it.data.localOrderId?.toInt()!!)
                        if (AppUtil.isNetworkAvailable(requireContext()) && !it.data.isPause) {
                            checkOrderStatus(it.data)
                        } else {
                            if (!it.data.isPause) {
                                pauseOrder(it.data.localOrderId?.toInt()!!)
                            }
                        }

                    } else {
                        toggleRecentOrders(false)
                    }
                }
                Status.LOADING -> {
                    //showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    dismissProgress()
                    toggleRecentOrders(false)
                }
            }
        }

    }

    private fun observePendingImage() {
        dashboardViewModel.getPendingImage().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("Success", "Success")
                    dismissProgress()
                    it.data?.let { orderImage ->
                        if (AppUtil.isNetworkAvailable(requireContext())) {
                            uploadImageOrFetchPendingImage(orderImage)
                        } else {
                            pauseOrder(orderImage.localOrderId!!)
                        }
                    }!!
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                    Log.e("PendingImage", "ERROR")
                    //Handle Error
                    dismissProgress()
                }
            }
        }
    }

    private fun uploadImageOrFetchPendingImage(orderImage: OrderImages) {
        if (orderImage.status != ImageUploadStatus.UPLOADING.toString() && !dashboardViewModel.apiCalling && orderImage.id != 0) {
            if (!dashboardViewModel.recentOrder?.isPause!!) {
                dashboardViewModel.uploadFile(orderImage)
            }

        } else {
            if (!dashboardViewModel.apiCalling) {
                dashboardViewModel.getPendingImage(orderImage.localOrderId!!, 0)
            }
        }
    }

    private fun pauseOrder(localOrderId: Int) {
        dashboardViewModel.toggleOrderUploadStatus(
            localOrderId,
            true
        )
        dashboardViewModel.markImageAsPause(dashboardViewModel.recentOrder?.localOrderId?.toInt()!!)
    }

    private fun observeOrderImageApiCall() {
        dashboardViewModel.getOrderImagesIds().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("Success", "Success")
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))

                        dashboardViewModel.recentOrder?.status = OrderStatus.PENDING.toString()
                        checkOrderStatus(dashboardViewModel.recentOrder!!)
                    }!!
                }
                Status.LOADING -> {
                    //showProgress()
                }
                Status.ERROR -> {
                    Log.e("ERROR", "ERROR")
                    //Handle Error
                    dismissProgress()
                    showToast(it.message)
                }
            }
        }
    }

    private fun observeOrderCreatedApiCall() {
        dashboardViewModel.getCreatedOrder().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("OrderCreated", "Success")
                    dismissProgress()
                    it.data?.let { order ->
                        Log.e("result", Gson().toJson(order))
                        dashboardViewModel.recentOrder?.status =
                            OrderStatus.PENDING_IMAGES.toString()
                        dashboardViewModel.recentOrder?.id = order.id
                        dashboardViewModel.recentOrder?.orderNo = order.orderNo
                        checkOrderStatus(dashboardViewModel.recentOrder!!)
                        showToast("Order Created Successfully")
                    }
                }
                Status.LOADING -> {
                    // showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    Log.e("OrderCreated", "ERROR")
                    dismissProgress()
                    dashboardViewModel.toggleOrderUploadStatus(
                        it.data?.localOrderId?.toInt()!!,
                        true
                    )
                }
            }
        }
    }

    private fun observeOrderImages() {
        dashboardViewModel.getOrderImages().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { images ->
                        Log.e("result", Gson().toJson(images))
                        orderImages.clear()
                        orderImages.addAll(images)
                        orderImagesAdapter?.notifyDataSetChanged()
                    }
                }
                Status.LOADING -> {
                    //showProgress()
                }
                Status.ERROR -> {
                    //Handle Error
                    dismissProgress()
                }
            }
        }
    }

    private fun checkOrderStatus(order: Order) {
        when (OrderStatus.valueOf(order.status)) {
            OrderStatus.LOCAL_CREATED -> {
                if (!dashboardViewModel.apiCalling) {
                    dashboardViewModel.createOrder(order)
                }
            }
            OrderStatus.PENDING_IMAGES -> {
                if (!dashboardViewModel.apiCalling) {
                    dashboardViewModel.updateOrderImages(order, orderImages)
                }
            }

            OrderStatus.PENDING -> {
                if (AppUtil.isNetworkAvailable(requireContext())) {
                    dashboardViewModel.getPendingImage(order.localOrderId?.toInt()!!, 0)
                }
            }
            OrderStatus.CANCELLED -> println("Order Cancelled")
            OrderStatus.COMPLETED -> showToast("Congrats! Your order successfully placed! We will update status shortly!")
            else -> {
                showToast("Invalid Order Status")
            }
        }
    }

    private fun toggleRecentOrders(toggle: Boolean) {
        ll_recent_orders.visibility = if (toggle) View.VISIBLE else View.GONE
        if (ll_recent_orders.visibility == View.GONE)
            showToast("Congrats! Your order successfully placed! We will update status shortly!")

        btn_create_order.visibility = if (toggle) View.VISIBLE else View.GONE
        products_recyclerview?.visibility = if (toggle) View.GONE else View.VISIBLE
        if (!toggle && productList.isNullOrEmpty()) {
            if (AppUtil.isNetworkAvailable(requireContext())) {
                dashboardViewModel.fetchMasterData(
                    dashboardViewModel.user?.jwtToken!!,
                    dashboardViewModel.lab?.compId!!
                )
            }
        }
    }

    private fun bindMasterData(masterDataModel: MasterDataModel) {
        this.masterDataModel = masterDataModel
        productList.clear()
        productList.addAll(masterDataModel.products)
        productAdapter?.notifyDataSetChanged()
    }


    /**
     * Method to show progress dialog
     */
    protected fun showProgress() {
        dialog?.show()
    }

    /**
     * Method to dismiss progress dialog
     */
    protected fun dismissProgress() {
        dialog?.dismiss()
    }

    /**
     * Method to show Toast
     * @param message Message to display on toast
     */
    protected fun showToast(message: String?) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        CommonFunctions().updateNetworkImage(
            requireActivity(), network_type
        );
        super.onResume()
    }

    override fun onRetry(orderImages: OrderImages) {
        dashboardViewModel.uploadFile(orderImages)
    }

    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(requireContext())
            .observe(viewLifecycleOwner, Observer { isConnected ->
                run {
                    if (dashboardViewModel.recentOrder != null) {
                        if (!isConnected) {
                            togglePause(true)
                        } else {
                            togglePause(false)
                        }
                    }
                }
            })
    }
}