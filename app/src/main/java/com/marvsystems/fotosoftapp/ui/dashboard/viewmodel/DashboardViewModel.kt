package com.marvsystems.fotosoftapp.ui.dashboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.error.ANError
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.ImageUploadStatus
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.data.model.MasterDataModel
import com.marvsystems.fotosoftapp.data.model.OrderStatus
import com.marvsystems.fotosoftapp.data.repository.DataRepository
import com.marvsystems.fotosoftapp.utils.AppUtil
import com.marvsystems.fotosoftapp.utils.Resource
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.io.File

class DashboardViewModel(
    private val dataRepository: DataRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {
    var user: LoginModel? = null
    var lab: Lab? = null
    private val pastOrders = MutableLiveData<Resource<List<Order>>>()
    private val orderImages = MutableLiveData<Resource<List<OrderImages>>>()
    private val masterData = MutableLiveData<Resource<MasterDataModel>>()
    private val recentOrderLiveData = MutableLiveData<Resource<Order>>()
    private val compositeDisposable = CompositeDisposable()
    private val createdOrder = MutableLiveData<Resource<Order>>()
    private val imageIds = MutableLiveData<Resource<JSONArray>>()
    private val pendingImage = MutableLiveData<Resource<OrderImages>>()
    var apiCalling: Boolean = false
    var orderComplete: Boolean = false
    var orderPause: Boolean = false
    var recentOrder: Order? = null

    fun fetchMasterData(jwtToken: String, compId: Int) {
        masterData.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.getMasterData(jwtToken, compId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    uploadMasterData(data)
                    masterData.postValue(Resource.success(data))
                }, {
                    it.printStackTrace()
                    masterData.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun fetchPastOrders(
        jwtToken: String,
        compId: Int,
        dateFrom: String,
        dateTo: String,
        partyId: String
    ) {
        pastOrders.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.getPastOrders(jwtToken, compId, dateFrom, dateTo, partyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    pastOrders.postValue(Resource.success(data))
                }, {
                    it.printStackTrace()
                    pastOrders.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    private fun uploadMasterData(masterDataModel: MasterDataModel) {
        Completable.fromAction {
            clearMasterData()
            appDatabase.masterDataDao().insertAllProducts(masterDataModel.products)
            appDatabase.masterDataDao().insertAllProductCategory(masterDataModel.productCategories)
            appDatabase.masterDataDao()
                .insertAllProductCategories(masterDataModel.productCategoryLists)
            appDatabase.masterDataDao().insertAllAlbumBagList(masterDataModel.albumBagLists)
            appDatabase.masterDataDao().insertAllAlbumBagMast(masterDataModel.albumBagMasts)
            appDatabase.masterDataDao().insertAllAlbumBoxList(masterDataModel.albumBoxLists)
            appDatabase.masterDataDao().insertAllAlbumBoxMast(masterDataModel.albumBoxMasts)
            appDatabase.masterDataDao().insertAllCoatingList(masterDataModel.coatingLists)
            appDatabase.masterDataDao().insertAllCoatingMast(masterDataModel.coatingMasts)
            appDatabase.masterDataDao().insertAllCoverList(masterDataModel.coverLists)
            appDatabase.masterDataDao().insertAllCoverMast(masterDataModel.coverMasts)
            appDatabase.masterDataDao().insertAllPaperList(masterDataModel.paperLists)
            appDatabase.masterDataDao().insertAllPaperMast(masterDataModel.paperMasts)
            appDatabase.masterDataDao().insertAllPaperSizeList(masterDataModel.paperSizeLists)
            appDatabase.masterDataDao().insertAllPaperSizeMast(masterDataModel.paperSizeMasts)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.e("Completed", "Completed")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    private fun clearMasterData() {
        appDatabase.masterDataDao().deleteAllProducts()
        appDatabase.masterDataDao().deleteAllProductCategory()
        appDatabase.masterDataDao().deleteAllProductCategoryList()
        appDatabase.masterDataDao().deleteAllAlbumBagList()
        appDatabase.masterDataDao().deleteAllAlbumBagMast()
        appDatabase.masterDataDao().deleteAllAlbumBoxList()
        appDatabase.masterDataDao().deleteAllAlbumBoxMast()
        appDatabase.masterDataDao().deleteAllCoatingList()
        appDatabase.masterDataDao().deleteAllCoatingMast()
        appDatabase.masterDataDao().deleteAllCoverList()
        appDatabase.masterDataDao().deleteAllCoverMast()
        appDatabase.masterDataDao().deleteAllPaperList()
        appDatabase.masterDataDao().deleteAllPaperMast()
        appDatabase.masterDataDao().deleteAllPaperSizeList()
        appDatabase.masterDataDao().deleteAllPaperSizeMast()
    }

    fun getMasterData(): LiveData<Resource<MasterDataModel>> {
        return masterData
    }

    fun getPastOrders(): LiveData<Resource<List<Order>>> {
        return pastOrders
    }


    override fun onCleared() {
        super.onCleared()
        cancelRequest()
    }

    fun fetchRecentOrder() {
        recentOrderLiveData.postValue(Resource.loading(null))
        compositeDisposable.add(appDatabase.orderDao().getRecentOrder(lab?.compId!!)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ order ->
                recentOrderLiveData.postValue(Resource.success(order))
            }
            ) {
                Log.e("Error", "Order")
                it.printStackTrace()
                recentOrderLiveData.postValue(Resource.error("Something Went Wrong", null))
                clearSubscribers()
            })
    }

    fun fetchOrderImages(orderId: Int) {
        orderImages.postValue(Resource.loading(null))
        compositeDisposable.add(appDatabase.orderImageDao().getOrderImages(orderId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ orderImage ->
                run {
                    orderImages.postValue(Resource.success(orderImage))
                }

            }
            ) {
                it.printStackTrace()
                orderImages.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun getRecentOrder(): LiveData<Resource<Order>> {
        return recentOrderLiveData
    }


    fun getOrderImages(): LiveData<Resource<List<OrderImages>>> {
        return orderImages
    }

    fun uploadFile(orderImage: OrderImages) {
        val file = File(orderImage.imagePath!!)
        orderImage.status = ImageUploadStatus.UPLOADING.toString()
        updateOrderImageStatus(orderImage)
//        dataRepository.uploadImage(file, orderImage.id!!, user?.jwtToken!!)
        var imagePath =
            lab?.ftpfolderImages + "/" + recentOrder?.folderName + "/" + orderImage.imageFolderName + "/" + orderImage.imageName;
        apiCalling = true
        compositeDisposable.add(
            dataRepository.uploadImage(
                file,
                orderImage.id!!,
                lab?.httpAddressUpload!!,
                imagePath,
                orderImage.orderId!!,
                user?.jwtToken!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    apiCalling = false
                    Log.e("ImageUpload ->", Gson().toJson(it))
                    orderImage.status = ImageUploadStatus.UPLOADED.toString()
                    updateOrderImageStatus(orderImage)
                    getPendingImage(orderImage.localOrderId!!, orderImage.localImageId!!)
                }, {
                    onError(it as ANError)
                    apiCalling = false
                    orderImage.status = ImageUploadStatus.FAILED.toString()
                    updateOrderImageStatus(orderImage)
                    getPendingImage(orderImage.localOrderId!!, orderImage.localImageId!!)
                    it.printStackTrace()
                })
        )
    }

    private fun updateOrderImageStatus(orderImage: OrderImages) {
        Completable.fromAction {
            appDatabase.orderImageDao().updateOrderImage(orderImage)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onComplete() {
                    Log.e("Update Order Image", "Completed")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    private fun checkOrderStatus(orderId: Int?) {
        Completable.fromAction {
            appDatabase.orderDao().updateOrderStatus(orderId!!)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onComplete() {
                    Log.e("Check Order Status", "Completed")
                    orderComplete = true;
                    fetchRecentOrder()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        Completable.fromAction {
            appDatabase.orderDao().changeOrderStatus(orderId, status)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.e("Change Order Status", "Completed")
                    fetchRecentOrder()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    fun toggleOrderUploadStatus(orderId: Int, isPause: Boolean) {
        if (isPause) {
            apiCalling = false
        }
        Completable.fromAction {
            appDatabase.orderDao().toggleOrderUploadStatus(orderId, isPause)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    fetchRecentOrder()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    private fun updateCreatedOrderInfo(
        localOrderId: Int,
        orderId: Int,
        orderNo: String,
        folderName: String,
        status: String
    ) {
        Completable.fromAction {
            appDatabase.orderDao()
                .updateOrderInfo(localOrderId, orderId, orderNo, folderName, status)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.e("Created Order Info", "Completed")
                    fetchRecentOrder()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    fun markImageAsPause(orderId: Int) {
        Completable.fromAction {
            appDatabase.orderImageDao().markImagesPause(orderId)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.e("Mark Image Pause", "Completed")
                    fetchRecentOrder()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    fun markImagePending(orderId: Int) {
        Completable.fromAction {
            appDatabase.orderImageDao().markImagesPending(orderId)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.e("Mark Image Pending", "Completed")
                    fetchRecentOrder()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    fun getPendingImage(orderId: Int, previousId: Int) {
        compositeDisposable.add(appDatabase.orderImageDao().getPendingImage(orderId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ orderImage ->
                Log.e("Inside get Order", "Order")
                pendingImage.postValue(Resource.success(orderImage))
            }
            ) {
                pendingImage.postValue(Resource.error("No Pending Images", null))
                checkOrderStatus(orderId)
                Log.e("Error", "Order")
                it.printStackTrace()
            })
    }

    fun cancelRequest() {
        if (recentOrder != null) {
            markImagePending(recentOrder?.localOrderId?.toInt()!!)
        }
        compositeDisposable.clear()
    }

    fun createOrder(order: Order) {
        apiCalling = true
        createdOrder.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.createOrder(order, user?.jwtToken!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    apiCalling = false
                    updateCreatedOrderInfo(
                        order.localOrderId?.toInt()!!,
                        data.id!!,
                        data.orderNo!!,
                        data.folderName!!,
                        OrderStatus.PENDING_IMAGES.toString()
                    )
                    createdOrder.postValue(Resource.success(data))
                }, {
                    apiCalling = false
                    it.printStackTrace()
                    createdOrder.postValue(Resource.error("Something Went Wrong", order))
                })
        )
    }

    fun updateOrderImages(order: Order, images: List<OrderImages>) {
        apiCalling = true
        images.forEach {
            it.orderId = order.id
            it.orderNo = order.orderNo
            val folderName = it.imagePath?.substring(
                AppUtil.nthLastIndexOf(2, "/", it.imagePath!!),
                AppUtil.nthLastIndexOf(1, "/", it.imagePath!!)
            )
            it.imageFolderName = folderName?.replace("/", "") + "_" + it.paperSize
        }

        imageIds.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.updateOrderImages(images, user?.jwtToken!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    apiCalling = false
                    Log.e("Response", Gson().toJson(data))
                    updateImagesId(order.id!!, order.localOrderId, images, data)
                    imageIds.postValue(Resource.success(data))
                }, {
                    apiCalling = false
                    it.printStackTrace()
                    toggleOrderUploadStatus(
                        order.localOrderId?.toInt()!!,
                        true
                    )
                    imageIds.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getCreatedOrder(): LiveData<Resource<Order>> {
        return createdOrder
    }

    fun getOrderImagesIds(): LiveData<Resource<JSONArray>> {
        return imageIds
    }

    private fun updateImagesId(
        orderId: Int,
        localOrderId: Long?,
        images: List<OrderImages>,
        data: JSONArray
    ) {
        for (index in images.indices) {
            images[index].orderId = orderId
            images[index].id = data.getInt(index)
        }
        Completable.fromAction {
            appDatabase.orderImageDao().update(images)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onComplete() {
                    updateOrderStatus(localOrderId?.toInt()!!, OrderStatus.PENDING.toString())
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    fun getPendingImage(): LiveData<Resource<OrderImages>> {
        return pendingImage
    }

    private fun onError(error: ANError) {
        if (error.errorCode != 0) {
            // received error from server
            // error.getErrorCode() - the error code from server
            // error.getErrorBody() - the error body from server
            // error.getErrorDetail() - just an error detail
            Log.d("ErrorCode", "onError errorCode : " + error.errorCode)
            Log.d("ErrorBody", "onError errorBody : " + error.errorBody)
            Log.d("ErrorDetails", "onError errorDetail : " + error.errorDetail)
            // get parsed error object (If ApiError is your class)
            // val apiError: ApiError = error.getErrorAsObject(ApiError::class.java)
        } else {
            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
            Log.d("ErrorDetails", "onError errorDetail : " + error.errorDetail)
        }
    }

    fun clearSubscribers() {
        recentOrderLiveData.postValue(Resource.error("No pending Orders", null))
        recentOrder = null
        apiCalling = false
        createdOrder.postValue(Resource.loading(null))
        orderImages.postValue(Resource.loading(null))
        imageIds.postValue(Resource.loading(null))
        imageIds.postValue(Resource.loading(null))
    }
}