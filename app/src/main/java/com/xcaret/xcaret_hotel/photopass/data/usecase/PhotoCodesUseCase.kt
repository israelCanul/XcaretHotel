package com.xcaret.xcaret_hotel.photopass.data.usecase

import android.util.Log
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.room.HotelAppDatabase
import com.xcaret.xcaret_hotel.data.usecase.UserUseCase
import com.xcaret.xcaret_hotel.photopass.data.repository.PhotoUserRepository

class PhotoCodesUseCase {
    val database = HotelAppDatabase.getDatabase(HotelXcaretApp.mContext)



    private val userUseCase: UserUseCase = UserUseCase()


    var photoUserRepository: PhotoUserRepository? = null

    init {
        val user = userUseCase.getFirebaseUser()
        Log.i("Firebase User PhotoPass", user?.uid ?: "")
        val uId = user?.uid ?: ""

        //Dev
        //photoUserRepository = PhotoUserRepository("pSm5hUkZjuP4Mb6WsK3F9Bgx02D2")  // Change for uid

        //Prod
        photoUserRepository = PhotoUserRepository(uId)
    }

    fun insertAll(list: MutableList<*>) {}
    fun removeBy(data: Any) {}
}