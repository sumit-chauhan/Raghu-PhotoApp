package com.marvsystems.fotosoftapp.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marvsystems.fotosoftapp.data.model.SignUpRequestModel
import com.marvsystems.fotosoftapp.data.model.State
import com.marvsystems.fotosoftapp.data.repository.DataRepository
import com.marvsystems.fotosoftapp.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SignUpViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val states = MutableLiveData<Resource<List<State>>>()
    private val isUserNameAvailable = MutableLiveData<Resource<Boolean>>()
    private val signUp = MutableLiveData<Resource<SignUpRequestModel>>()


    fun fetchStates() {
        states.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.getStates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    states.postValue(Resource.success(result))
                }, {
                    it.printStackTrace()
                    states.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun checkUserName(userName: String, compId: Int) {
        isUserNameAvailable.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.isUserNameAvailable(compId, userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    isUserNameAvailable.postValue(Resource.success(result))
                }, {
                    it.printStackTrace()
                    isUserNameAvailable.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun signUp(signUpRequestModel: SignUpRequestModel) {
        signUp.postValue(Resource.loading(null))
        compositeDisposable.add(
            dataRepository.signUp(signUpRequestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    signUp.postValue(Resource.success(result))
                }, {
                    it.printStackTrace()
                    signUp.postValue(Resource.error("Something Went Wrong", null))
                })
        )
    }

    fun getStates(): LiveData<Resource<List<State>>> {
        return states
    }

    fun getSignUp(): LiveData<Resource<SignUpRequestModel>> {
        return signUp
    }

    fun isUserNameAvailable(): LiveData<Resource<Boolean>> {
        return isUserNameAvailable
    }

}