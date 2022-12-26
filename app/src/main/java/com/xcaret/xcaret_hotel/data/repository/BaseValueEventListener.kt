package com.xcaret.xcaret_hotel.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xcaret.xcaret_hotel.data.mapper.FirebaseMapper

/**
 * Created by José Cárdenas on 2020-01-16
 */

@Suppress("UNCHECKED_CAST")
class BaseValueEventListener<Model> : ValueEventListener {

    private var mapper: FirebaseMapper<*, *>
    private var callback: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>

    constructor(mapper: FirebaseMapper<*, *>, callback: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Model>){
        this.mapper = mapper
        this.callback = callback
    }

    override fun onCancelled(p0: DatabaseError) {
        callback.onError(p0.toException())
    }

    override fun onDataChange(p0: DataSnapshot) {
        val data = mapper.mapList(p0).toMutableList()
        callback.onSuccess(data as MutableList<Model>)
    }

}