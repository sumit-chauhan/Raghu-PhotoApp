package com.marvsystems.fotosoftapp.ui.album.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.database.mapper.*
import com.marvsystems.fotosoftapp.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CreateAlbumViewModel(
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val productCategoryList = MutableLiveData<Resource<List<Category>>>()
    private val paperList = MutableLiveData<Resource<List<Paper>>>()
    private val paperSizeList = MutableLiveData<Resource<List<PaperSize>>>()
    private val albumBagList = MutableLiveData<Resource<List<AlbumBag>>>()
    private val albumBoxList = MutableLiveData<Resource<List<AlbumBox>>>()
    private val coverList = MutableLiveData<Resource<List<Cover>>>()
    private val coatingList = MutableLiveData<Resource<List<Coating>>>()


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun fetchAllProductCategories(productId: String) {
        compositeDisposable.add(appDatabase.masterDataDao().getProductCategories(productId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ categories -> productCategoryList.postValue(Resource.success(categories)) }
            ) {
                it.printStackTrace()
                productCategoryList.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun fetchAllPaperList(categoryId: String) {
        compositeDisposable.add(appDatabase.masterDataDao().getPaperList(categoryId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ papers ->
                paperList.postValue(Resource.success(papers))
            }
            ) {
                it.printStackTrace()
                paperList.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun fetchAllPaperSizeList(categoryId: String) {
        compositeDisposable.add(appDatabase.masterDataDao().getPaperSizeList(categoryId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ papers -> paperSizeList.postValue(Resource.success(papers)) }
            ) {
                it.printStackTrace()
                paperSizeList.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun fetchAllAlbumBagList(categoryId: String) {
        compositeDisposable.add(appDatabase.masterDataDao().getAlbumBagList(categoryId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ bags -> albumBagList.postValue(Resource.success(bags)) }
            ) {
                it.printStackTrace()
                albumBagList.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun fetchAllAlbumBoxList(categoryId: String) {
        compositeDisposable.add(appDatabase.masterDataDao().getAlbumBoxList(categoryId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ bags -> albumBoxList.postValue(Resource.success(bags)) }
            ) {
                it.printStackTrace()
                albumBoxList.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun fetchAllCoverList(categoryId: String) {
        compositeDisposable.add(appDatabase.masterDataDao().getCoverList(categoryId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ bags -> coverList.postValue(Resource.success(bags)) }
            ) {
                it.printStackTrace()
                coverList.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun fetchAllCoatingList(categoryId: String) {
        compositeDisposable.add(appDatabase.masterDataDao().getCoatingList(categoryId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ bags -> coatingList.postValue(Resource.success(bags)) }
            ) {
                it.printStackTrace()
                coatingList.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun getProductCategories(): LiveData<Resource<List<Category>>> {
        return productCategoryList
    }

    fun getPaperList(): LiveData<Resource<List<Paper>>> {
        return paperList
    }

    fun getPaperSizeList(): LiveData<Resource<List<PaperSize>>> {
        return paperSizeList
    }

    fun getBagList(): LiveData<Resource<List<AlbumBag>>> {
        return albumBagList
    }

    fun getBoxList(): LiveData<Resource<List<AlbumBox>>> {
        return albumBoxList
    }

    fun getCoverList(): LiveData<Resource<List<Cover>>> {
        return coverList
    }

    fun getCoatingList(): LiveData<Resource<List<Coating>>> {
        return coatingList
    }
}