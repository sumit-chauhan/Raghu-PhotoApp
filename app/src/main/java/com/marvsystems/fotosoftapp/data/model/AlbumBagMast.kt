package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumBagMast(
    @PrimaryKey(autoGenerate = true)
    val bagId : Int,
    val id: String?,
    val compId: Int?,
    val createdByUsrId: Int?,
    val createdOnDt: String?,
    val description: String?,
    val displayOrder: Int?,
    val updateByUserId: Int?,
    val updateOnDt: String?,
    val usrAe: String?
){
    override fun toString(): String {
        return description!!
    }

}