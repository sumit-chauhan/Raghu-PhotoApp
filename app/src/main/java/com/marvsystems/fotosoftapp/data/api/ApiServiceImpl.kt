package com.marvsystems.fotosoftapp.data.api

import android.util.Log
import com.androidnetworking.common.Priority
import com.google.gson.Gson
import com.marvsystems.fotosoftapp.data.database.Order
import com.marvsystems.fotosoftapp.data.database.OrderImages
import com.marvsystems.fotosoftapp.data.model.*
import com.marvsystems.fotosoftapp.utils.Constants.BASE_URL
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ApiServiceImpl : ApiService {


    override fun getLab(licenceKey: String): Single<LabResponseModel> {
        Log.e("getLab info api",BASE_URL + "api/Installations/LicenseKey/" + licenceKey)
        return Rx2AndroidNetworking.get(BASE_URL + "api/Installations/LicenseKey/" + licenceKey)
            .build()
            .getObjectSingle(LabResponseModel::class.java)
    }

    override fun login(loginModel: LoginModel): Single<LoginModel> {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", loginModel.username)
            jsonObject.put("password", loginModel.password)
            jsonObject.put("compId", loginModel.compId)
            jsonObject.put("appVersion", loginModel.appVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return Rx2AndroidNetworking.post(BASE_URL + "Users/authenticate")
            .addJSONObjectBody(jsonObject)
            .setContentType("application/json")
            .build()
            .getObjectSingle(LoginModel::class.java)
    }

    override fun getMasterData(jwtToken: String, compId: Int): Single<MasterDataModel> {
        return Rx2AndroidNetworking.get(BASE_URL + "api/Masters/getAll/" + compId)
            .addHeaders("Authorization", "Bearer $jwtToken")
            .build()
            .getObjectSingle(MasterDataModel::class.java)
    }

    override fun getStates(): Single<List<State>> {
        return Rx2AndroidNetworking.get(BASE_URL + "api/Masters/States")
            .build()
            .getObjectListSingle(State::class.java)
    }

    override fun getPastOrders(
        jwtToken: String,
        compId: Int,
        dateFrom: String,
        dateTo: String,
        partyId: String
    ): Single<List<Order>> {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("compId", compId)
            jsonObject.put("partyId", partyId)
            jsonObject.put("startDate", dateFrom)
            jsonObject.put("endDate", dateTo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return Rx2AndroidNetworking.post(BASE_URL + "api/orders/history")
            .addJSONObjectBody(jsonObject)
            .setContentType("application/json")
            .addHeaders("Authorization", "Bearer $jwtToken")
            .build()
            .getObjectListSingle(Order::class.java)
    }

    override fun createOrder(order: Order, jwtToken: String): Single<Order> {
        Log.e("Create Order Request", Gson().toJson(order))
        return Rx2AndroidNetworking.post(BASE_URL + "api/Orders")
            .addApplicationJsonBody(order)
            .addHeaders("Authorization", "Bearer $jwtToken")
            .build()
            .getObjectSingle(Order::class.java)
    }

    override fun updateOrderImages(
        orderImages: List<OrderImages>,
        jwtToken: String
    ): Observable<JSONArray> {
        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS).build()
        Log.e("OrderImageRequest", Gson().toJson(orderImages))
        return Rx2AndroidNetworking.post(BASE_URL + "api/OrderImages/list")
            .setOkHttpClient(client)
            .addApplicationJsonBody(orderImages)
            .addHeaders("Authorization", "Bearer $jwtToken")
            .build()
            .jsonArrayObservable
    }

    override fun uploadImage(
        file: File,
        orderImageId: Int,
        jwtToken: String
    ): Single<UploadResponseModel> {
        Log.e("ImageUpload", orderImageId.toString())
        return Rx2AndroidNetworking.upload(BASE_URL + "api/OrderImages/UploadImagefile")
            .addMultipartFile("ImageFile", file)
            .addMultipartParameter("OrderImageId", orderImageId.toString())
            .addHeaders("Authorization", "Bearer $jwtToken")
            .setTag(orderImageId)
            .setPriority(Priority.HIGH)
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()
            .setUploadProgressListener { bytesUploaded, totalBytes ->
                Log.e(
                    "upload/total",
                    "$bytesUploaded/$totalBytes"
                )
            }
            .getObjectSingle(UploadResponseModel::class.java)// setting an executor to get response or completion on that executor thread
    }

    override fun isUserNameAvailable(compId: Int, userName: String): Observable<Boolean> {
        return Rx2AndroidNetworking.get(BASE_URL + "Users/UserExists/"+userName+"/"+compId)
            .build()
            .getObjectObservable(Boolean::class.java)
    }

    override fun signUp(signUpRequestModel: SignUpRequestModel): Single<SignUpRequestModel> {
        Log.e("SignUp Request", Gson().toJson(signUpRequestModel))
        return Rx2AndroidNetworking.post(BASE_URL + "Users/register")
            .addApplicationJsonBody(signUpRequestModel)
            .build()
            .getObjectSingle(SignUpRequestModel::class.java)
    }

}