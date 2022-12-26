package com.xcaret.xcaret_hotel.data.mapper

import com.xcaret.xcaret_hotel.data.entity.UserEntity
import com.xcaret.xcaret_hotel.domain.ProviderType
import com.xcaret.xcaret_hotel.domain.User

class UserMapper: FirebaseMapper<UserEntity, User>(isSingleObject = true){
    override fun map(from: UserEntity?, key: String?): User {
        return User(
            uid = key ?: "",
            salesForceId = from?.salesForceId ?: "",
            cognitoId = key ?: "",
            firstName = from?.firstName ?: "",
            lastName = from?.lastName ?: "",
            name = from?.name ?: "",
            email = from?.email ?: "",
            phone = from?.phone ?: "",
            provider = ProviderType.fromValue(from?.provider ?: ""),
            provider_value = from?.provider ?: "",
            title_code = from?.title ?: "",
            userName = from?.userName ?: "",
            address = from?.address ?: "",
            country_code = from?.country ?: "",
            state_code = from?.state ?: "",
            cp = from?.cp ?: "",
            platform = from?.platform ?: "",
            lang = from?.lang ?: "",
            macAddress = from?.macAddress ?: "",
            registered = from?.registered ?: "",
            device = from?.device ?: "",
            token = from?.token ?: "",
            version = from?.version ?: "",
            notify_promotions = from?.notify_promotions == true,
            picture = from?.picture ?: "",
            city = from?.city ?: ""
        )
    }
}