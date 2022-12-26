package com.xcaret.xcaret_hotel.data.entity

import com.xcaret.xcaret_hotel.view.config.ListItemViewModel

data class PaymentBankEntity(
    val cardTypes: List<PaymentCardTypeEntity>? = null,
    val banks: List<PaymentBanksEntity>? = null
)

data class PaymentCardTypeEntity(
    val idCardType: Int? = null,
    val cardTypeCode: String? = null,
    val cardTypeName: String? = null,
    val cardTypeUrlLogo: String? = null,
)

data class PaymentBanksEntity(
    val idBank: Int? = null,
    val bankCode: String? = null,
    val bankName: String? = null,
    var bankInstallments: List<PaymentBankInstallmentEntity>? = null,
    val idSivex: Int? = null
)

data class PaymentBankInstallmentEntity(
    val idBank: Int? = null,
    val installmentCode: String? = null,
    val installmentName: String? = null,
    val installments: Int? = null,
    val commissionAmount: Double? = null,
    val commissionPercentage: Double? = null,
    val minimiumAmount: Double? = null,
    var isSelected: Boolean = false
): ListItemViewModel()

data class PaymentBankInfoEntity(
    val Bin: String? = null,
    val Brand: String? = null,
    val Bank: String? = null,
    val Type: String? = null,
    val Level: String? = null,
    val IsoCountry: String? = null,
    val Info: String? = null,
    val Country2_Iso: String? = null,
    val Country3_Iso: String? = null,
    val Www: String? = null,
    val Phone: String? = null,
    val IdBank: String? = null,
    val PaymentMethodCode: String? = null,
    val PaymentMethodName: String? = null,
    val CardTypeCode: String? = null,
    val CardTypeName: String? = null,
    var error:ErrorPayment? = null
) {
    fun getCartTypCode(): String{
        val b = Brand?.toLowerCase() ?: ""
        return when {
            b.contains("amex") -> "ax"
            b.contains("mastercard") -> "mc"
            b.contains("visa") -> "vi"
            b.contains("discover") -> "ds"
            b.contains("jcb") -> "jc"
            b.contains("diners club") -> "dn"
            else -> ""
        }
    }
}
data class ErrorPayment(
    var code:Int? = 0,
    var text:String? = null
)