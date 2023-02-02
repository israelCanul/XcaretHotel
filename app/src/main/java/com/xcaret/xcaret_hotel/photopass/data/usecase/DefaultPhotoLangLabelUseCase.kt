package com.xcaret.xcaret_hotel.photopass.data.usecase

import android.util.Log
import androidx.lifecycle.LiveData
import com.xcaret.xcaret_hotel.photopass.data.repository.DefaultPhotoLangLabelRepository
import com.xcaret.xcaret_hotel.photopass.data.repository.SingleFirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.photopass.data.room.dao.BasePhotoDao
import com.xcaret.xcaret_hotel.photopass.domain.DefaultPhotoLangLabel


class DefaultPhotoLangLabelUseCase: BasePhotoUseCase<DefaultPhotoLangLabel>() {
    private val dao = database.defaultPhotoLangLabelDao()
    private val repository = DefaultPhotoLangLabelRepository()
    fun getFromFirebase(listener: SingleFirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<DefaultPhotoLangLabel>){
        repository.addListener(listener)
    }
    fun getFromFirebaseAndSave(){
        getFromFirebase(object: SingleFirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<DefaultPhotoLangLabel>{
            override fun onError(e: Exception) {
                e.printStackTrace()
            }
            override fun onSuccess(result: MutableList<DefaultPhotoLangLabel>) {
                insert(result as MutableList<DefaultPhotoLangLabel>){
                    Log.i("lbl_store isertados", it.toString())
                }
            }
            override fun onRemove(item: DefaultPhotoLangLabel) {
                removeBy(item)
            }
        })
    }
    override fun insertAll(list: MutableList<*>) {}
    override fun insertAllFromList(list: MutableList<*>) {}

//    override fun insertAllFromList(list: MutableList<*>) {
//        insert(list as MutableList<DefaultPhotoLangLabel>){
//            Log.i("lbl_store isertados", it.toString())
//        }
//        list.forEach {
//            Log.i("DefaultLang",it.toString())
//        }
//    }

    override fun removeBy(data: Any) {}
    override fun getDao(): BasePhotoDao<DefaultPhotoLangLabel> = dao
    fun getDefaultLangByLabelLive(lang: String, label: List<String>): LiveData<List<DefaultPhotoLangLabel>> = dao.getDefaultLangByLabelLive(label)
}