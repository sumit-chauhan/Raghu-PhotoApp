package com.marvsystems.fotosoftapp.ui.album.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.model.MasterDataModel
import com.marvsystems.fotosoftapp.data.model.Product
import com.marvsystems.fotosoftapp.data.repository.DataRepository
import com.marvsystems.fotosoftapp.utils.Resource
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SelectProductViewModel(
    private val dataRepository: DataRepository,
    private val appDatabase: AppDatabase,
) : ViewModel() {

    private val masterData = MutableLiveData<Resource<MasterDataModel>>()
    private val compositeDisposable = CompositeDisposable()
    private val productList = MutableLiveData<Resource<List<Product>>>()


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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
