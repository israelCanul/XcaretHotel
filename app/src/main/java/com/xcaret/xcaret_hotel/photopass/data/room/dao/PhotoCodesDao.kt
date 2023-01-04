package com.xcaret.xcaret_hotel.photopass.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xcaret.xcaret_hotel.photopass.domain.AlbumList
import com.xcaret.xcaret_hotel.photopass.domain.PhotoCodes

@Dao
interface PhotoCodesDao{

    @Transaction
    fun insertAll(list: MutableList<AlbumList>?) = list?.forEach(){ album ->
        insertAlbumList(album)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbumList(vararg album: AlbumList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoCode(vararg photo: PhotoCodes)

    @Query("SELECT * FROM PhotoCodes")
    fun getAllPhoto(): LiveData<List<PhotoCodes>>

    @Query("SELECT * FROM AlbumList")
    fun getAllAlbum(): LiveData<List<AlbumList>>

    @Query("""SELECT * FROM AlbumList a
        WHERE a.code = :code GROUP BY a.generic_id""")
    fun getAlbumByCode(code:String): LiveData<List<AlbumList>>

    @Query("""SELECT * FROM PhotoCodes pc
        WHERE pc.code = :code GROUP BY pc.photo_id""")
    fun getPhotoByCode(code:String): LiveData<PhotoCodes>

    /*
    @Query("""SELECT * FROM AlbumList a
        WHERE a.generic_id = :key""")
    fun getAlbumByKey(key:String): LiveData<AlbumList>
     */

    @Query("""SELECT * FROM AlbumList a
        WHERE a.generic_id = :key""")
    fun getAlbumByKey(key:String): AlbumList?

    @Query("SELECT * FROM AlbumList")
    fun getAllAlbumNotData(): List<AlbumList>?
}
