package com.marvsystems.fotosoftapp.data.api

import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.data.model.SignUpRequestModel
import com.marvsystems.fotosoftapp.data.model.UploadResponseModel
import io.reactivex.Single
import java.io.File

class ApiHelper(private val apiService: ApiService) {

    fun getLabs(licenceKey: String) = apiService.getLab(licenceKey)

    fun login(loginModel: LoginModel) = apiService.login(loginModel)

    fun getMasterData(jwtToken: String, compId: Int) = apiService.getMasterData(jwtToken, compId)

    fun getStates() = apiService.getStates()

    fun getPastOrders(
        jwtToken: String,
        compId: Int,
        dateFrom: String,
        dateTo: String,
        partyId: String
    ) = apiService.getPastOrders(jwtToken, compId, dateFrom, dateTo, partyId)

    fun createOrder(order: Order, jwtToken: String) = apiService.createOrder(order, jwtToken)

    fun updateOrderImages(orderImages: List<OrderImages>, jwtToken: String) =
        apiService.updateOrderImages(orderImages, jwtToken)

    fun uploadImage(file: File, orderImageId: Int, httpAddressUpload: String, imagePath: String, orderId: Int, jwtToken: String) =
        apiService.uploadImage(file, orderImageId,httpAddressUpload, imagePath,orderId, jwtToken)

    fun isUserNameAvailable(compId: Int, userName: String) =
        apiService.isUserNameAvailable(compId, userName)

    fun signUp(signUpRequestModel: SignUpRequestModel) =
        apiService.signUp(signUpRequestModel)
}