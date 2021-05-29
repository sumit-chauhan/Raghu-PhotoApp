package com.marvsystems.fotosoftapp.data.database.mapper

data class PaperSize(
    val paperSizeId: String,
    val description: String,
) {

    override fun toString(): String {
        return description
    }

}