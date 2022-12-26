package com.xcaret.xcaret_hotel.data.usecase

import android.annotation.SuppressLint
import com.xcaret.xcaret_hotel.data.api.CognitoApiService
import com.xcaret.xcaret_hotel.data.entity.CognitoValidUserEntity
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.LogHX
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync

class CognitoUseCase{

    private val tag = "CognitoUseCase"
    private val cognitoApi = CognitoApiService()


    @SuppressLint("CheckResult")
    fun validateUser(username: String, password: String, response: (result: CognitoValidUserEntity?) -> Unit){
        try{
            cognitoApi
                .validateUser(username, password)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<CognitoValidUserEntity>() {
                    override fun onError(e: Throwable) {
                        LogHX.e("$tag - validateUser", e.localizedMessage ?: "")
                        response(CognitoValidUserEntity(SUCCESS = false, MESSAGE = e.message))
                    }

                    override fun onSuccess(t: CognitoValidUserEntity) {
                        LogHX.i("$tag - validateUser", t.toString())
                        response(t)
                    }
                })
        }
        catch (e: Exception){
            LogHX.e("$tag - validateUser", e.localizedMessage ?: "")
            response(CognitoValidUserEntity(SUCCESS = false))
        }
    }

    fun getuser(username: String, response: (result: CognitoValidUserEntity?) -> Unit){
        try {
            cognitoApi
                .getUser(username)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<CognitoValidUserEntity>() {
                    override fun onError(e: Throwable) {
                        LogHX.e("$tag - getUserInfo", e.localizedMessage ?: "")
                        response(CognitoValidUserEntity(SUCCESS = false, MESSAGE = e.message))
                    }

                    override fun onSuccess(t: CognitoValidUserEntity) {
                        LogHX.i("$tag - getUserInfo", t.toString())
                        response(t)
                    }
                })

        }catch (e:Exception){
            LogHX.e("$tag - GetUser", e.localizedMessage ?: "")
            response(CognitoValidUserEntity(SUCCESS = false))
        }
    }

}