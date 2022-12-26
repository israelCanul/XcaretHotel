package com.xcaret.xcaret_hotel.data.mapper

import com.google.firebase.database.DataSnapshot
import java.lang.reflect.ParameterizedType


/**
 * Created by José Cárdenas on 2020-01-16
 */

interface IMapper<From, To> {
    fun map(from: From?, key: String?): To?
}