package com.marvsystems.fotosoftapp.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.ui.main.listeners.LabClickListener
import com.marvsystems.fotosoftapp.utils.AppUtil

class LabsAdapter(
    private val context: Context,
    private val labs: List<Lab>,
    private val labClickListener: LabClickListener
) :
    RecyclerView.Adapter<LabsAdapter.LabsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabsViewHolder {
        return LabsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_labs,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LabsViewHolder, position: Int) {
        holder.llLabItem.setOnClickListener {
            labClickListener.onClick(labs[position])
        }

        holder.llLabItem.setOnLongClickListener { labClickListener.onLongClick(labs[position]) }

        holder.tvLabName.text = labs[position].compName
        holder.tvLabAddress.text =
            String.format(
                "%s, %s",
                labs[position].compCity,
                labs[position].compState
            )
        holder.tvLabEmail.text = labs[position].compEmail

//      val phone = String.format(
//            "%s, %s, %s",
//            labs[position].contactPersonMobile,
//            labs[position].compPhone,
//            labs[position].contactPersonInternalMobile
//        )
//        holder.tvLabPhone.text = phone

        var phoneNo  = ""

        if (labs[position].contactPersonMobile != null && labs[position].contactPersonMobile != ""){
            phoneNo = String.format("%s", labs[position].contactPersonMobile)
        }

        if (labs[position].compPhone != null && labs[position].compPhone != ""){
            if (phoneNo.length == 0)    phoneNo = String.format("%s", labs[position].compPhone)
            else                        phoneNo = String.format("%s, %s", phoneNo, labs[position].compPhone)
        }

        if (labs[position].contactPersonInternalMobile != null && labs[position].contactPersonInternalMobile != ""){
            if (phoneNo.length == 0)    phoneNo = String.format("%s", labs[position].contactPersonInternalMobile)
            else                        phoneNo = String.format("%s, %s", phoneNo, labs[position].contactPersonInternalMobile)
        }

        holder.tvLabPhone.text = phoneNo
        holder.ivCompanyLogo.setImageBitmap(AppUtil.convertBase64ToBitmap(labs[position].compLogoImage!!))
    }

    override fun getItemCount(): Int {
        return labs.size
    }

    class LabsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llLabItem: LinearLayout = itemView.findViewById(R.id.ll_lab_item)
        var tvLabName: TextView = itemView.findViewById(R.id.tv_lab_name)
        var tvLabAddress: TextView = itemView.findViewById(R.id.tv_lab_address)
        var tvLabEmail: TextView = itemView.findViewById(R.id.tv_lab_email)
        var tvLabPhone: TextView = itemView.findViewById(R.id.tv_lab_phone)
        var ivCompanyLogo: ImageView = itemView.findViewById(R.id.iv_lab_icon)
    }

}