package com.marvsystems.fotosoftapp.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.data.repository.DataRepository
import com.marvsystems.fotosoftapp.utils.Resource
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel(
    private val dataRepository: DataRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val user = MutableLiveData<Resource<LoginModel>>()


    fun login(loginModel: LoginModel, isRemember: Boolean) {
        user.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.login(loginModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (result?.jwtToken.isNullOrEmpty()) {
                        user.postValue(Resource.error(result.message!!, null))
                    } else {
                        result.password = loginModel.password
                        updateLoginData(result, isRemember)
                        user.postValue(Resource.success(result))
                    }
                }, {
                    it.printStackTrace()
                    user.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    private fun updateLoginData(loginModel: LoginModel, remember: Boolean) {
        Completable.fromAction {
            appDatabase.labDao().updateLoggedInfo(
                remember = remember,
                username = loginModel.username,
                password = loginModel.password,
                compId = loginModel.compId,
                jwtToken = loginModel.jwtToken
            )
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


    fun getUser(): LiveData<Resource<LoginModel>> {
        return user
    }

}