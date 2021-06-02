package com.marvsystems.fotosoftapp.ui.album.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.mapper.*
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.data.model.Product
import com.marvsystems.fotosoftapp.ui.album.viewmodel.CreateAlbumViewModel
import com.marvsystems.fotosoftapp.ui.base.BaseActivity
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.Constants.SELECT_BAG
import com.marvsystems.fotosoftapp.utils.Constants.SELECT_BOX
import com.marvsystems.fotosoftapp.utils.Constants.SELECT_COATING
import com.marvsystems.fotosoftapp.utils.Constants.SELECT_COVER
import com.marvsystems.fotosoftapp.utils.Constants.SELECT_PAPER
import com.marvsystems.fotosoftapp.utils.Constants.SELECT_SIZE
import com.marvsystems.fotosoftapp.utils.Status
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_lab_info_header.*

class CreateAlbumActivity : BaseActivity() {

    private lateinit var createAlbumViewModel: CreateAlbumViewModel
    private var data: LoginModel? = null
    private var productCategoryList = ArrayList<Category>()
    private var paperList = ArrayList<Paper>()
    private var sizeList = ArrayList<PaperSize>()
    private var bagList = ArrayList<AlbumBag>()
    private var boxList = ArrayList<AlbumBox>()
    private var coverList = ArrayList<Cover>()
    private var coatingList = ArrayList<Coating>()

    private var productCategoryAdapter: ArrayAdapter<Category>? = null
    private var paperAdapter: ArrayAdapter<Paper>? = null
    private var sizeAdapter: ArrayAdapter<PaperSize>? = null
    private var bagAdapter: ArrayAdapter<AlbumBag>? = null
    private var boxAdapter: ArrayAdapter<AlbumBox>? = null
    private var coverAdapter: ArrayAdapter<Cover>? = null
    private var coatingAdapter: ArrayAdapter<Coating>? = null
    private var order: Order? = null
    private var selectedProduct: Product? = null
    private var lab: Lab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)
        data = intent.getSerializableExtra("DATA") as LoginModel?
        lab = intent.getSerializableExtra("LAB") as Lab?
        selectedProduct = intent.getSerializableExtra("PRODUCT") as Product?
        setLabInfo()
        getOrder()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        showBackOnToolbar()
        setupViewModel()
        observeData()
        setUpAdapter()
        setOnChangeListener()

        order?.productId = selectedProduct?.id
        order?.products = selectedProduct?.description
        tv_product_name.text = selectedProduct?.description
        createAlbumViewModel.fetchAllProductCategories(selectedProduct?.id!!)

        btn_next.setOnClickListener {
            if (isDataValid()) {
                order?.albumName = album_name_et.text.toString()
                order?.remarks = remarks_et.text.toString()
                val intent = Intent(this, AttachImagesActivity::class.java)
                intent.putExtra("ORDER", order!!)
                intent.putExtra("LAB", lab!!)
                intent.putExtra("LOGIN", data!!)
                startActivity(intent)
            }
        }
    }

    private fun isDataValid(): Boolean {
        when {
            order?.productCategoryId == "0" -> {
                showToast("Select a valid product category")
                return false
            }
            order?.paper.isNullOrEmpty()-> {
                showToast("Select a valid paper")
                return false
            }
            order?.photoSize.isNullOrEmpty()-> {
                showToast("Select a valid size")
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun setOnChangeListener() {
        productCategoryChangeListener()
        paperChangeListener()
        sizeChangeListener()
        bagChangeListener()
        boxChangeListener()
        coverChangeListener()
        coatingChangeListener()
        coatingChangeListener()
    }


    private fun productCategoryChangeListener() {
        spinner_product_cat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                order?.productCategoryId = productCategoryList[position].productCategoryId
                order?.productCategory = productCategoryList[position].description

                createAlbumViewModel.fetchAllPaperList(productCategoryList[position].productCategoryId)
                createAlbumViewModel.fetchAllPaperSizeList(productCategoryList[position].productCategoryId)
                createAlbumViewModel.fetchAllAlbumBagList(productCategoryList[position].productCategoryId)
                createAlbumViewModel.fetchAllAlbumBoxList(productCategoryList[position].productCategoryId)
                createAlbumViewModel.fetchAllCoverList(productCategoryList[position].productCategoryId)
                createAlbumViewModel.fetchAllCoatingList(productCategoryList[position].productCategoryId)

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun paperChangeListener() {
        spinner_paper.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                try {
                    if (position!=0){
                        order?.paper = paperList[position].description
                    }else{
                        order?.paper=""
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun sizeChangeListener() {
        spinner_size.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    if (position!=0){
                        order?.photoSize = sizeList[position].description
                    }else{
                        order?.photoSize=""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun bagChangeListener() {
        spinner_bag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                try {
                    if (position!=0){
                        order?.albumBag = bagList[position].description
                    }else{
                        order?.albumBag=""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun boxChangeListener() {
        spinner_box.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                try {
                    if (position!=0){
                        order?.albumBox = boxList[position].description
                    }else{
                        order?.albumBox=""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun coverChangeListener() {
        spinner_box.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    if (position!=0){
                        order?.cover = coverList[position].description
                    }else{
                        order?.cover=""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun coatingChangeListener() {
        spinner_lamination.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    if (position!=0){
                        order?.coating = coatingList[position].description
                    }else{
                        order?.coating=""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun getOrder() {
        order = Order()
        order?.productId = selectedProduct?.id
        order?.products = selectedProduct?.description
        order?.partyId = data?.userDetail?.partyId
        order?.compId = lab?.compId
        order?.cloudUploadBy = data?.userDetail?.userName
    }

    private fun setUpAdapter() {
        productCategoryAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, productCategoryList)
        paperAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paperList)
        sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sizeList)
        bagAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bagList)
        boxAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, boxList)
        coverAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, coverList)
        coatingAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, coatingList)

        spinner_product_cat.adapter = productCategoryAdapter
        spinner_paper.adapter = paperAdapter
        spinner_size.adapter = sizeAdapter
        spinner_bag.adapter = bagAdapter
        spinner_box.adapter = boxAdapter
        spinner_cover.adapter = coverAdapter
        spinner_lamination.adapter = coatingAdapter
    }

    private fun observeData() {
        observeProductCategories()
        observePaperSizeList()
        observeBagList()
        observeBoxList()
        observeCoverList()
        observeCoatingList()
        observePaperList()
    }

    private fun observeProductCategories() {
        createAlbumViewModel.getProductCategories().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        productCategoryList.clear()
                        productCategoryList.add(Category("0", "Select Category"))
                        productCategoryList.addAll(result)
                        productCategoryAdapter?.notifyDataSetChanged()
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

    private fun observePaperSizeList() {
        createAlbumViewModel.getPaperSizeList().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        sizeList.clear()
                        sizeList.add(PaperSize("0", SELECT_SIZE))
                        sizeList.addAll(result)
                        sizeAdapter?.notifyDataSetChanged()
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

    private fun observeBagList() {
        createAlbumViewModel.getBagList().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        bagList.clear()
                        bagList.add(AlbumBag("0", SELECT_BAG))
                        bagList.addAll(result)
                        bagAdapter?.notifyDataSetChanged()
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

    private fun observeBoxList() {
        createAlbumViewModel.getBoxList().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        boxList.clear()
                        boxList.add(AlbumBox("0", SELECT_BOX))
                        boxList.addAll(result)
                        boxAdapter?.notifyDataSetChanged()
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

    private fun observeCoverList() {
        createAlbumViewModel.getCoverList().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        coverList.clear()
                        coverList.add(Cover("0", SELECT_COVER))
                        coverList.addAll(result)
                        coverAdapter?.notifyDataSetChanged()
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

    private fun observeCoatingList() {
        createAlbumViewModel.getCoatingList().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        coatingList.clear()
                        coatingList.add(Coating("0", SELECT_COATING))
                        coatingList.addAll(result)
                        coatingAdapter?.notifyDataSetChanged()
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

    private fun observePaperList() {
        createAlbumViewModel.getPaperList().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("result", Gson().toJson(result))
                        paperList.clear()
                        paperList.add(Paper("0", SELECT_PAPER))
                        paperList.addAll(result)
                        paperAdapter?.notifyDataSetChanged()
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

    private fun setupViewModel() {
        createAlbumViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(CreateAlbumViewModel::class.java)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}