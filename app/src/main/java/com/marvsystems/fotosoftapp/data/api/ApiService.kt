package com.marvsystems.fotosoftapp.data.api

import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.*
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONArray
import java.io.File

interface ApiService {

    fun getLab(licenceKey: String): Single<LabResponseModel>

    fun login(loginModel: LoginModel): Single<LoginModel>

    fun getMasterData(jwtToken: String, compId: Int): Single<MasterDataModel>

    fun getStates(): Single<List<State>>

    fun getPastOrders(
        jwtToken: String,
        compId: Int,
        dateFrom: String,
        dateTo: String,
        partyId: String
    ): Single<List<Order>>

    fun createOrder(order: Order, jwtToken: String): Single<Order>

    fun updateOrderImages(orderImages : List<OrderImages>, jwtToken: String) : Observable<JSONArray>

    fun uploadImage(file: File, orderImageId: Int, httpAddressUpload: String, imagePath: String, orderId: Int, jwtToken: String) : Single<UploadResponseModel>

    fun isUserNameAvailable(compId: Int,userName:String) : Observable<Boolean>

    fun signUp(signUpRequestModel: SignUpRequestModel) : Single<SignUpRequestModel>
}