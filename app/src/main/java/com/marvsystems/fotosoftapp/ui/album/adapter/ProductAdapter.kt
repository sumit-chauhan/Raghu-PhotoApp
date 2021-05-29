package com.marvsystems.fotosoftapp.ui.album.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.model.Product
import com.marvsystems.fotosoftapp.ui.album.listeners.ProductClickListener

class ProductAdapter(
    private val context: Context,
    private val products: List<Product>,
    private val listener: ProductClickListener
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvProduct: CardView = itemView.findViewById(R.id.cv_product)
        var tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.tvProductName.text = products[position].description
        holder.cvProduct.setOnClickListener {
            listener.onProductClick(products[position])
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}