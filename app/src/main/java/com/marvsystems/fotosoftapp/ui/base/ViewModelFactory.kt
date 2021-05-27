package com.marvsystems.fotosoftapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.data.repository.DataRepository
import com.marvsystems.fotosoftapp.ui.album.viewmodel.AttachImagesViewModel
import com.marvsystems.fotosoftapp.ui.album.viewmodel.CreateAlbumViewModel
import com.marvsystems.fotosoftapp.ui.album.viewmodel.SelectProductViewModel
import com.marvsystems.fotosoftapp.ui.auth.viewmodel.LoginViewModel
import com.marvsystems.fotosoftapp.ui.auth.viewmodel.SignUpViewModel
import com.marvsystems.fotosoftapp.ui.dashboard.viewmodel.DashboardViewModel
import com.marvsystems.fotosoftapp.ui.main.viewmodel.MainViewModel

class ViewModelFactory(private val apiHelper: ApiHelper, private val appDatabase: AppDatabase?) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(DataRepository(apiHelper), appDatabase!!) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(DataRepository(apiHelper), appDatabase!!) as T
        }
        if (modelClass.isAssignableFrom(CreateAlbumViewModel::class.java)) {
            return CreateAlbumViewModel(appDatabase!!) as T
        }
        if (modelClass.isAssignableFrom(SelectProductViewModel::class.java)) {
            return SelectProductViewModel(DataRepository(apiHelper), appDatabase!!) as T
        }
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(DataRepository(apiHelper), appDatabase!!) as T
        }
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(DataRepository(apiHelper)) as T
        }
        if (modelClass.isAssignableFrom(AttachImagesViewModel::class.java)) {
            return AttachImagesViewModel(DataRepository(apiHelper), appDatabase!!) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}