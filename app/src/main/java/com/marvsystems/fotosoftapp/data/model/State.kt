package com.marvsystems.fotosoftapp.data.model

data class State(
    val description: String,
    val id: Int
){
    override fun toString(): String {
        return description
    }

}