package com.marvsystems.fotosoftapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single

@Dao
interface OrderDao {

    @Query("SELECT * FROM `Order` where compId =:compId AND status!= 'COMPLETED' order by localOrderId desc limit 1")
    fun getRecentOrder(compId: Int): Single<Order>

    @Query("SELECT * FROM `Order` WHERE localOrderId =:localOrderId")
    fun getAlbumById(localOrderId: Int): LiveData<Order>

    @Query("UPDATE `Order` SET status = 'COMPLETED' WHERE (SELECT COUNT(*) FROM `OrderImages` WHERE localOrderId =:localOrderId and (OrderImages.status='PENDING' OR OrderImages.status='UPLOADING' OR OrderImages.status='FAILED'))<=0")
    fun updateOrderStatus(localOrderId: Int): Int

    @Query("UPDATE `Order` SET status = :status WHERE localOrderId=:localOrderId")
    fun changeOrderStatus(localOrderId: Int, status: String): Int

    @Query("UPDATE `Order` SET status = :status ,id = :orderId, orderNo = :orderNo, folderName = :folderName WHERE localOrderId=:localOrderId")
    fun updateOrderInfo(localOrderId: Int, orderId: Int, orderNo: String, folderName: String, status: String): Int

    @Query("UPDATE `Order` SET isPause = :isPause WHERE localOrderId=:localOrderId")
    fun toggleOrderUploadStatus(localOrderId: Int, isPause: Boolean): Int

    @Insert
    fun insert(order: Order): Long

    @Update
    fun update(order: Order)

    @Delete
    fun delete(order: Order)
}