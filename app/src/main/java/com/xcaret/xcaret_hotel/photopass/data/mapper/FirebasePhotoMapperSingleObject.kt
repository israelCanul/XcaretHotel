package com.xcaret.xcaret_hotel.photopass.data.mapper

import com.google.firebase.database.DataSnapshot
import java.lang.reflect.ParameterizedType

abstract class FirebasePhotoMapperSingleObject<Entity, Model> : IMapper<Entity, Model> {

    open fun map(dataSnapshot: DataSnapshot): Model{
        return map(dataSnapshot.getValue(getEntityClass()), dataSnapshot.key!!)
    }
    open fun toMap(dataSnapshot: DataSnapshot): Model{
        return map(dataSnapshot)
    }

    private fun getEntityClass(): Class<Entity>{
        val superClass = this::class.java.genericSuperclass as ParameterizedType
        return superClass.actualTypeArguments[0] as Class<Entity>
    }
}
interface IMapper<From, To> {
    fun map(from: From?, key: String?): To
}