package com.marvsystems.fotosoftapp.data.repository

import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.*
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONArray
import java.io.File

class DataRepository(private val apiHelper: ApiHelper) {


    fun getLabs(licenceKey: String): Single<LabResponseModel> {
        return apiHelper.getLabs(licenceKey)
    }


    fun login(loginModel: LoginModel): Single<LoginModel> {
        return apiHelper.login(loginModel)
    }

    fun getMasterData(jwtToken: String, compId: Int): Single<MasterDataModel> {
        return apiHelper.getMasterData(jwtToken, compId)
    }

    fun getPastOrders(
        jwtToken: String,
        compId: Int,
        dateFrom: String,
        dateTo: String,
        partyId: String
    ): Single<List<Order>> {
        return apiHelper.getPastOrders(jwtToken, compId, dateFrom, dateTo, partyId)
    }

    fun getStates(): Single<List<State>> {
        return apiHelper.getStates()
    }

    fun createOrder(order: Order, jwtToken: String): Single<Order> {
        return apiHelper.createOrder(order, jwtToken)
    }

    fun updateOrderImages(orderImages: List<OrderImages>, jwtToken: String): Observable<JSONArray> {
        return apiHelper.updateOrderImages(orderImages, jwtToken)
    }

    fun uploadImage(file: File, orderImageId: Int, jwtToken: String): Single<UploadResponseModel> {
        return apiHelper.uploadImage(file, orderImageId, jwtToken)
    }

    fun isUserNameAvailable(compId: Int,userName:String): Observable<Boolean> {
        return apiHelper.isUserNameAvailable(compId, userName)
    }

    fun signUp(signUpRequestModel:SignUpRequestModel): Single<SignUpRequestModel> {
        return apiHelper.signUp(signUpRequestModel)
    }
}