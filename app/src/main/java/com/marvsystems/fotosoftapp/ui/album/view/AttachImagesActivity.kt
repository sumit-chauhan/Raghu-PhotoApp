package com.marvsystems.fotosoftapp.ui.album.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.BuildConfig
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.api.ApiServiceImpl
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.ui.album.viewmodel.AttachImagesViewModel
import com.marvsystems.fotosoftapp.ui.base.BaseActivity
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.ui.dashboard.DeleteListener
import com.marvsystems.fotosoftapp.ui.dashboard.adapter.ImagesAdapter
import com.marvsystems.fotosoftapp.ui.dashboard.view.DashboardActivity
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.CommonFunctions
import com.marvsystems.fotosoftapp.utils.DateTimeFormat
import com.marvsystems.fotosoftapp.utils.Status
import gun0912.tedimagepicker.builder.TedRxImagePicker
import kotlinx.android.synthetic.main.activity_attach_images.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_lab_info_header.*
import java.io.File


class AttachImagesActivity : BaseActivity(), DeleteListener {

    private var images: ArrayList<Uri>? = null
    private var imagesAdapter: ImagesAdapter? = null
    private var order: Order? = null
    private var lab: Lab? = null
    private var orderImages: ArrayList<OrderImages>? = null
    private var viewModel: AttachImagesViewModel? = null
    private var loggedInUser: LoginModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attach_images)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        showBackOnToolbar()
        setupViewModel()
        order = intent.getSerializableExtra("ORDER") as Order?
        lab = intent.getSerializableExtra("LAB") as Lab?
        loggedInUser = intent.getSerializableExtra("LOGIN") as LoginModel?
        Log.e("Data", Gson().toJson(order))
        setLabInfo()

        title = order?.albumName

        images = ArrayList()
        orderImages = ArrayList()

        imagesAdapter = ImagesAdapter(this, images!!, this)
        images_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        images_recyclerview.adapter = imagesAdapter

        btn_attach_images.setOnClickListener {
            TedRxImagePicker.with(this)
                .startMultiImage()
                .subscribe(this::showMultiImage, Throwable::printStackTrace)
        }

        btn_clear_images.setOnClickListener {
            images?.clear()
            orderImages?.clear()
            imagesAdapter?.notifyDataSetChanged()
        }

        observeOrderSave()
        observeImageSave()

        btn_create_album?.setOnClickListener {
            if(totalImageSize() > 0)
                createOrder()
            else
                showToast("Please select atleast one picture.")
        }
    }

    private fun observeOrderSave() {

        viewModel?.localOrder()?.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("Success", "Success")
                    dismissProgress()
                    it.data?.let { order ->
                        this.order = order
                        Log.e("result", Gson().toJson(order))
                        showToast("Album saved successfully")
                        viewModel?.insertImages(order, orderImages!!)
                    }
                }
                Status.LOADING -> {
                    showProgress()
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

    private fun observeImageSave(){
        viewModel?.localImages()?.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("Success", "Success")
                    dismissProgress()
                    it.data?.let { images ->
                       // showToast("Images Saved Successfully")
                        navigateToDashboard()
                    }
                }
                Status.LOADING -> {
                    showProgress()
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

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("DATA", loggedInUser)
        intent.putExtra("LAB", lab)
        startActivity(intent)
        finishAffinity()
    }

    private fun showMultiImage(uriList: List<Uri>) {
        Log.e("Size", "" + uriList.size)
        addOrderImages(uriList)
        images?.addAll(uriList)
        sortList(orderImages!!)
        imagesAdapter?.notifyDataSetChanged()
    }

    private fun addOrderImages(uriList: List<Uri>) {
        uriList.forEach { uri ->
            run {
                val orderImage = OrderImages()
                orderImage.imageSizeInKb = getImageSize(uri)
                orderImage.imageName = uri.lastPathSegment
                orderImage.imageNameLocal = uri.lastPathSegment
                orderImage.imagePath = uri.path
                orderImage.compId = order?.compId?.toLong()
                orderImage.partyId = order?.partyId?.toLong()
                orderImage.paper = order?.paper
                orderImage.paperSize = order?.photoSize
                orderImage.imageType = "Inner Image"
                orderImage.createdByUsrId = loggedInUser?.userDetail?.id?.toLong()
                orderImages?.add(orderImage)
            }
        }
    }

    override fun onDelete(position: Int) {
        imagesAdapter?.deleteImage(position)
        orderImages?.removeAt(position)
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

    private fun totalImageSize(): Long {
        var totalSize = 0L
        orderImages?.forEach {
            totalSize += it.imageSizeInKb!!
        }
        return totalSize
    }

    private fun getImageSize(uri: Uri): Long {
        val file = File(uri.path!!)
        return file.length() / 1024
    }

    private fun createOrder() {
        order?.cloudData = "Y"
        order?.orderDate = AppUtil.getCurrentDateTime(DateTimeFormat.YYYY_MM_DD_T_HH_MM_SS_SSS_Z)
        order?.noOfFilesSelected = orderImages?.size
        order?.totalImageSize = totalImageSize()
        order?.totalImageSizeUnCommpressed = order?.totalImageSize
        order?.uploadStartDt =
            AppUtil.getCurrentDateTime(DateTimeFormat.YYYY_MM_DD_T_HH_MM_SS_SSS_Z)
        order?.createdOnDt = AppUtil.getCurrentDateTime(DateTimeFormat.YYYY_MM_DD_T_HH_MM_SS_SSS_Z)
        order?.uploadEndDt = AppUtil.getCurrentDateTime(DateTimeFormat.YYYY_MM_DD_T_HH_MM_SS_SSS_Z)
        order?.appVersion = BuildConfig.VERSION_NAME
        order?.webUploadYn = "Y"
        order?.clientIp = AppUtil.getIPAddress(true)

        viewModel?.jwtToken = loggedInUser?.jwtToken
        viewModel?.insertOrder(order!!)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(AttachImagesViewModel::class.java)
    }

    private fun sortList(orderImages: ArrayList<OrderImages>) {
        orderImages.sortBy { it.imageName?.toLowerCase() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}