package com.xcaret.xcaret_hotel.data.usecase

import android.annotation.SuppressLint
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.api.SalesForceApiService
import com.xcaret.xcaret_hotel.data.entity.*
import com.xcaret.xcaret_hotel.domain.SalesForceContact
import com.xcaret.xcaret_hotel.domain.User
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Session
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class SalesForceUseCase {

    private val salesForceApi = SalesForceApiService()

    fun getOrCreateProfileSF(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", user: User, response: (result: User) -> Unit){
        try {
            getpaxprofilerq(idToken, user.email) { getPaxResponse ->
                if (getPaxResponse.COUNT == 0) {
                    user.como_se_entero = Constants.COMO_SE_ENTERO_DEFAULT
                    paxProfileRQ(idToken, user.toRequest()) { response ->
                        if(response.SUCCESS == true){
                            user.success = true
                            user.salesForceId = response.CONTACT?.element?.get(0)?.ID ?: ""
                        }
                        response(user)
                    }
                } else if (getPaxResponse.COUNT > 0){
                    getPaxResponse.CONTACT?.let { salesForceElementGenericEntity ->
                        val cognitoId = salesForceElementGenericEntity.ELEMENT?.get(0)?.EXTERNAL_ID__C ?: ""
                        user.salesForceId = salesForceElementGenericEntity.ELEMENT?.get(0)?.ID ?: ""
                        user.firstName = salesForceElementGenericEntity.ELEMENT?.get(0)?.FIRSTNAME ?: ""
                        user.lastName = salesForceElementGenericEntity.ELEMENT?.get(0)?.LASTNAME ?: ""
                        user.phone = salesForceElementGenericEntity.ELEMENT?.get(0)?.PHONE ?: ""
                        user.name = salesForceElementGenericEntity.ELEMENT?.get(0)?.NAME ?: ""
                        user.cp = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGPOSTALCODE ?: ""
                        user.country_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGCOUNTRYCODEISO__C ?: ""
                        user.state_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGSTATECODEISO__C ?: ""
                        user.address = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGSTREET ?: ""
                        //user.cognitoId = salesForceElementGenericEntity.ELEMENT?.get(0)?.EXTERNAL_ID__C.isNullOrEmpty() ?: ""
                        if(cognitoId.trim().isNotEmpty()) user.cognitoId = cognitoId
                        user.notify_promotions = salesForceElementGenericEntity.ELEMENT?.get(0)?.NOTIFICACION_NOTICIAS__C == true
                        user.city = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGCITY ?: ""
                        user.title_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.TITLE ?: ""
                        user.como_se_entero = salesForceElementGenericEntity.ELEMENT?.get(0)?.COMO_SE_ENTERO_DE_NOSOTROS__C ?: ""
                        user.success = true
                    }
                    response(user)
                }else {
                    user.success = false
                    response(user)
                }
            }
        }catch (e: Exception){
            user.success = false
            response(user)
        }
    }

    fun getProfile(email: String, response: (result: User) -> Unit){
        try{
            getpaxprofilerq(request = email){ getPaxResponse ->
                if (getPaxResponse.COUNT == 0) {
                    response(User(success = false))
                } else{
                    val user = User(success = true)
                    getPaxResponse.CONTACT?.let { salesForceElementGenericEntity ->
                        val cognitoId = salesForceElementGenericEntity.ELEMENT?.get(0)?.EXTERNAL_ID__C ?: ""
                        user.salesForceId = salesForceElementGenericEntity.ELEMENT?.get(0)?.ID ?: ""
                        user.firstName = salesForceElementGenericEntity.ELEMENT?.get(0)?.FIRSTNAME ?: ""
                        user.lastName = salesForceElementGenericEntity.ELEMENT?.get(0)?.LASTNAME ?: ""
                        user.phone = salesForceElementGenericEntity.ELEMENT?.get(0)?.PHONE ?: ""
                        user.name = salesForceElementGenericEntity.ELEMENT?.get(0)?.NAME ?: ""
                        user.cp = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGPOSTALCODE ?: ""
                        user.country_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGCOUNTRYCODEISO__C ?: ""
                        user.state_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGSTATECODEISO__C ?: ""
                        user.address = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGSTREET ?: ""
                        user.city = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGCITY ?: ""
                        user.notify_promotions = salesForceElementGenericEntity.ELEMENT?.get(0)?.NOTIFICACION_NOTICIAS__C == true
                        if(cognitoId.trim().isNotEmpty()) user.cognitoId = cognitoId
                        user.title_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.TITLE ?: ""
                        user.como_se_entero = salesForceElementGenericEntity.ELEMENT?.get(0)?.COMO_SE_ENTERO_DE_NOSOTROS__C ?: ""
                    }
                    response(user)
                }
            }
        }
        catch (e: Exception){
            response(User(success = null))
        }
    }

    fun getpaxprofilerq(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", request: String, result: (response: SalesForceGetProfileEntity) -> Unit){
        try{
            salesForceApi.getpaxprofilerq(idToken, request)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<SalesForceGetProfileEntity>(){
                    override fun onError(e: Throwable) {
                        result(getPaxProfileError(e.localizedMessage))
                    }

                    override fun onSuccess(t: SalesForceGetProfileEntity) {
                        result(t)
                    }
                })
        }
        catch (e: Exception){
            result(getPaxProfileError(e.localizedMessage))
        }
    }

    fun getpaxprofilerqExternalId(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", request: String, result: (response: SalesForceGetProfileEntity) -> Unit){
        try{
            salesForceApi.getpaxprofilerqExternalId(idToken, request)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<SalesForceGetProfileEntity>(){
                    override fun onError(e: Throwable) {
                        result(getPaxProfileError(e.localizedMessage))
                    }

                    override fun onSuccess(t: SalesForceGetProfileEntity) {
                        result(t)
                    }
                })
        }
        catch (e: Exception){
            result(getPaxProfileError(e.localizedMessage))
        }
    }


    private fun getPaxProfileError(error: String?): SalesForceGetProfileEntity{
        val res = SalesForceGetProfileEntity()
        res.SUCCESS = null
        res.MESSAGE = error ?: ""
        return res
    }

    fun paxProfileRQ(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", request: JsonObject, result: (response: SalesForcePaxProfileEntity) -> Unit){
        try{
            salesForceApi.paxprofilerq(idToken, request)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.computation())
                .subscribeWith(object: DisposableSingleObserver<SalesForcePaxProfileEntity>(){
                    override fun onError(e: Throwable) {
                        result(SalesForcePaxProfileEntity(SUCCESS = null))
                    }

                    override fun onSuccess(t: SalesForcePaxProfileEntity) {
                        result(t)
                    }
                })
        }catch (e: Exception){
            result(SalesForcePaxProfileEntity(SUCCESS = null))
        }
    }

    fun getOrCreateProfileSFByExternalId(idToken: String = Session.getToken(HotelXcaretApp.mContext) ?: "", user: User?, response: (result: User?) -> Unit){
        try {
            getpaxprofilerqExternalId(idToken, user?.cognitoId.toString()) { getPaxResponse ->
                if (getPaxResponse.COUNT == 0) {
                    user?.como_se_entero = Constants.COMO_SE_ENTERO_DEFAULT
                    paxProfileRQ(idToken, user?.toRequest()!!) { response ->
                        if(response.SUCCESS == true){
                            user?.success = true
                            user?.salesForceId = response.CONTACT?.element?.get(0)?.ID ?: ""
                        }
                        response(user)
                    }
                } else if (getPaxResponse.COUNT > 0){
                    getPaxResponse.CONTACT?.let { salesForceElementGenericEntity ->
                        val cognitoId = salesForceElementGenericEntity.ELEMENT?.get(0)?.EXTERNAL_ID__C ?: ""
                        user?.salesForceId = salesForceElementGenericEntity.ELEMENT?.get(0)?.ID ?: ""
                        user?.firstName = salesForceElementGenericEntity.ELEMENT?.get(0)?.FIRSTNAME ?: ""
                        user?.lastName = salesForceElementGenericEntity.ELEMENT?.get(0)?.LASTNAME ?: ""
                        user?.phone = salesForceElementGenericEntity.ELEMENT?.get(0)?.PHONE ?: ""
                        user?.name = salesForceElementGenericEntity.ELEMENT?.get(0)?.NAME ?: ""
                        user?.cp = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGPOSTALCODE ?: ""
                        user?.country_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGCOUNTRYCODEISO__C ?: ""
                        user?.state_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGSTATECODEISO__C ?: ""
                        user?.address = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGSTREET ?: ""
                        //user.cognitoId = salesForceElementGenericEntity.ELEMENT?.get(0)?.EXTERNAL_ID__C.isNullOrEmpty() ?: ""
                        if(cognitoId.trim().isNotEmpty()) user?.cognitoId = cognitoId
                        user?.notify_promotions = salesForceElementGenericEntity.ELEMENT?.get(0)?.NOTIFICACION_NOTICIAS__C == true
                        user?.city = salesForceElementGenericEntity.ELEMENT?.get(0)?.MAILINGCITY ?: ""
                        user?.title_code = salesForceElementGenericEntity.ELEMENT?.get(0)?.TITLE ?: ""
                        user?.como_se_entero = salesForceElementGenericEntity.ELEMENT?.get(0)?.COMO_SE_ENTERO_DE_NOSOTROS__C ?: ""
                        user?.success = true
                    }
                    response(user)
                }else {
                    user?.success = false
                    response(user)
                }
            }
        }catch (e: Exception){
            user?.success = false
            response(user)
        }
    }
}