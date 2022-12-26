package com.xcaret.xcaret_hotel.view.menu.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.Country
import com.xcaret.xcaret_hotel.domain.State
import com.xcaret.xcaret_hotel.domain.Title
import com.xcaret.xcaret_hotel.view.config.Language

class DropDownViewModel: ViewModel() {
    private val langTitleUseCase: LangTitleUseCase by lazy { LangTitleUseCase() }
    private val countryUseCase: CountryUseCase by lazy { CountryUseCase() }
    private val statUserUseCase: StateUseCase by lazy { StateUseCase() }
    private val airlineUseCase: AirlineUseCase by lazy { AirlineUseCase() }
    private val airlineTerminalUseCase: AirlineTerminalUseCase by lazy { AirlineTerminalUseCase() }

    val countrySelectedLiveData = MutableLiveData<Country>()
    val stateSelectedLiveData = MutableLiveData<State>()
    val titleSelectedLiveData = MutableLiveData<Title>()

    fun all(lang: String = Language.getLangCode(HotelXcaretApp.mContext)) = langTitleUseCase.all(lang)
    fun allCountries() = countryUseCase.all()
    fun allStatesByCountryId() = statUserUseCase.allByCountryId(countrySelectedLiveData.value?.id ?: -1)
    fun allAirlines() = airlineUseCase.all()
    fun allAirlineTerminal() = airlineTerminalUseCase.all()
}