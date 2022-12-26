package com.xcaret.xcaret_hotel.data.usecase

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.data.api.BookingApiService
import com.xcaret.xcaret_hotel.data.api.MopApiService
import com.xcaret.xcaret_hotel.data.entity.BookingEntity
import com.xcaret.xcaret_hotel.data.entity.ErrorPayment
import com.xcaret.xcaret_hotel.data.entity.PaymentBankEntity
import com.xcaret.xcaret_hotel.data.entity.PaymentBankInfoEntity
import com.xcaret.xcaret_hotel.domain.PaymenSecurity
import com.xcaret.xcaret_hotel.domain.Payment
import com.xcaret.xcaret_hotel.domain.PaymentGenericResponse
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.LogHX
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.HttpException
import java.lang.Exception
import retrofit2.adapter.rxjava2.Result.response




class PaymentUseCase {
    private val tag = "PaymentUseCase"
    private lateinit var apiMopApiService: MopApiService
    private lateinit var apiBookingService: BookingApiService
    private val paramSettingUseCase: ParamSettingUseCase by lazy { ParamSettingUseCase() }

    private lateinit var paymentSecurity: PaymenSecurity

    private fun initialize(ok: () -> Unit){
        doAsync {
            val url = paramSettingUseCase.findParamSettingByCode(Constants.api_mop_url)
            val user = paramSettingUseCase.findParamSettingByCode(Constants.api_mop_user)
            val pass = paramSettingUseCase.findParamSettingByCode(Constants.api_mop_pass)
            val token = paramSettingUseCase.findParamSettingByCode(Constants.api_mop_token)
            paymentSecurity = PaymenSecurity(user?.value ?: "", pass?.value ?: "", token?.value ?: "")
            apiMopApiService = MopApiService(url?.value ?: "")
            apiBookingService = BookingApiService()
            uiThread {
                ok()
            }
        }
    }

    private fun confirmSecurity(ok: () -> Unit){
        if(::paymentSecurity.isInitialized) ok()
        else initialize(ok)
    }

    fun booking(request: JsonObject, response: (result: PaymentGenericResponse<BookingEntity>) -> Unit){
        try{
            confirmSecurity {
                apiBookingService.dummyBooking(request)
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object: DisposableSingleObserver<BookingEntity>(){
                        override fun onSuccess(value: BookingEntity) {
                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val printJson = gson.toJson(value)
                            LogHX.i("$tag - booking", printJson)
                            response(PaymentGenericResponse(true, data = value))
                        }

                        override fun onError(e: Throwable) {
                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val printJson = gson.toJson(e)
                            LogHX.i("$tag - booking", printJson)
                            response(PaymentGenericResponse(false))
                        }

                    })
            }
        }
        catch (e: Exception){
            LogHX.e("$tag - banks", e.toString())
            response(PaymentGenericResponse(false))
        }
    }

    fun banks(payment: Payment, response: (result: PaymentGenericResponse<PaymentBankEntity>) -> Unit){
        try{
            confirmSecurity {
                apiMopApiService.banks(paymentSecurity, payment)
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object: DisposableSingleObserver<PaymentBankEntity>(){
                        override fun onSuccess(value: PaymentBankEntity) {
                            LogHX.i("$tag - banks", value.toString())
                            response(PaymentGenericResponse(true, data = value))
                        }

                        override fun onError(e: Throwable) {
                            LogHX.e("$tag - banks", e.toString())
                            response(PaymentGenericResponse(false))
                        }

                    })
            }
        }
        catch (e: Exception){
            LogHX.e("$tag - banks", e.toString())
            response(PaymentGenericResponse(false))
        }
    }

    fun bankInfo(bin: String, response: (result: PaymentGenericResponse<PaymentBankInfoEntity>) -> Unit){
        try{
            confirmSecurity {
                apiMopApiService.bankInfo(paymentSecurity, bin)
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object: DisposableSingleObserver<PaymentBankInfoEntity>(){
                        override fun onSuccess(value: PaymentBankInfoEntity) {
                            LogHX.i("$tag - banks", value.toString())
                            response(PaymentGenericResponse(true, data = value))
                        }

                        override fun onError(e: Throwable) {
                            LogHX.e("$tag - banks", e.toString())
                            response(PaymentGenericResponse(false))
                        }

                    })
            }
        }
        catch (e: Exception){
            LogHX.e("$tag - banks", e.toString())
            response(PaymentGenericResponse(false))
        }
    }

    fun bankInfoV2(bin: String, response: (result: PaymentGenericResponse<PaymentBankInfoEntity>) -> Unit){
        try{
            val Nbin = bin.substring(0,6)
            Log.e("NBIN", "Bin $Nbin")
            confirmSecurity {
                //apiMopApiService.bankInfo(paymentSecurity, bin)
                apiMopApiService.bankInfoV2(Nbin.toInt())
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.computation())
                    .subscribeWith(object: DisposableSingleObserver<PaymentBankInfoEntity>(){
                        override fun onSuccess(value: PaymentBankInfoEntity) {
                            LogHX.i("$tag - banks", value.toString())
                            response(PaymentGenericResponse(true, data = value))
                        }

                        override fun onError(e: Throwable) {
                            var code =  (e as HttpException).code()
                            LogHX.e("$tag - banks", e.toString())
                            response(PaymentGenericResponse(false,code,PaymentBankInfoEntity(IdBank = "0",error = ErrorPayment(code))))
                        }

                    })
            }
        }
        catch (e: Exception){
            LogHX.e("$tag - banks", e.toString())
            response(PaymentGenericResponse(false))
        }
    }


}