package com.marvsystems.fotosoftapp.ui.dashboard.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.ui.dashboard.DeleteListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.lang.Exception

class ImagesAdapter(
    private val context: Context,
    private val images: ArrayList<Uri>,
    private val deleteListener: DeleteListener
) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.iv_image)
        val delete: ImageView = itemView.findViewById(R.id.iv_delete)
        val imageName: TextView = itemView.findViewById(R.id.tv_image_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_images, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {

        Picasso.get()
            .load(File(images[position].path!!))
            .fit()
            .centerInside()
            .into(holder.image)

        holder.delete.setOnClickListener {
            deleteListener.onDelete(position)
        }
        holder.imageName.text=String.format(
            "%s. %s",
            position+1,
            images[position].lastPathSegment
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun deleteImage(position: Int) {
        images.removeAt(position)
        notifyDataSetChanged()
    }

}