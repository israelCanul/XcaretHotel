package com.xcaret.xcaret_hotel.photopass.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.xcaret.xcaret_hotel.data.mapper.FirebaseMapper
import com.xcaret.xcaret_hotel.photopass.data.api.Photo
import com.xcaret.xcaret_hotel.photopass.data.api.ShareFile
import com.xcaret.xcaret_hotel.photopass.data.entity.AlbumListEntity

import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoCodesEntity
import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoUserAlbumsEntity
import com.xcaret.xcaret_hotel.photopass.data.mapper.FirebasePhotoMapperSingleObject
import com.xcaret.xcaret_hotel.photopass.data.mapper.PhotoUserMapper
import com.xcaret.xcaret_hotel.photopass.data.mapper.UserPhotoMapper
import com.xcaret.xcaret_hotel.photopass.data.room.dao.PhotoCodesDao
import com.xcaret.xcaret_hotel.photopass.domain.AlbumList
import com.xcaret.xcaret_hotel.photopass.domain.PhotoCodes
import com.xcaret.xcaret_hotel.photopass.vm.AlbumPPViewModel
import java.text.SimpleDateFormat
import java.util.*

class PhotoCodesRepository(var photocode: String, var dao: PhotoCodesDao) : FirebaseDatabaseRepository<PhotoCodes>() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var mapperSingleObject: FirebasePhotoMapperSingleObject<*, *>? = null
    private var xcaretApp: FirebaseApp
    init {
        xcaretApp = FirebaseApp.getInstance("xcaret_database")
        initSingle(PhotoUserMapper(), FirebaseDatabase.getInstance(xcaretApp,FirebasePhotoReference.FB_Photo_Pass))
    }
    override fun getRootNode(): String = FirebasePhotoReference.PHOTOCODES_CHILD + photocode
}
class PhotoUserRepository(var uid: String): FirebaseDatabaseRepository<String>() {
    var xcaretApp: FirebaseApp = FirebaseApp.getInstance("xcaret_database")
    private val databaseRefCodes = FirebaseDatabase.getInstance(xcaretApp,FirebasePhotoReference.FB_Photo_Pass).getReference(FirebasePhotoReference.PHOTOCODES_CHILD)
    private val databaseRefUsers = FirebaseDatabase.getInstance(FirebasePhotoReference.FB_Photo_Pass).getReference(FirebasePhotoReference.PHOTOUSERS_CHILD)

    init {
        Log.i("databaseRefCodes",databaseRefCodes.toString())
        init(UserPhotoMapper(), FirebaseDatabase.getInstance(xcaretApp,FirebasePhotoReference.FB_Photo_Pass))
    }
    override fun getRootNode(): String ="${FirebasePhotoReference.PHOTOUSERS_CHILD}${uid}/listCodes"
    fun updateAlbumData(album: PhotoCodesEntity){
        databaseRefCodes.child(Objects.requireNonNull(album.code!!)).child("visitDate").setValue(album.visitDate)
        databaseRefCodes.child(Objects.requireNonNull(album.code!!)).child("expiresDate").setValue(album.expiresDate)
        databaseRefCodes.child(Objects.requireNonNull(album.code!!)).child("valid").setValue(album.valid)
    }
    fun updatePhotoCode(album: PhotoCodesEntity, children: ArrayList<AlbumListEntity>){
        databaseRefCodes.child(Objects.requireNonNull(album.code!!)).child("totalUnlock").setValue(album.totalUnlock)
        val cant = children.size
        for (i in 0 until cant) {
            val albumChild = children[i]
            val find = AlbumPPViewModel().validateAlbumListRoom(albumChild.parkID, album.code)
            if (find != null) {
                databaseRefCodes.child(album.code!!).child("albumsList").child(find.uid).child("totalMedia").setValue(albumChild.totalMedia)
                databaseRefCodes.child(album.code!!).child("albumsList").child(find.uid).child("unlock").setValue(albumChild.unlock)
                databaseRefCodes.child(album.code!!).child("albumsList").child(find.uid).child("visitDate").setValue(albumChild.visitDate)
            } else {
                val key: String? = databaseRefCodes.child(album.code!!).child("albumsList").push().key
                key?.let {
                    databaseRefCodes.child(album.code!!).child("albumsList").child(it).setValue(albumChild.toMap())
                }
            }
        }
    }
    fun savePhotoCode(album: PhotoCodesEntity , children: ArrayList<AlbumListEntity>){
        databaseRefCodes.child(Objects.requireNonNull(album.code!!)).setValue(album.toMap())
        val cant = children.size
        for (i in 0 until cant) {
            val albumChild = children[i]
            val key: String? = databaseRefCodes.child(album.code!!).child("albumsList").push().key
            key?.let {
                databaseRefCodes.child(album.code!!).child("albumsList").child(it).setValue(albumChild.toMap())
            }
        }
    }
    fun savePhotoUsers(photoUserAlbumsEntity: PhotoUserAlbumsEntity){
        if(uid == ""){
            //TODO: Agregar el uid de cognito a esta seccion ya que es la que esta ligada
            val currentUser = FirebaseAuth.getInstance().currentUser
            uid = currentUser?.uid ?: ""
        }
        databaseRefUsers.child(uid).child("listCodes").child(Objects.requireNonNull(photoUserAlbumsEntity.uid)).setValue(photoUserAlbumsEntity.toMap())
    }
    fun updateAlbumTotalMedia(code: String, parkID: Int, cantPhotos: Int ,photoGallery: List<PhotoCodes>?, albumGallery: List<AlbumList>?){
        var photoCodes: PhotoCodes? = null
        if(uid == ""){
            val currentUser = FirebaseAuth.getInstance().currentUser
            uid = currentUser?.uid ?: ""
        }

        photoGallery?.forEach { it ->
            if (code == it.code)
                photoCodes = it
        }

        photoCodes.let {
            if (it != null) {
                albumGallery.let { albumGallery ->
                    if (albumGallery != null) {
                        val cant = albumGallery.size
                        for (i in 0 until cant) {
                            val albumChild = albumGallery[i]
                            if (albumChild.parkID == parkID) {
                                databaseRefCodes.child(code).child("albumsList").child(Objects.requireNonNull(albumChild.uid)).child("totalMedia").setValue(cantPhotos)
                                albumChild.unlock = true
                            }
                        }
                    }
                }
            }
        }
    }
    fun updateAlbumForUnlock( code: String, parkID: Int, photoGallery: List<PhotoCodes>?, albumGallery: List<AlbumList>?){
        var photoCodes: PhotoCodes? = null

        if (uid == "") {
            val currentUser = FirebaseAuth.getInstance().currentUser
            uid = currentUser?.uid ?: ""
        }

        photoGallery?.forEach { it ->
            if (code == it.code)
                photoCodes = it
        }

        photoCodes.let {
            if (it != null) {
                it.totalUnlock = it.totalUnlock!! + 1
                databaseRefCodes.child(code).child("totalUnlock").setValue(it.totalUnlock)
                albumGallery.let { albumGallery ->
                    if (albumGallery != null) {
                        val cant = albumGallery.size
                        for (i in 0 until cant) {
                            val albumChild = albumGallery[i]
                            if (albumChild.parkID == parkID) {
                                databaseRefCodes.child(code).child("albumsList").child(Objects.requireNonNull(albumChild.uid)).child("unlock").setValue(true)
                                albumChild.unlock = true
                            }
                        }
                    }
                }
            }
        }
    }
    //region Save/Share actions
    fun shareLogPhotoAction(code: String, albumUID: String, shareFiles: ArrayList<ShareFile>){
        if(uid == ""){
            val currentUser = FirebaseAuth.getInstance().currentUser
            uid = currentUser?.uid ?: ""
        }

        if (code.isNotEmpty() && albumUID.isNotEmpty()) {
            val cant = shareFiles.size
            for (i in 0 until cant) {
                val item = shareFiles[i]
                val mediaID = item.photo.mediaID.toString()
                val key = databaseRefUsers.child(uid).child("listCodes").child(code).child("savePhotos").child(albumUID).child(mediaID).push().key
                val dates: MutableMap<String, String> = HashMap()
                @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
                dates["date"] = dateFormat.format(Date())
                if (key != null){
                    databaseRefUsers.child(uid).child("listCodes").child(code).child("savePhotos").child(albumUID).child(mediaID).child(key).setValue(dates)
                }
            }
        }

    }

    fun saveLogPhotoAction(code: String, albumUID: String, photo: Photo){
        if(uid == ""){
            val currentUser = FirebaseAuth.getInstance().currentUser
            uid = currentUser?.uid ?: ""
        }
        if (code.isNotEmpty() && albumUID.isNotEmpty()) {
            val mediaID = photo.mediaID.toString()
            val key = databaseRefUsers.child(uid).child("listCodes").child(code).child("savePhotos").child(albumUID).child(mediaID).push().key
            val dates: MutableMap<String, String> = HashMap()
            @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            dates["date"] = dateFormat.format(Date())
            if (key != null){
                databaseRefUsers.child(uid).child("listCodes").child(code).child("savePhotos").child(albumUID).child(mediaID).child(key).setValue(dates)
            }
        }
    }
}