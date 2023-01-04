package com.xcaret.xcaret_hotel.photopass.data.repository

import android.util.Log
import com.google.firebase.database.BuildConfig
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.xcaret.xcaret_hotel.data.mapper.FirebaseMapper
import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.data.repository.FirebaseReference
import com.xcaret.xcaret_hotel.photopass.data.mapper.PhotoUserMapper
import com.xcaret.xcaret_hotel.photopass.data.mapper.UserPhotoMapper
import com.xcaret.xcaret_hotel.photopass.data.room.dao.PhotoCodesDao
import com.xcaret.xcaret_hotel.photopass.domain.PhotoCodes

class PhotoCodesRepository(var photocode: String, var dao: PhotoCodesDao) : FirebaseDatabaseRepository<PhotoCodes>() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var mapperSingleObject: FirebaseMapper<*, *>? = null
    init {
        initSingle(PhotoUserMapper(), FirebaseDatabase.getInstance(FirebaseReference.FB_Photo_Pass))
    }

    fun initSingle(mapperSingleObject: FirebaseMapper<*, *>,
                   firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(FirebaseReference.FB_Photo_Pass)){
        this.firebaseDatabase = firebaseDatabase
        databaseReference = firebaseDatabase.getReference(getRootNode())
        Log.i("Referencia DB Photopass", databaseReference.toString())
        this.mapperSingleObject = mapperSingleObject
    }
    override fun getRootNode(): String = FirebaseReference.PHOTOCODES_CHILD + photocode
}
class PhotoUserRepository(var uid: String): FirebaseDatabaseRepository<String>() {
    init {
        init(UserPhotoMapper(), FirebaseDatabase.getInstance(FirebaseReference.FB_Photo_Pass))
    }
    override fun getRootNode(): String ="${FirebaseReference.PHOTOUSERS_CHILD}${uid}/listCodes"
}