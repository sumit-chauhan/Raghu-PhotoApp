package com.marvsystems.fotosoftapp.data.database.mapper

data class Cover(
    val coverId: String,
    val description: String,
) {

    override fun toString(): String {
        return description
    }

}