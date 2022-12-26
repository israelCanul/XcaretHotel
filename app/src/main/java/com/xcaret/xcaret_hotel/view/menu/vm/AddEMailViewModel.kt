package com.xcaret.xcaret_hotel.view.menu.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xcaret.xcaret_hotel.data.usecase.UserUseCase
import com.xcaret.xcaret_hotel.domain.UserValidError

class AddEMailViewModel: ViewModel() {

    private val userUseCase: UserUseCase by lazy { UserUseCase() }

    fun isValidAddEmail(email: String, confirmEmail: String): UserValidError = userUseCase.isValidAddEmail(email, confirmEmail)
}