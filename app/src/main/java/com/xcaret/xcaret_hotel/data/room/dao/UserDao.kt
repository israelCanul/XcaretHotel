package com.xcaret.xcaret_hotel.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.config.Session

@Dao
abstract class UserDao: BaseDao<User>(User::class.simpleName ?: "") {

    @Query("SELECT * FROM User u LIMIT 1")
    abstract fun getSession(): LiveData<User?>

    @Query("SELECT * FROM User u LIMIT 1")
    abstract fun getUser(): User?

    @Query("SELECT * FROM User u LIMIT 1")
    abstract fun getUserLive(): LiveData<User?>

    @RawQuery
    abstract fun updateProfilePictureByQuery(query: SupportSQLiteQuery): Int

    fun updateProfilePicture(uid: String = Session.getUID(HotelXcaretApp.mContext) ?: "", url: String): Int{
        val updateQuery = "UPDATE ${User::class.simpleName ?: ""} SET picture='$url' WHERE uid='$uid'"
        return updateProfilePictureByQuery(SimpleSQLiteQuery(updateQuery))
    }
}