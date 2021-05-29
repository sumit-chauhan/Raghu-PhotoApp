package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Product(
    val compId: Int?,
    val createdByUsrId: Int?,
    val createdOnDt: String?,
    val description: String?,
    val displayOrder: Int?,
    @PrimaryKey
    val id: String,
    val oldId: String?,
    val usrAe: String?

) : Serializable {

    override fun toString(): String {
        return description!!
    }

}

