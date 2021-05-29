package com.marvsystems.fotosoftapp.ui.album.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.api.ApiServiceImpl
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.data.model.MasterDataModel
import com.marvsystems.fotosoftapp.data.model.Product
import com.marvsystems.fotosoftapp.ui.album.adapter.ProductAdapter
import com.marvsystems.fotosoftapp.ui.album.listeners.ProductClickListener
import com.marvsystems.fotosoftapp.ui.album.viewmodel.SelectProductViewModel
import com.marvsystems.fotosoftapp.ui.base.BaseActivity
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.Status
import kotlinx.android.synthetic.main.activity_select_product.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_lab_info_header.*

class SelectProductActivity : BaseActivity() , ProductClickListener {

    private var lab: Lab? = null
    private var data: LoginModel? = null
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<Product>()
    private var masterDataModel: MasterDataModel? = null
    private lateinit var selectProductViewModel: SelectProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_product)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        showBackOnToolbar()
        lab = intent.getSerializableExtra("LAB") as Lab?
        data = intent.getSerializableExtra("DATA") as LoginModel?
        setupViewModel()
        productAdapter = ProductAdapter(this,productList,this)
        setLinearLayoutManager(products_recyclerview)
        products_recyclerview.adapter=productAdapter
        observeMasterDataAPICall()
        selectProductViewModel.fetchMasterData(data?.jwtToken!!, data?.compId!!)
        setLabInfo()

    }

    private fun observeMasterDataAPICall() {
        selectProductViewModel.getMasterData().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        bindMasterData(result)
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

    private fun bindMasterData(masterDataModel: MasterDataModel) {
        this.masterDataModel = masterDataModel
        productList.clear()
        productList.addAll(masterDataModel.products)
        productAdapter?.notifyDataSetChanged()
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
        selectProductViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(SelectProductViewModel::class.java)
    }

    override fun onProductClick(product: Product) {
        val intent = Intent(this, CreateAlbumActivity::class.java).putExtra("DATA", data)
        intent.putExtra("LAB", lab)
        intent.putExtra("PRODUCT", product)
        startActivity(intent)
    }
}