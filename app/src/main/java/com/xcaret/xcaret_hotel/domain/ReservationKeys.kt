package com.xcaret.xcaret_hotel.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservationKeys")
data class ReservationKeys(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val after: String?,
    val before: String?
)