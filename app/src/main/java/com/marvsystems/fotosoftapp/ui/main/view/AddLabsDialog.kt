package com.marvsystems.fotosoftapp.ui.main.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.ui.main.listeners.SubmitLabClickListener
import kotlinx.android.synthetic.main.dialog_add_labs.*


class AddLabsDialog(var activity: Activity, var listener: SubmitLabClickListener) :
    Dialog(activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_labs)
        licence_et.requestFocus()
        btn_submit.setOnClickListener {
            dismiss()
            listener.onSubmitClick(licence_et.text.toString())
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

    }

}