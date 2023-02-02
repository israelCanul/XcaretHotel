package com.xcaret.xcaret_hotel.photopass.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xcaret.xcaret_hotel.photopass.data.mapper.FirebasePhotoMapperSingleObject

@Suppress("UNCHECKED_CAST")
class SingleObjectBaseValueListener<Model> : ValueEventListener {

    private var mapperSingleObject: FirebasePhotoMapperSingleObject<*, *>
    private var callback: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallbackSingleObject<Model>

    constructor(mapperSingleObject: FirebasePhotoMapperSingleObject<*, *>, callback: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallbackSingleObject<Model>){
        this.mapperSingleObject = mapperSingleObject
        this.callback = callback
    }

    override fun onCancelled(p0: DatabaseError) {
        callback.onError(p0.toException())
    }

    override fun onDataChange(p0: DataSnapshot) {
        val data = mapperSingleObject.toMap(p0)
        callback.onSuccess(data as Model)
    }

}