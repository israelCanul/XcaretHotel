package com.xcaret.xcaret_hotel.view.quote.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.GalleryUseCase
import com.xcaret.xcaret_hotel.data.usecase.RoomLocationUseCase
import com.xcaret.xcaret_hotel.domain.Gallery
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.config.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomDetailDialogViewModel: ViewModel() {
    private val roomLocation = RoomLocationUseCase()
    private val galleryUseCase = GalleryUseCase()


    val galleryList = MutableLiveData<List<Gallery?>>()

    fun getLocations(roomUID: String, lang: String = Language.getLangCode(HotelXcaretApp.mContext)): LiveData<List<Place>> = roomLocation.getLocations(roomUID, lang)
    fun getImages(parentUID:String){
        viewModelScope.launch {
            val lst = withContext(Dispatchers.IO){
                galleryUseCase.getImagesFromGallery(parentUID)
            }
            if (lst.isNotEmpty()){
                galleryList.value = lst
            }
        }
    }


}