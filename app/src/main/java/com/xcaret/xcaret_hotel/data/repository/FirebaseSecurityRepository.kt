package com.xcaret.xcaret_hotel.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.xcaret.xcaret_hotel.data.mapper.UserMapper
import com.xcaret.xcaret_hotel.data.mapper.WeatherMapper
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.config.LogHX

class UserRepository(private var userUID: String = ""): FirebaseDatabaseRepository<User>(){
    private var rootNode = FirebaseReference.VISITORS

    init {
        this.rootNode += "/$userUID"
        init(UserMapper(), FirebaseDatabase.getInstance(FirebaseReference.FB_Security))
    }

    fun updateRootNode(uid: String){
        LogHX.i("old user reference", rootNode)
        if(userUID != uid) {
            userUID = uid
            this.rootNode = "${FirebaseReference.VISITORS}/$userUID"
            removeListener()
            updateDatabaseReference()
            LogHX.i("new user reference", rootNode)
        }
    }

    override fun getRootNode(): String = rootNode

    fun getToken(isSuccessGetToken: (token: String?) -> Unit){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(!task.isSuccessful) isSuccessGetToken("")
            else isSuccessGetToken(task.result)
        }
    }

}