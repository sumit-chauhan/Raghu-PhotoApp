package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductCategory(
    val compId: Int?,
    val createdByUsrId: Int?,
    val createdOnDt: String?,
    val description: String?,
    val displayOrder: Int?,
    @PrimaryKey
    val id: String,
    val oldId: String?,
    val usrAe: String?
)
{
    override fun toString(): String {
        return description!!
    }

}