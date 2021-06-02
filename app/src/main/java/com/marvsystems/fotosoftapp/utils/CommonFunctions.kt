package com.marvsystems.fotosoftapp.utils

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.marvsystems.fotosoftapp.R

class CommonFunctions {
    fun updateNetworkImage(context: Context, image: ImageView) {
        var type = AppSharedPreferences.getInstance(context).getString("ConnectionType");
        if (image != null && type != null) {
            if (type == "2G") {
                image.setImageResource(R.drawable.network_2g);
                image.setColorFilter(
                    ContextCompat.getColor(context, R.color.yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (type == "3G") {
                image.setImageResource(R.drawable.network_3g);
                image.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (type == "4G") {
                image.setImageResource(R.drawable.network_4g);
                image.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (type == "LTE") {
                image.setImageResource(R.drawable.network_lte);
                image.setColorFilter(
                    ContextCompat.getColor(context, R.color.green_dark),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else { // TODO - check others
                image.setImageResource(R.drawable.network_h);
                image.setColorFilter(
                    ContextCompat.getColor(context, R.color.gray),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            }
        }
    }
}