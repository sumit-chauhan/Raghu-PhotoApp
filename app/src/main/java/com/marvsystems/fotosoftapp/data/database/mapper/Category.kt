package com.marvsystems.fotosoftapp.data.database.mapper

data class Category(
    val productCategoryId: String,
    val description: String,
) {

    override fun toString(): String {
        return description
    }

}