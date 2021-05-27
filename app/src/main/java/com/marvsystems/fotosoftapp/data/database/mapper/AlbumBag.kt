package com.marvsystems.fotosoftapp.data.database.mapper

data class AlbumBag(
    val albumBagId: String,
    val description: String,
) {

    override fun toString(): String {
        return description
    }

}