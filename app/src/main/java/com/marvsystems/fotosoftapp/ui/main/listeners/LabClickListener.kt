package com.marvsystems.fotosoftapp.ui.main.listeners

import com.marvsystems.fotosoftapp.data.database.Lab

interface LabClickListener {

    fun onClick(lab:Lab)

    fun onLongClick(lab:Lab) : Boolean
}