package com.marvsystems.fotosoftapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface OrderImagesDao {

    @Insert
    fun insert(orderImages: List<OrderImages>)

    @Update
    fun update(orderImages: List<OrderImages>)

    @Query("SELECT * FROM `OrderImages` WHERE localOrderId =:localOrderId")
    fun getOrderImages(localOrderId: Int): Flowable<List<OrderImages>>

    @Query("SELECT * FROM `OrderImages` WHERE localOrderId =:localOrderId AND status!='UPLOADED' order by localImageId asc limit 1")
    fun getPendingImage(localOrderId: Int): Single<OrderImages>

    @Update
    fun updateOrderImage(orderImages: OrderImages)

    @Query("Update `OrderImages` set status = 'PAUSE' WHERE localOrderId =:localOrderId AND (status='FAILED' OR status='UPLOADING')")
    fun markImagesPause(localOrderId: Int)

    @Query("Update `OrderImages` set status = 'PENDING' WHERE localOrderId =:localOrderId AND status='UPLOADING'")
    fun markImagesPending(localOrderId: Int)

    @Query("Update OrderImages set orderId=:orderId,orderNo=:orderNo WHERE localOrderId =:localOrderId")
    fun updateOrderId(localOrderId: Int, orderId: Int, orderNo: String)

}