package com.marvsystems.fotosoftapp.ui.album.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.repository.DataRepository
import com.marvsystems.fotosoftapp.utils.Resource
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers


class AttachImagesViewModel(
    private val dataRepository: DataRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val localOrder = MutableLiveData<Resource<Order>>()
    private val localImages = MutableLiveData<Resource<List<OrderImages>>>()
    private val compositeDisposable = CompositeDisposable()
    var jwtToken: String? = null



    fun insertOrder(order: Order?) {
        localOrder.postValue(Resource.loading(null))
        insertOrderInLocal(order)
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeWith(object : DisposableSingleObserver<Long>() {
                override fun onSuccess(t: Long) {
                    order?.localOrderId = t
                    localOrder.postValue(Resource.success(order))
                }

                override fun onError(e: Throwable) {
                    localOrder.postValue(Resource.error("Something Went Wrong", null))
                }

            })?.let {
                compositeDisposable.add(
                    it
                )
            }

    }

    private fun insertOrderInLocal(order: Order?): Single<Long>? {
        return Single.fromCallable { appDatabase.orderDao().insert(order!!) }
    }

    fun insertImages(order: Order, images: List<OrderImages>) {
        for (index in images.indices) {
            images[index].localOrderId = order.localOrderId?.toInt()
        }

        Completable.fromAction {
            appDatabase.orderImageDao().insert(images)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onComplete() {
                    Log.e("Images Saved in LocalDb", "Completed")
                    localImages.postValue(Resource.success(images))
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })

    }

    fun localOrder(): LiveData<Resource<Order>> {
        return localOrder
    }

    fun localImages(): LiveData<Resource<List<OrderImages>>> {
        return localImages
    }
}