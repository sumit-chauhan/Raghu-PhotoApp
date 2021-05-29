package com.marvsystems.fotosoftapp.ui.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.ImageUploadStatus
import com.marvsystems.fotosoftapp.ui.dashboard.RetryListener
import com.squareup.picasso.Picasso
import java.io.File

class OrderImagesAdapter(
    private val context: Context,
    private val images: ArrayList<OrderImages>,
    private val retryListener: RetryListener
) :
    RecyclerView.Adapter<OrderImagesAdapter.ImagesViewHolder>() {

    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.iv_image)
        val imageName: TextView = itemView.findViewById(R.id.tv_image_name)
        val status: TextView = itemView.findViewById(R.id.tv_image_status)
        val retry: TextView = itemView.findViewById(R.id.tv_retry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_order_images, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {

        Picasso.get()
            .load(File(images[position].imagePath!!))
            .fit()
            .centerInside()
            .into(holder.image)

        holder.imageName.text = String.format(
            "%s. %s",
            position + 1,
            images[position].imageName
        )
        holder.status.text = images[position].status
        if (images[position].status == ImageUploadStatus.FAILED.toString()) {
            holder.retry.visibility = View.VISIBLE
        } else {
            holder.retry.visibility = View.GONE
        }

        holder.retry.setOnClickListener {
            retryListener.onRetry(images[position])
        }

        holder.status.setTextColor(getColor(images[position].status))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun getColor(status: String): Int {
        when (ImageUploadStatus.valueOf(status)) {
            ImageUploadStatus.PENDING -> {
                return context.getColor(R.color.orange)
            }
            ImageUploadStatus.UPLOADING -> {
                return context.getColor(android.R.color.holo_blue_dark)
            }
            ImageUploadStatus.UPLOADED -> {
                return context.getColor(android.R.color.holo_green_dark)
            }
            ImageUploadStatus.FAILED -> {
                return context.getColor(android.R.color.holo_red_dark)
            }
        }
        return context.getColor(R.color.orange)
    }
}