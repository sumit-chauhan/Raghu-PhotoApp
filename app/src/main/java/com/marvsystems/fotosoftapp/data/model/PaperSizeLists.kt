package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PaperSizeLists(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val compId: Int?,
    val createdByUsrId: Int?,
    val createdOnDt: String?,
    val paperSizeId: String?,
    val productCategoryId: String?,
    val productId: String?,
    val usrAe: String?
)
{
    override fun toString(): String {
        return paperSizeId!!
    }

}