package com.marvsystems.fotosoftapp.ui.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.database.Order
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersAdapter(private val context: Context, private val orders: List<Order>) :
    RecyclerView.Adapter<MyOrdersAdapter.LabsViewHolder>() {

    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val serverSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabsViewHolder {
        return LabsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_orders,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LabsViewHolder, position: Int) {
        val orderDate = serverSdf.parse(orders[position].orderDate!!)
        val date = sdf.format(orderDate!!)

        holder.orderDate.text = date
        holder.orderNumber.text = orders[position].orderNo
        holder.productName.text = orders[position].products
        holder.paper.text = orders[position].paper
        holder.size.text = orders[position].photoSize
        holder.jobStatus.text = context.getString(R.string.not_available)
        holder.labJobNo.text = context.getString(R.string.not_available)
        "${orders[position].noOfFilesReceived}/${orders[position].noOfFilesSelected}".also {
            holder.files.text = it
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class LabsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderDate: TextView = itemView.findViewById(R.id.tv_order_date)
        var productName: TextView = itemView.findViewById(R.id.tv_product_name)
        var paper: TextView = itemView.findViewById(R.id.tv_paper)
        var labJobNo: TextView = itemView.findViewById(R.id.tv_lab_job_no)
        var orderNumber: TextView = itemView.findViewById(R.id.tv_order_number)
        var size: TextView = itemView.findViewById(R.id.tv_size)
        var files: TextView = itemView.findViewById(R.id.tv_files)
        var jobStatus: TextView = itemView.findViewById(R.id.tv_job_status)
    }
}