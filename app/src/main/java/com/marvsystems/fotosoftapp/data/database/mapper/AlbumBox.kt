package com.marvsystems.fotosoftapp.data.database.mapper

data class AlbumBox(
    val albumBoxId: String,
    val description: String,
) {

    override fun toString(): String {
        return description
    }

}