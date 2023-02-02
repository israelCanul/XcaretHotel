package com.xcaret.xcaret_hotel.photopass.data.mapper

import android.util.Log
import com.google.firebase.database.DataSnapshot
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
abstract class FirebasePhotoMapper<Entity, Model> : IMapper<Entity, Model> {

    open fun map(dataSnapshot: DataSnapshot): Model?{
        return try {
            map(dataSnapshot.getValue(getEntityClass()), dataSnapshot.key!!)
        }catch (e: Exception){
            Log.d(getEntityClass().simpleName + " mapper", e.message?: "default")
            e.printStackTrace()
            null
        }
    }

    open fun mapList(dataSnapshot: DataSnapshot): List<Model>{
        val list = mutableListOf<Model>()
        if(isList()){
            for(item in dataSnapshot.children)
                map(item)?.let { list.add(it) }
        }else map(dataSnapshot)?.let { list.add(it) }
        return list
    }

    open fun isList(): Boolean = true

    private fun getEntityClass(): Class<Entity>{
        val superClass = this::class.java.genericSuperclass as ParameterizedType
        return superClass.actualTypeArguments[0] as Class<Entity>
    }
}