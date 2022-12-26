package com.xcaret.xcaret_hotel.data.usecase

import com.xcaret.xcaret_hotel.data.repository.FirebaseDatabaseRepository
import com.xcaret.xcaret_hotel.domain.Reservation
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.config.LogHX

class SecurityUseCase {

    private val tag = "SecurityUseCase"
    private var numObjectDownloaded = 0

    private val userUseCase: UserUseCase by lazy { UserUseCase() }
    private val reservationUseCase: ReservationsUseCase by lazy { ReservationsUseCase() }

    fun startDownload(ok: () -> Unit){
        downloadInfoByUser(ok)
    }

    private fun downloadInfoByUser(ok: () -> Unit){
        val callbackReservation = object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Reservation>{
            override fun onSuccess(result: MutableList<Reservation>) {
                reservationUseCase.insert(result){
                    notifyDownload(ok)
                }
            }

            override fun onError(e: Exception) {
                notifyDownload(ok)
                LogHX.e("$tag-reservationsUseCase", e.localizedMessage)
            }

        }

        val callbackUser = object: FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<User>{
            override fun onSuccess(result: MutableList<User>) {
                userUseCase.insert(result){
                    notifyDownload(ok)
                }
            }

            override fun onError(e: Exception) {
                notifyDownload(ok)
                LogHX.e("$tag-userUseCase", e.localizedMessage)
            }

        }

        reservationUseCase.getFromFirebase(callbackReservation)
        userUseCase.getFromFirebase(callbackUser)
    }

    private fun notifyDownload(ok: () -> Unit){
        numObjectDownloaded ++
        LogHX.i("Downloader", "$numObjectDownloaded to ${SplashUseCase.NUM_OBJECT_TO_DOWNLOAD}")
        if(numObjectDownloaded == NUM_OBJECT_TO_DOWNLOAD)
            ok()
    }

    companion object{
        const val NUM_OBJECT_TO_DOWNLOAD = 2
    }
}