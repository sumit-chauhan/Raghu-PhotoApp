package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumBagLists(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val albumBagId: String?,
    val compId: Int?,
    val createdByUsrId: Int?,
    val createdOnDt: String?,
    val productCategoryId: String?,
    val productId: String?,
    val usrAe: String?
) {
    override fun toString(): String {
        return albumBagId!!
    }

}