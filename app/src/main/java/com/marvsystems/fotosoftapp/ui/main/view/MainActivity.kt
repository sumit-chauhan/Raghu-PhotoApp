package com.marvsystems.fotosoftapp.ui.main.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.api.ApiServiceImpl
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.ui.auth.view.LoginActivity
import com.marvsystems.fotosoftapp.ui.base.BaseActivity
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.ui.main.adapter.LabsAdapter
import com.marvsystems.fotosoftapp.ui.main.listeners.LabClickListener
import com.marvsystems.fotosoftapp.ui.main.listeners.SubmitLabClickListener
import com.marvsystems.fotosoftapp.ui.main.viewmodel.MainViewModel
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.Status
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), LabClickListener {

    var adapter: LabsAdapter? = null
    var labs = ArrayList<Lab>()
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        setupObserver()
        labs_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = LabsAdapter(this, labs, this)
        labs_recyclerview.adapter = adapter

        btn_add_labs.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this)){
                addLabs()
            }else{
                showToast("Please check your internet connectivity")
            }
        }
        loadLabs()
        if (AppUtil.isNetworkAvailable(this)){
            mainViewModel.fetchAllLabsForUpdate()
        }
    }

    private fun loadLabs() {
        mainViewModel.fetchAllLabs()
    }

    private fun addLabs() {
        val customDialog = AddLabsDialog(this, object : SubmitLabClickListener {
            override fun onSubmitClick(licence: String) {
                if (licence.isNotBlank()) {
                    Log.e("Licence", licence)
                    mainViewModel.fetchLabDetails(licence)
                }
            }

        })
        customDialog.show()
    }


    private fun setupObserver() {
        observeLabAPICall()
        observeLabsFromDB()
    }

    private fun observeLabsFromDB() {
        mainViewModel.labsFromDB().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { result ->
                        Log.e("LabsFromDB", Gson().toJson(result))
                        if (result.isNotEmpty()) {
                            tv_no_labs_found.visibility = View.GONE
                            labs_recyclerview.visibility = View.VISIBLE
                            labs.clear()
                            labs.addAll(result)
                            adapter?.notifyDataSetChanged()
                        } else {
                            labs.clear()
                            adapter?.notifyDataSetChanged()
                            addLabs()
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

    private fun observeLabAPICall() {
        mainViewModel.getLab().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgress()
                    it.data?.let { lab ->
                        Log.e("Get Lab Status.SUCCESS", Gson().toJson(lab))

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

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(MainViewModel::class.java)
    }

    override fun onClick(lab: Lab) {
        startActivity(Intent(this, LoginActivity::class.java).putExtra("DATA", lab))
    }

    override fun onLongClick(lab: Lab): Boolean {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Delete Lab ?")
            .setMessage("Are you sure want to delete " + lab.compName + " ?")
            .setPositiveButton("Yes") { dialog, p1 ->
                dialog?.dismiss()
                mainViewModel.deleteLab(lab)
            }
            .setNegativeButton(
                "No"
            ) { dialog, p1 -> dialog?.dismiss() }.create()
        alertDialog.show()
        return true
    }


}