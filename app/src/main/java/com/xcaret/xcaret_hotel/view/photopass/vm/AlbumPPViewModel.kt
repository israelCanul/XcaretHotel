package com.xcaret.xcaret_hotel.view.photopass.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.data.usecase.ParamSettingUseCase

import com.xcaret.xcaret_hotel.view.photopass.domain.*
import com.xcaret.xcaret_hotel.view.photopass.data.entity.AlbumListEntity
import com.xcaret.xcaret_hotel.view.photopass.data.entity.PhotoCodesEntity
import com.xcaret.xcaret_hotel.view.photopass.data.entity.PhotoUserAlbumsEntity


import java.util.*

class AlbumPPViewModel : ViewModel() {


    val valid = MutableLiveData<Boolean>()
    val book = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    val token = MutableLiveData<String>()


    val albuma: MutableLiveData<List<AlbumList>> = MutableLiveData()

    /*Label in Home Park*/
    val lblLeyendNotAlbums = MutableLiveData<String>()
    val lblPlaceHolderCode = MutableLiveData<String>()
    val lblNotePhoto = MutableLiveData<String>()
    val lblInfoNotePhoto = MutableLiveData<String>()
    val lblTitleAlbums = MutableLiveData<String>()




    //API Entity



}


