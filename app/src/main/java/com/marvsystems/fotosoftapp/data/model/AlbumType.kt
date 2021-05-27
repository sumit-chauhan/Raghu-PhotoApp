package com.marvsystems.fotosoftapp.data.model

import androidx.room.Entity

@Entity
data class AlbumType(
    val albumType1: String,
    val compId: Int,
    val createdByUsrId: Int,
    val createdOnDt: String,
    val displayOrder: Int,
    val id: String,
    val usrAe: String
)