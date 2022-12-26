package com.xcaret.xcaret_hotel.data.mapper

import com.google.firebase.database.DataSnapshot
import com.xcaret.xcaret_hotel.view.config.LogHX
import java.lang.Exception
import java.lang.reflect.ParameterizedType

/**
 * Created by José Cárdenas on 2020-01-16
 */

@Suppress("UNCHECKED_CAST")
abstract class FirebaseMapper<Entity, Model>(val isSingleObject: Boolean = false): IMapper<Entity, Model> {

    open fun map(dataSnapshot: DataSnapshot): Model?{
        return try {
            map(dataSnapshot.getValue(getEntityClass()), dataSnapshot.key!!)
        }catch (e: Exception){
            LogHX.e("mapper", getEntityClass().simpleName)
            null
        }
    }

    open fun mapList(dataSnapshot: DataSnapshot): List<Model>{
        val list = mutableListOf<Model>()
        if(isSingleObject){
            map(dataSnapshot)?.let { list.add(it) }
        }else {
            for (item in dataSnapshot.children)
                map(item)?.let { list.add(it) }
        }
        return list
    }

    private fun getEntityClass(): Class<Entity>{
        val superClass = this::class.java.genericSuperclass as ParameterizedType
        return superClass.actualTypeArguments[0] as Class<Entity>
    }
}