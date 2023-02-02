package com.xcaret.xcaret_hotel.photopass.data.usecase

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp

import com.xcaret.xcaret_hotel.data.room.HotelAppDatabase
import com.xcaret.xcaret_hotel.data.usecase.ParamSettingUseCase
import com.xcaret.xcaret_hotel.data.usecase.UserUseCase
import com.xcaret.xcaret_hotel.photopass.data.api.*

import com.xcaret.xcaret_hotel.photopass.data.config.Constants
import com.xcaret.xcaret_hotel.photopass.data.config.LocaleHelper

import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoLogin
import com.xcaret.xcaret_hotel.photopass.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.photopass.data.repository.PhotoCodesRepository
import com.xcaret.xcaret_hotel.photopass.data.repository.PhotoUserRepository
import com.xcaret.xcaret_hotel.photopass.data.room.dao.PhotoCodesDao
import com.xcaret.xcaret_hotel.photopass.domain.AlbumList
import com.xcaret.xcaret_hotel.photopass.domain.PhotoCodes
import com.xcaret.xcaret_hotel.photopass.domain.PhotoUser
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.time.Instant
import java.time.ZoneId

class PhotoCodesUseCase {
    val database = HotelAppDatabase.getDatabase(HotelXcaretApp.mContext)
    private val dao: PhotoCodesDao = database.photoCodesDao()
    //API Entity
    private lateinit var apiPhotoService : PhotoApiService
    private lateinit var photoUser: PhotoUser
    private val paramSettingUseCase: ParamSettingUseCase by lazy { ParamSettingUseCase() }

    private var photoCodesRepository: PhotoCodesRepository? = null
    private val userUseCase: UserUseCase = UserUseCase()

    var photoUserRepository: PhotoUserRepository? = null

    init {
        val user = userUseCase.getFirebaseUser()
        Log.i("Firebase User PhotoPass", user?.uid ?: "")
        val uId = user?.uid ?: ""

        //Dev
//        photoUserRepository = PhotoUserRepository("jpfEiJhdjma8Cgc0ck8QEF97e2Q2")  // Change for uid

        //Prod
        photoUserRepository = PhotoUserRepository(uId)
    }

    private fun initialize(ok:() -> Unit){
        doAsync {
            val user = paramSettingUseCase.findParamSettingByCode(Constants.photo_user)
            val pass = paramSettingUseCase.findParamSettingByCode(Constants.photo_password)
            apiPhotoService =  PhotoApiService()
            photoUser = PhotoUser(user?.value ?: "", pass?.value ?: "")
            uiThread {
                ok()
            }
        }
    }
    private fun confirmSecurity(ok: () -> Unit) {
        if (::apiPhotoService.isInitialized) ok()
        else initialize(ok)
    }
    fun getFromFirebase(listener: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallbackSingleObject<PhotoCodes>, photoCode: String) {
        photoCodesRepository = PhotoCodesRepository(photoCode, dao)
        photoCodesRepository?.addListenerSingleObject(listener)
    }

    fun insertAll(list: MutableList<*>) {}
    fun removeBy(data: Any) {}
    fun getAllPhoto(): LiveData<List<PhotoCodes>> = dao.getAllPhoto()
    fun getPhotoByCode(code: String): LiveData<PhotoCodes> = dao.getPhotoByCode(code)
    fun getAllAlbum(): LiveData<List<AlbumList>> = dao.getAllAlbum()
    fun getAllAlbumNotData(): List<AlbumList>? = dao.getAllAlbumNotData()

    fun getAlbumByCode(code: String): LiveData<List<AlbumList>> = dao.getAlbumByCode(code)
    fun getAlbumByKey(key: String):AlbumList? = dao.getAlbumByKey(key)

    @SuppressLint("CheckResult")
    fun fetchLoginFromRemote(isSuccess: (token: PhotoLogin?, error: Throwable?) -> Unit){
        try {
            confirmSecurity {
                apiPhotoService.login(photoUser.getCreateRequest())
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<Login>() {
                        override fun onSuccess(login: Login) {
                            isSuccess(mapperLogin(login),null)
                        }

                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess( null, e)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null, e)
        }
    }
    private fun mapperLogin (response: Login): PhotoLogin {
        val photoLogin = PhotoLogin()
        if(!response.token.isNullOrEmpty()) {
            photoLogin.token = response.token
            photoLogin.exp = Instant.ofEpochMilli(response.exp!!).atZone(ZoneId.systemDefault()).toLocalDateTime().toString()
        }
        return photoLogin
    }

    fun getPhotoUser(listener: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<String>) {
        photoUserRepository?.addListener(listener)
    }

    @SuppressLint("CheckResult")
    fun fetchCodeFromRemote(purchaseCode: String, token: String, isSuccess: (photoList: Code?, error: Throwable?) -> Unit) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("JoinedCode", purchaseCode)
        try {
            confirmSecurity {
                apiPhotoService.getCode(token, jsonObject)
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<Code>() {
                        override fun onSuccess(photoList: Code) {
                            isSuccess(photoList, null)
                        }
                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess(null, e)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null, e)

        }
    }
    @SuppressLint("CheckResult")
    fun fetchAlbumFromRemote(purchaseCode: String, token: String, isSuccess: (albumList: List<Album>?, error: Throwable?) -> Unit) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("JoinedCode", purchaseCode)
        try {
            confirmSecurity {
                apiPhotoService.getAlbum(token, jsonObject)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<List<Album>>() {
                        override fun onSuccess(albumList: List<Album>) {
                            val albumListFilter = albumList.filter { it.albumType == 0 }
                            isSuccess(albumList, null)
                        }

                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess(null, e)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null, e)
        }
    }
    @SuppressLint("CheckResult")
    fun fetchSelectedParksFromRemote(purchaseCode: String, token: String, isSuccess: (selectedParkList: List<SelectedPark>?, error: Throwable?) -> Unit) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("JoinedCode", purchaseCode)
        try {
            confirmSecurity {
                apiPhotoService.getSelectedParks(token, jsonObject)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<List<SelectedPark>>() {
                        override fun onSuccess(selectedParkList: List<SelectedPark>) {
                            isSuccess(selectedParkList, null)
                        }

                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess(null, e)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null, e)
        }
    }
    @SuppressLint("CheckResult")
    fun fetchPhotoStatusFromRemote(DownloadCode: String, parkID: Int, token: String, isSuccess: (photoStatus: PhotoStatus?, error: Throwable?) -> Unit) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("JoinedCode", DownloadCode)
        jsonObject.addProperty("ParkID", parkID)
        try {
            confirmSecurity {
                apiPhotoService.getPhotoStatus(token, jsonObject)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<PhotoStatus>() {
                        override fun onSuccess(photoStatus: PhotoStatus) {
                            isSuccess(photoStatus, null)
                        }

                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess(null, e)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null, e)
        }
    }
    @SuppressLint("CheckResult")
    fun fetchPhotosFromRemote(purchaseCode: String, parkID: Int, totalMedia: Int =0, page: Int = 1,  token: String, isSuccess: (photosList:  GetPhotos?, error:Throwable?) -> Unit){
        val jsonObject = JsonObject()
        jsonObject.addProperty("JoinedCode", purchaseCode)
        jsonObject.addProperty("ParkID", parkID)
        jsonObject.addProperty("Page", page)
        jsonObject.addProperty("OrderBy", 1)
        jsonObject.addProperty("PhotosPerPage", Constants.ITEMS_PER_PAGE)
        try {
            confirmSecurity {
                apiPhotoService.getPhotos(token, jsonObject)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<GetPhotos>() {
                        override fun onSuccess(photosList: GetPhotos) {
                            isSuccess(photosList, null)
                            //photoUserRepository?.updateAlbumTotalMedia(purchaseCode, parkID, photosList.size, photoGallery, albumGallery)
                        }

                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess(null, e)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null, e)
        }
    }
    @SuppressLint("CheckResult")
    fun fetchAddVisitParkRemote(purchaseCode: String, parkID: Int, token: String, isSuccess: (photosList: VisitPark?, error:Throwable?) -> Unit){
        val jsonObject = JsonObject()
        jsonObject.addProperty("JoinedCode", purchaseCode)
        jsonObject.addProperty("ParkID", parkID)
        try {
            confirmSecurity {
                apiPhotoService.addVisitPark(token, jsonObject)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<VisitPark>() {
                        override fun onSuccess(photosList: VisitPark) {
                            isSuccess(photosList, null)
                            //photoUserRepository?.updateAlbumForUnlock(purchaseCode, parkID, photoGallery, albumGallery)
                        }

                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess(null, e)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null, e)
        }
    }
    @SuppressLint("CheckResult")
    fun sendUrlRemote(purchaseCode: String, visitorEmail: String?, token: String, isSuccess: (urlMediaResponse: UrlCode?) -> Unit){
        val jsonObject = JsonObject()
        jsonObject.addProperty("JoinedCode", purchaseCode)
        jsonObject.addProperty("VisitorEmail", visitorEmail)
        jsonObject.addProperty("Shipping", true)
        jsonObject.addProperty("Lang", LocaleHelper.getLocale(HotelXcaretApp.mContext).language.uppercase())
        try {
            confirmSecurity {
                apiPhotoService.sendUrlCode(token, jsonObject)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object : DisposableSingleObserver<UrlCode>() {
                        override fun onSuccess(response: UrlCode) {
                            isSuccess(response)
                            //photoUserRepository?.updateAlbumForUnlock(purchaseCode, parkID, photoGallery, albumGallery)
                        }

                        override fun onError(e: Throwable) {
                            Log.d("API", "Conection fail ${e.message}")
                            isSuccess(null)
                        }
                    })
            }
        }catch (e: Exception){
            isSuccess(null)
        }
    }
    fun updateAlbum(purchaseCode: String, parkID: Int, photosList: GetPhotos, photoGallery: List<PhotoCodes>?, albumGallery: List<AlbumList>?){
        photoUserRepository?.updateAlbumTotalMedia(purchaseCode, parkID, photosList.totalPhotos ?: 0, photoGallery, albumGallery)
    }

    fun updateAlbumUnlock(purchaseCode: String, parkID: Int, photoGallery: List<PhotoCodes>?, albumGallery: List<AlbumList>?){
        photoUserRepository?.updateAlbumForUnlock(purchaseCode, parkID, photoGallery, albumGallery)
    }

    //region Save/Share actions
    fun updateAlbumTotalMedia(purchaseCode: String, albumUID: String, shareFiles: ArrayList<ShareFile> ){
        photoUserRepository?.shareLogPhotoAction(purchaseCode, albumUID, shareFiles)
    }

    fun saveLogPhotoAction(purchaseCode: String, albumUID: String, photo: Photo ){
        photoUserRepository?.saveLogPhotoAction(purchaseCode, albumUID, photo)
    }
}