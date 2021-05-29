package com.marvsystems.fotosoftapp.data.database.mapper

data class Coating(
    val coatingId: String,
    val description: String,
) {

    override fun toString(): String {
        return description
    }

}