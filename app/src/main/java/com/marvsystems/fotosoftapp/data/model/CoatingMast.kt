package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CoatingMast(
    @PrimaryKey(autoGenerate = true)
    val coatingId : Int,
    val compId: Int?,
    val createdByUsrId: Int?,
    val createdOnDt: String?,
    val description: String?,
    val displayOrder: Int?,
    val id: String?,
    val usrAe: String?
){
    override fun toString(): String {
        return description!!
    }

}