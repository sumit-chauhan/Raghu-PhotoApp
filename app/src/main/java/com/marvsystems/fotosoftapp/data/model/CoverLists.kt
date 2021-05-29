package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CoverLists(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val compId: Int?,
    val coverId: String?,
    val createdByUsrId: Int?,
    val createdOnDt: String?,
    val productCategoryId: String?,
    val productId: String?,
    val usrAe: String?
) {
    override fun toString(): String {
        return coverId!!
    }
}