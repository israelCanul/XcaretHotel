package com.xcaret.xcaret_hotel.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.amplifyframework.auth.AuthProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.ErrorCode
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.LogHX

@Entity
data class User(
    @PrimaryKey
    var uid: String = "",

    @ColumnInfo(name = "sales_force_id")
    var salesForceId: String = "",

    @ColumnInfo(name = "cognito_id")
    var cognitoId: String = "",

    @ColumnInfo(name = "first_name")
    var firstName: String = "",

    @ColumnInfo(name = "last_name")
    var lastName: String = "",
    var name: String = "",
    var email: String = "",
    @Ignore var password: String = "",
    @Ignore var confirmPassword: String = "",
    var phone: String = "",
    @Ignore var provider: ProviderType = ProviderType.Default,
    var provider_value: String = "",
    var title_code: String = "",

    @ColumnInfo(name = "user_name")
    var userName: String = "",

    var address: String = "",
    var country_code: String = "",
    var state_code: String = "",
    var cp: String = "",
    var platform: String = "Android",
    var lang: String = "",
    var city: String = "",

    @ColumnInfo(name = "mac_address")
    var macAddress: String = "",

    var registered: String = "",
    var device: String = "",
    var token: String = "",
    var version: String = "",
    var picture: String = "",
    var notify_promotions: Boolean = true,
    var como_se_entero: String = "",
    var language: String = Language.getLangNameSF(HotelXcaretApp.mContext),
    @Ignore var success: Boolean? = false,
    @Ignore var special_request: String? = null,
    @Ignore var iam_adult: Boolean = false,
    @Ignore var objCountry: Country? = null,
    @Ignore var objState: State? = null

){

    @Ignore
    fun toBuyer(): BuyerData{
        return BuyerData(
            title = title_code,
            firstName = firstName,
            lastName = lastName,
            address = address,
            country = country_code,
            state = state_code,
            city = city,
            cp = cp,
            phone = phone,
            specialRequest = special_request ?: "",
            iamAdult = iam_adult,
            objState = objState,
            objCountry = objCountry,
            email = email
        )
    }

    @Ignore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "salesForceId" to salesForceId,
            "cognitoId" to cognitoId,
            "firstName" to firstName,
            "lastName" to lastName,
            "name" to name,
            "email" to email,
            "provider" to provider.value,
            "title" to title_code,
            "userName" to userName,
            "address" to address,
            "country" to country_code,
            "state" to state_code,
            "city" to city,
            "cp" to cp,
            "platform" to platform,
            "lang" to lang,
            "macAddress" to macAddress,
            "registered" to registered,
            "device" to device,
            "phone" to phone,
            "token" to token,
            "version" to version,
            "notify_promotions" to notify_promotions,
            "como_se_entero" to como_se_entero,
            "language" to language
        )
    }

    @Ignore
    fun toMapUpdate(): Map<String, Any?> {
        return mapOf(
            "salesForceId" to salesForceId,
            "firstName" to firstName,
            "lastName" to lastName,
            "name" to name,
            "userName" to userName,
            "address" to address,
            "country" to country_code,
            "state" to state_code,
            "city" to city,
            "cp" to cp,
            "phone" to phone,
            "notify_promotions" to notify_promotions,
            "title" to title_code
        )
    }

    @Ignore
    fun toRequest(): JsonObject {
        val request = JsonObject()
        val contact = JsonArray()
        val profile = JsonObject()

        profile.addProperty("FirstName", firstName)
        profile.addProperty("LastName", lastName)
        profile.addProperty("Email", email)
        profile.addProperty("Phone", phone)
        profile.addProperty("mailingCountryCodeIso__c", country_code)
        profile.addProperty("mailingStateCodeIso__c", state_code)
        profile.addProperty("External_Id__c", cognitoId)
        profile.addProperty("MailingPostalCode", cp)
        profile.addProperty("Notificacion_noticias__c", notify_promotions)
        profile.addProperty("MailingCity", city)
        profile.addProperty("MailingStreet", address)
        profile.addProperty("Como_se_entero_de_nosotros__c",como_se_entero )
        profile.addProperty("Lenguaje__c", language)
        profile.addProperty("Title", title_code)

        contact.add(profile)
        request.add("Contact", contact)
        request.addProperty("ContactDeveloperName", "Ex_Visitante")
        LogHX.i("request", request.toString())
        return request
    }

    @Ignore
    override fun toString(): String {
        return "User {" +
                "firstName = $firstName, " +
                "lastName = $lastName, " +
                "name = $name, " +
                "email = $email, " +
                "country = $country_code, " +
                "state = $state_code, " +
                "}"
    }

    fun isProfileComplete(): Boolean {
        return title_code.isNotEmpty() && country_code.isNotEmpty() &&
                state_code.isNotEmpty() && city.isNotEmpty() && address.isNotEmpty() &&
                cp.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()
    }
}

class UserValidError(
    var hasError: Boolean = false,
    var firstName: Int = 0,
    var lastName: Int = 0,
    var email: Int = 0,
    var password: Int = 0,
    var confirmPassword: Int = 0,
    var confirmEmail: Int = 0,
    var code: Int = 0,
    var state: Int = 0,
    var country: Int = 0,
    var phone: Int = 0,
    var city: Int = 0,
    var address: Int = 0,
    var cp: Int = 0

)

class AuthResult(
    var success: Boolean = false,
    val message: String = "",
    val userValidError: UserValidError? = null,
    var errorCode: ErrorCode = ErrorCode.None
)

enum class ProviderType(val value: String){
    Default("password"),
    Google("google.com"),
    Facebook("facebook.com"),
    Visitor("Firebase");

    companion object {
        fun fromValue(v: String): ProviderType {
            return when (v) {
                Google.value -> Google
                Facebook.value -> Facebook
                Visitor.value -> Visitor
                else -> Default
            }

        }

        fun fromValue(provider: AuthProvider): ProviderType {
            return when (provider.providerKey) {
                AuthProvider.google().providerKey -> Google
                AuthProvider.facebook().providerKey -> Facebook
                else -> Default
            }

        }
    }
}