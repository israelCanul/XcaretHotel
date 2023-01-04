package com.xcaret.xcaret_hotel.photopass.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.data.usecase.ParamSettingUseCase
import com.xcaret.xcaret_hotel.domain.ParamSetting

import com.xcaret.xcaret_hotel.photopass.domain.*
import com.xcaret.xcaret_hotel.photopass.data.entity.AlbumListEntity
import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoCodesEntity
import com.xcaret.xcaret_hotel.photopass.data.entity.PhotoUserAlbumsEntity
import com.xcaret.xcaret_hotel.photopass.data.usecase.PhotoCodesUseCase


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


    //Id photopass restringed
    fun findParamSettingByCode(code: String): ParamSetting? = paramSettingUseCase.findParamSettingByCode(code)




}


