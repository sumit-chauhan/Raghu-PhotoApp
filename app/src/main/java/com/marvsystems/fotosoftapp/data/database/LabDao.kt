package com.marvsystems.fotosoftapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LabDao {

    @Query("SELECT * FROM Lab")
    fun getAllLabs(): Flowable<List<Lab>>

    @Query("SELECT * FROM Lab")
    fun getAllLabsSingle(): Single<List<Lab>>

    @Query("SELECT * FROM Lab WHERE id =:id")
    fun getLabById(id: IntArray): LiveData<Lab>

    @Insert
    fun insert(vararg lab: Lab)

    @Query("UPDATE Lab SET username=:username,password=:password,remember=:remember,jwtToken=:jwtToken WHERE compId =:compId")
    fun updateLoggedInfo(
        username: String,
        password: String,
        remember: Boolean,
        compId: Int,
        jwtToken: String
    )

    @Delete
    fun delete(lab: Lab)

    @Query("UPDATE Lab SET compId=:compId,licenseKey=:licenseKey, compName=:compName,compAdd=:compAdd,compCity=:compCity,compState=:compState,compLocation=:compLocation,compPhone=:compPhone,compEmail=:compEmail,contactPerson=:contactPerson,contactPersonMobile=:contactPersonMobile,contactPersonInternalMobile=:contactPersonInternalMobile,compLogoImage=:compLogoImage,albumUploaderNo=:albumUploaderNo,albumBookingNo=:albumBookingNo,delivery1No=:delivery1No,delivery2No=:delivery2No,accountsNo=:accountsNo,technicalSupportNo=:technicalSupportNo,complainNo=:complainNo,othersNo=:othersNo WHERE id = :id")
    fun update(
        id: Int, compId: Int, licenseKey: String,
        compName: String, compAdd: String,
        compCity: String, compState: String,
        compLocation: String, compPhone: String,
        compEmail: String, contactPerson: String,
        contactPersonMobile: String, contactPersonInternalMobile: String, compLogoImage: String,
        albumUploaderNo: String, albumBookingNo: String, delivery1No: String,
        delivery2No: String, accountsNo: String, technicalSupportNo: String,
        complainNo: String, othersNo: String
    )
}