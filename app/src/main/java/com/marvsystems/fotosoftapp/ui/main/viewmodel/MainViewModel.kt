package com.marvsystems.fotosoftapp.ui.main.viewmodel

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.error.ANError
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.data.model.LabModel
import com.marvsystems.fotosoftapp.data.model.LabResponseModel
import com.marvsystems.fotosoftapp.data.repository.DataRepository
import com.marvsystems.fotosoftapp.utils.Resource
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MainViewModel(
    private val dataRepository: DataRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val lab = MutableLiveData<Resource<LabModel>>()
    private val labFromDb = MutableLiveData<Resource<List<Lab>>>()
    private val compositeDisposable = CompositeDisposable()


    fun fetchLabDetails(licenceKey: String) {
        lab.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.getLabs(licenceKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ labs ->
                    saveLab(labs)
                    lab.postValue(Resource.success(labs.installation))
                }, {
                    it.printStackTrace()
                    it as ANError
                    when {
                        it.errorBody != null -> lab.postValue(Resource.error(it.errorBody, null))
                        it.errorDetail != null -> lab.postValue(Resource.error(it.errorDetail, null))
                        else -> lab.postValue(Resource.error("Something went wrong", null))
                    }
                })
        )
    }

    fun fetchAllLabs() {
        compositeDisposable.add(appDatabase.labDao().getAllLabs()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ labs -> labFromDb.postValue(Resource.success(labs)) }
            ) {
                it.printStackTrace()
                lab.postValue(Resource.error("Something Went Wrong", null))
            })
    }

    fun fetchAllLabsForUpdate() {
        compositeDisposable.add(appDatabase.labDao().getAllLabsSingle()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ labs ->
                updateLabDetails(labs)
            }
            ) {
                it.printStackTrace()

            })
    }

    private fun updateLabDetails(labs: List<Lab>?) {
        labs?.forEach { lab ->
            run {
                fetchLabDetails(lab.licenseKey!!)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getLab(): LiveData<Resource<LabModel>> {
        return lab
    }

    fun labsFromDB(): LiveData<Resource<List<Lab>>> {
        return labFromDb
    }

    private fun saveLab(labResponseModel: LabResponseModel) {
        var contactPersonMobile  = ""
        var contactPersonInternalMobile  = ""

        if (labResponseModel.installation?.comp?.contactPersonMobile != null)
            contactPersonMobile = labResponseModel.installation?.comp?.contactPersonMobile!!
        if (labResponseModel.installation?.comp?.contactPersonInternalMobile != null)
            contactPersonInternalMobile = labResponseModel.installation?.comp?.contactPersonInternalMobile!!



        val lab = Lab(
            labResponseModel.installation?.id!!,
            labResponseModel.installation?.compId!!,
            labResponseModel.installation?.licenseKey,
            labResponseModel.installation?.comp?.compName,
            labResponseModel.installation?.comp?.compAdd,
            labResponseModel.installation?.comp?.compCity,
            labResponseModel.installation?.comp?.compState,
            labResponseModel.installation?.comp?.compLocation,
            labResponseModel.installation?.comp?.compPhone,
            labResponseModel.installation?.comp?.compEmail,
            labResponseModel.installation?.comp?.contactPerson,
            contactPersonMobile,
            contactPersonInternalMobile,
            labResponseModel.installation?.comp?.compLogoImage,
            labResponseModel.support?.albumUploaderNo,
            labResponseModel.support?.albumBookingNo,
            labResponseModel.support?.delivery1No,
            labResponseModel.support?.delivery2No,
            labResponseModel.support?.accountsNo,
            labResponseModel.support?.technicalSupportNo,
            labResponseModel.support?.complainNo,
            labResponseModel.support?.othersNo,
            null, null, false,
            null
        )

        Completable.fromAction {
            appDatabase.labDao().insert(lab)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    Log.e("Completed", "Completed")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    if (e is SQLiteConstraintException) {
                        updateLab(lab)
                    }
                }
            })
    }

    fun deleteLab(lab: Lab) {
        Completable.fromAction {
            appDatabase.labDao().delete(lab)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                    fetchAllLabs()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })

    }

    fun updateLab(lab: Lab) {
        Completable.fromAction {
            appDatabase.labDao().update(lab.id,lab.compId,
                lab.licenseKey!!,
                lab.compName!!,
                lab.compAdd!!,
                lab.compCity!!,
                lab.compState!!,
                lab.compLocation!!,
                lab.compPhone!!,
                lab.compEmail!!,
                lab.contactPerson!!,
                lab.contactPersonMobile!!,
                lab.contactPersonInternalMobile!!,
                lab.compLogoImage!!,
                lab.albumUploaderNo!!,
                lab.albumBookingNo!!,
                lab.delivery1No!!,
                lab.delivery2No!!,
                lab.accountsNo!!,
                lab.technicalSupportNo!!,
                lab.complainNo!!,
                lab.othersNo!!
                )
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })

    }
}