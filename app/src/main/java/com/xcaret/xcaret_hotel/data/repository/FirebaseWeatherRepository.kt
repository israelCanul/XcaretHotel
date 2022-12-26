package com.xcaret.xcaret_hotel.data.repository

import com.xcaret.xcaret_hotel.data.mapper.WeatherMapper
import com.xcaret.xcaret_hotel.domain.Weather
import com.xcaret.xcaret_hotel.view.config.LogHX

class FirebaseWeatherRepository(val placeUID: String): FirebaseDatabaseRepository<Weather>() {

    private var rootNode = FirebaseReference.WEATHER;

    init {
        this.rootNode += "/$placeUID"
        LogHX.d("root", rootNode)
        init(WeatherMapper())

        setReferenceQuery(getReference().orderByKey().limitToLast(LIMIT))
    }

    override fun getRootNode(): String = rootNode

    override fun addListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Weather>) {
        addListenerForQuery(object: FirebaseDatabaseRepositoryCallback<Weather>{
            override fun onError(e: Exception) {
                firebaseCallback.onError(e)
            }

            override fun onSuccess(result: MutableList<Weather>) {
                result.forEachIndexed { index, _ ->
                    result[index].place_uid = placeUID
                }
                firebaseCallback.onSuccess(result)
            }
        })
    }

    companion object{
        private const val LIMIT = 8
    }
}