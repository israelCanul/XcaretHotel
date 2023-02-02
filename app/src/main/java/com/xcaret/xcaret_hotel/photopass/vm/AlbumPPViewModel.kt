package com.xcaret.xcaret_hotel.photopass.vm

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.xcaret.xcaret_hotel.data.usecase.ParamSettingUseCase
import com.xcaret.xcaret_hotel.domain.LangLabel
import com.xcaret.xcaret_hotel.domain.ParamSetting
import com.xcaret.xcaret_hotel.photopass.data.api.Album
import com.xcaret.xcaret_hotel.photopass.data.api.Code
import com.xcaret.xcaret_hotel.photopass.data.api.SelectedPark

import com.xcaret.xcaret_hotel.photopass.domain.*
import com.xcaret.xcaret_hotel.photopass.data.entity.AlbumListEntity
import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoCodesEntity
import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoLogin
import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoUserAlbumsEntity
import com.xcaret.xcaret_hotel.photopass.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.photopass.data.usecase.DefaultPhotoLangLabelUseCase
import com.xcaret.xcaret_hotel.photopass.data.usecase.PhotoCodesUseCase
import com.xcaret.xcaret_hotel.photopass.data.usecase.PhotoLangLabelUseCase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat


import java.util.*

class AlbumPPViewModel : ViewModel() {


    var photo = MutableLiveData<List<PhotoCodes>>()
    var album = MutableLiveData<List<AlbumList>>()

    val valid = MutableLiveData<Boolean>()
    val book = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val loadApiError = MutableLiveData<Boolean>()
    val messageError = MutableLiveData<String>()
    val repeatPhotoCode = MutableLiveData<Boolean>()
    val token = MutableLiveData<String>()

    private var selectedParks = ArrayList<Int>()

    //API ROOM
    private val photoCodesEntity = PhotoCodesEntity()
    private var albumChildrens = ArrayList<AlbumListEntity>()
    private val photoUserAlbumsEntity = PhotoUserAlbumsEntity()

    val photoa: MutableLiveData<List<PhotoCodes>> = MutableLiveData()
    val albuma: MutableLiveData<List<AlbumList>> = MutableLiveData()

    /*Label in Home Park*/
    val lblLeyendNotAlbums = MutableLiveData<String>()
    val lblPlaceHolderCode = MutableLiveData<String>()
    val lblNotePhoto = MutableLiveData<String>()
    val lblInfoNotePhoto = MutableLiveData<String>()
    val lblTitleAlbums = MutableLiveData<String>()
    val txtAlertLydNotCode = MutableLiveData<String>()
    val lblNoPhotos = MutableLiveData<String>()
    val lblVisitDate = MutableLiveData<String>()
    val lblIncluded = MutableLiveData<String>()
    val lblIydHeaderAlbum = MutableLiveData<String>()
    val lblLydHeaderAlbumComplete = MutableLiveData<String>()
    val lblUnlockPark = MutableLiveData<String>()
    val lblCompletedAlbums = MutableLiveData<String>()
    val btnTabAlbum = MutableLiveData<String>()
    val btnTabShare = MutableLiveData<String>()
    val btnTabSendHd = MutableLiveData<String>()
    val lblTitlePhotosByAlbum = MutableLiveData<String>()
    val lblTitleSelectPhotos = MutableLiveData<String>()

    val lblValidateDayPhotopass = MutableLiveData<String>()
    val lblValidateDayPhotopassDownload = MutableLiveData<String>()
    val lblValidatePhotopassa = MutableLiveData<String>()
    val lblValidatePhotopassb = MutableLiveData<String>()
    val lblExpiratePhotopass = MutableLiveData<String>()

    val labelPpAlbum: List<MutableLiveData<String>> = listOf(lblLeyendNotAlbums, lblPlaceHolderCode, lblNotePhoto, lblInfoNotePhoto, lblTitleAlbums,
        lblValidateDayPhotopass,
        lblValidateDayPhotopassDownload,
        lblValidatePhotopassa,
        lblValidatePhotopassb,
        lblExpiratePhotopass
    )

    //API Entity
    private val photoCodesUseCase = PhotoCodesUseCase()
    private val paramSettingUseCase = ParamSettingUseCase()

    /*langs*/
    val defaultLangUseCase = PhotoLangLabelUseCase()


    fun getDefaultLangByLabel(): LiveData<List<LangLabel>> = defaultLangUseCase.getLangByLabelLive("en", keysLabelPpAlbum)

    //Id photopass restringed
    fun findParamSettingByCode(code: String): ParamSetting? = paramSettingUseCase.findParamSettingByCode(code)
    fun login(isSuccess: (token: PhotoLogin?, error: Throwable?) -> Unit) = photoCodesUseCase.fetchLoginFromRemote(isSuccess)

    fun getPhotoUser(listener: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<String>) = photoCodesUseCase.getPhotoUser(listener)

    //region Query by ROOM
    fun getPhoto(): LiveData<List<PhotoCodes>> = photoCodesUseCase.getAllPhoto()
    fun getAlbum(): LiveData<List<AlbumList>> =  photoCodesUseCase.getAllAlbum()
    fun getAllAlbumNotData(): List<AlbumList>? =  photoCodesUseCase.getAllAlbumNotData()
    fun getAlbumByCode(code:String): LiveData<List<AlbumList>> = photoCodesUseCase.getAlbumByCode(code)

    fun updateAlbumDate (purchaseCode: String){
        doAsync {
            photoCodesUseCase.fetchCodeFromRemote(purchaseCode, token.value ?: ""){ photoList, error->
                photoList?.let {
                    getCodeAlbumDate(it, purchaseCode)
                }?: kotlin.run {
                    uiThread {
                        loadApiError.value = true
                        messageError.value = "Conection fail ${error?.message}"
                        loading.postValue(false)
                        //loading.value = false
                        error?.printStackTrace()
                    }
                }
            }
        }
    }
    private fun getCodeAlbumDate(photoList: Code, photoCodes: String) {
        doAsync {
            photoList.let { code ->
                photoCodesEntity.code = photoCodes
                photoCodesEntity.expiresDate = code.expiresDate
                photoCodesEntity.visitDate = code.visitDate
                photoCodesEntity.valid = code.isValid
                PhotoCodesUseCase().photoUserRepository?.updateAlbumData(photoCodesEntity)
                uiThread { loading.value = false }
            }
        }
    }
    fun fetchCodeFromRemote(purchaseCode: String) {
        doAsync {
            photoCodesUseCase.fetchCodeFromRemote(purchaseCode, token.value ?: ""){ photoList, error->
                photoList?.let {
                    Log.i("Inside fetchCodeFromRemote", it.toString())
                    getCode(it, purchaseCode)
                }?: kotlin.run {
                    uiThread {
                        loadApiError.value = true
                        messageError.value = "Conection fail ${error?.message}"
                        loading.value = false
                        error?.printStackTrace()
                    }
                }
            }
        }
    }
    private fun fetchAlbumFromRemote(purchaseCode: String) {
        doAsync {
            photoCodesUseCase.fetchAlbumFromRemote(purchaseCode, token.value ?: ""){ albumList, error->
                albumList?.let {
                    Log.i("Inside fetchAlbumFromRemote", it.toString())
                    getAlbum(it, purchaseCode)
                }?: kotlin.run {
                    uiThread {
                        loadApiError.value = true
                        messageError.value = "Conection fail ${error?.message}"
                        loading.value = false
                        error?.printStackTrace()
                    }
                }
            }
        }
    }
    private fun fetchSelectedParksFromRemote(purchaseCode: String) {
        doAsync {
            photoCodesUseCase.fetchSelectedParksFromRemote(purchaseCode, token.value ?: ""){ selectedParkList, error->
                selectedParkList?.let {
                    Log.i("Inside fetchSelectedParksFromRemote", it.toString())
                    getSelectedParks(it, purchaseCode)
                }?: kotlin.run {
                    uiThread {
                        loadApiError.value = true
                        messageError.value = "Conection fail ${error?.message}"
                        loading.value = false
                        error?.printStackTrace()
                    }
                }
            }
        }
    }
    private fun fetchPhotoStatusFromRemote(DownloadCode: String, parkID: Int, visitDate: String?) {
        doAsync {
            photoCodesUseCase.fetchPhotoStatusFromRemote(DownloadCode, parkID, token.value ?: ""){ photoStatus, error->
                photoStatus?.let {
                    getPhotoStatus(it.total, parkID, visitDate)
                }?: kotlin.run {
                    uiThread {
                        loadApiError.value = true
                        messageError.value = "Conection fail ${error?.message}"
                        loading.value = false
                        error?.printStackTrace()
                    }
                }
            }
        }
    }
    private fun getCode(photoList: Code, photoCodes: String) {
        doAsync {
            photoList.let { code ->
                if (code.isValid == true) {
                    photoCodesEntity.code = photoCodes
                    @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
                    photoCodesEntity.dateRegister = dateFormat.format(Date())
                    photoCodesEntity.expiresDate = code.expiresDate
                    photoCodesEntity.visitDate = code.visitDate
                    photoCodesEntity.valid = code.isValid
                    if (code.isBook == true) {
                        uiThread {
                            photoCodesEntity.book = true
                            photoCodesEntity.totalPurchase = code.availableParks
                        }

                        fetchSelectedParksFromRemote(photoCodes)
                    } else {
                        uiThread {
                            photoCodesEntity.book = false
                            photoCodesEntity.totalPurchase = 1
                            photoCodesEntity.totalUnlock = 1
                        }
                        fetchAlbumFromRemote(photoCodes)
                    }
                    uiThread {
                        valid.value = true
                    }
                } else {
                    photoCodesEntity.valid = code.isValid
                    uiThread {
                        valid.value = false
                        loading.value = false
                    }
                }
            }
        }
    }
    private fun getAlbum(albumList: List<Album>, photoCodes: String) {
        doAsync {
            albumList.let {
                albumChildrens.clear()
                val cant = it.size
                for (i in 0 until cant) {
                    val parkID = it[i].parkId
                    val visitDate = it[i].visitDate
                    val totalMedia = it[i].totalMedia
                    var unlock = false

                    if (photoCodesEntity.book) {
                        if (parkID != 8 && parkID != 11) {
                            val cantselect = selectedParks.size
                            for (j in 0 until cantselect) {
                                if (selectedParks[j] == parkID) {
                                    unlock = true
                                    break
                                }
                            }
                        } else {
                            unlock = true
                        }
                        val albumListEntity = AlbumListEntity()
                        albumListEntity.parkID = parkID
                        albumListEntity.totalMedia = it[i].totalMedia
                        albumListEntity.visitDate = visitDate
                        albumListEntity.unlock = unlock

                        albumChildrens.add(albumListEntity)
                    } else {
                        getPhotoStatus(totalMedia, parkID, visitDate)
                    }
                } // end for
                if (photoCodesEntity.book) {
                    if (!validatePhotoUserRoom(photoCodesEntity)) {
                        uiThread {
                            repeatPhotoCode.value = false
                        }
                        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
                        photoUserAlbumsEntity.dateRegister = dateFormat.format(Date())
                        photoUserAlbumsEntity.uid = photoCodesEntity.code.toString()

                        //Llenar datos en Firebase
                        val find = validatePhotoCodeRoom(photoCodesEntity.code ?: "")
                        if (find != null) {
                            PhotoCodesUseCase().photoUserRepository?.updatePhotoCode(photoCodesEntity, albumChildrens)
                        } else {
                            PhotoCodesUseCase().photoUserRepository?.savePhotoCode(photoCodesEntity, albumChildrens)
                        }
                        PhotoCodesUseCase().photoUserRepository?.savePhotoUsers(photoUserAlbumsEntity)

                        val bundle = Bundle()
                        val totalPurchase = photoCodesEntity.totalPurchase!!
                        val totalUnlock = photoCodesEntity.totalUnlock!!
                        val left = totalPurchase - totalUnlock

                        bundle.putInt("totalPurchase", totalPurchase)
                        bundle.putInt("left", left)
                        bundle.putInt("unlock", totalUnlock)
                        //DialogPurchaseFragment.newInstance(bundle)//TODO: Implementar este fragment

                        //Add fb Logs
                        //fireBaseLogs?.logEvent("saveAlbum", "Fotos")

                        //Valida Book
                        uiThread {
                            book.value = true
                        }

                    } else {
                        uiThread {
                            repeatPhotoCode.value = true
                        }
                    }

                    uiThread {
                        loading.value = false
                    }

                }
            }
        }
    }

    private fun getSelectedParks(selectedPark: List<SelectedPark>, photoCodes: String) {
        doAsync {
            selectedPark.let {
                selectedParks = java.util.ArrayList()
                val cant = it.size
                var totalunlock = 0
                for (i in 0 until cant) {
                    it[i].parkId?.let { id ->
                        selectedParks.add(id)
                    }
                    totalunlock++
                }
                photoCodesEntity.totalUnlock = totalunlock
                fetchAlbumFromRemote(photoCodes)
            }
        }
    }
    private fun getPhotoStatus(totalMedia: Int?, parkID: Int?, visitDate: String?) {  //change photoStatus: PhotoStatus per totalMedia
        doAsync {
            val unlock = true
            val albumChild = AlbumListEntity()

            albumChild.parkID = parkID
            albumChild.totalMedia = totalMedia
            albumChild.visitDate = visitDate
            albumChild.unlock = unlock

            albumChildrens.add(albumChild)

            if (!validatePhotoUserRoom(photoCodesEntity)) {
                @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
                photoUserAlbumsEntity.dateRegister = dateFormat.format(Date())
                photoUserAlbumsEntity.uid = photoCodesEntity.code ?: ""
                //Llenar datos en Firebase
                val find = validatePhotoCodeRoom(photoCodesEntity.code ?: "")
                if (find != null) {
                    PhotoCodesUseCase().photoUserRepository?.updatePhotoCode(photoCodesEntity, albumChildrens)
                } else {
                    PhotoCodesUseCase().photoUserRepository?.savePhotoCode(photoCodesEntity, albumChildrens)
                }
                PhotoCodesUseCase().photoUserRepository?.savePhotoUsers(photoUserAlbumsEntity)

                //Add fb Logs
                //fireBaseLogs?.logEvent("saveAlbum", "Fotos")

                uiThread {
                    book.value = false
                }

            } else {
                uiThread {
                    repeatPhotoCode.value = true
                }
            }

            uiThread {
                loading.value = false
            }
        }
    }
    private fun validatePhotoUserRoom(photoCodesEntity: PhotoCodesEntity): Boolean {
        var result = false
        photo.value.let {
            it?.forEach { photoCodes ->
                if (photoCodesEntity.code == photoCodes.uid)
                    result = true
            }
        }
        return result
    }

    private fun validatePhotoCodeRoom(code: String): PhotoCodes? {
        var result: PhotoCodes? = null
        photo.value.let {
            it?.forEach { photoCodes ->
                if (code == photoCodes.code)
                    result = photoCodes
            }
        }
        return result
    }
    fun validateAlbumListRoom(parkId: Int?, code: String?): AlbumList? {
        var result: AlbumList? = null
        album.value.let {
            it?.forEach { albumList ->
                if(code == albumList.code && parkId == albumList.parkID)
                    result = albumList
            }
        }
        return result
    }
    companion object{
        val keysLabelPpAlbum: List<String> = listOf("lbl_leyend_not_albums",
            "lbl_place_holder_code",
            "lbl_note_photo",
            "lbl_info_note_photo",
            "lbl_title_albums",
            "lbl_validate_day_photopass",
            "lbl_validate_day_photopass_download",
            "lbl_validate_photopassa",
            "lbl_validate_photopassb",
            "lbl_expirate_photopass")
    }
    open fun getLabelDefault(labelXCHome: List<MutableLiveData<String>>, keysLabelXCHome: List<String>, list: List<LangLabel>){
        labelXCHome.forEachIndexed{ index, a ->
            a.postValue(list.firstOrNull() {
//                Log.i("LABEL", it.lbl_key.toString());
                it.parent_uid == keysLabelXCHome[index]
            }?.value ?: "")
        }
    }
}


