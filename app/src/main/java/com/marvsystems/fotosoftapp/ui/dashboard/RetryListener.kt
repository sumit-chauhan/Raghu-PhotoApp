package com.marvsystems.fotosoftapp.ui.dashboard

import com.marvsystems.fotosoftapp.data.database.OrderImages

interface RetryListener {
    fun onRetry(orderImages: OrderImages)
}