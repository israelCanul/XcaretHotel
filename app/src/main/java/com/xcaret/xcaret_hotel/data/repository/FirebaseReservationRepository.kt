package com.xcaret.xcaret_hotel.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.data.mapper.ReservationMapper
import com.xcaret.xcaret_hotel.domain.Reservation
import com.xcaret.xcaret_hotel.view.config.LogHX

class FirebaseReservationRepository(var userUID: String = ""): FirebaseDatabaseRepository<Reservation>() {

    private var rootNode = FirebaseReference.RESERVATIONS;

    init {
        this.rootNode += "/$userUID"
        LogHX.i("reservation reference", rootNode)
        init(ReservationMapper(), FirebaseDatabase.getInstance(BuildConfig.DBReservations))
    }

    fun updateRootNode(uid: String){
        LogHX.i("old reservation reference", rootNode)
        if(userUID != uid) {
            userUID = uid
            this.rootNode = "${FirebaseReference.RESERVATIONS}/$userUID"
            updateDatabaseReference()
            LogHX.i("new reservation reference", rootNode)
        }
    }

    override fun getRootNode(): String = rootNode
}