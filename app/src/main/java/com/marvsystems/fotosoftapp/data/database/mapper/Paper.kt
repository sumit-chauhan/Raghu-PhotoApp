package com.marvsystems.fotosoftapp.data.database.mapper

data class Paper(
    val paperId: String,
    val description: String,
) {

    override fun toString(): String {
        return description
    }

}