package com.xcaret.xcaret_hotel.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.data.mapper.FirebaseMapper
import java.lang.AssertionError

/**
 * Created by José Cárdenas on 2020-01-16
 */

abstract class FirebaseDatabaseRepository<Model> {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferenceQuery: Query
    private lateinit var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>
    private lateinit var listener: BaseValueEventListener<Model>
    private var mapper: FirebaseMapper<*, *>? = null
    private lateinit var firebaseDatabase: FirebaseDatabase

    private var isAttachListener = false
    private var isAttachQueryListener = false

    protected abstract fun getRootNode(): String

    constructor()

    constructor(mapper: FirebaseMapper<*, *>,
                firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.DBCore)){
        init(mapper, firebaseDatabase)
    }

    fun init(mapper: FirebaseMapper<*, *>,
             firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.DBCore)){
        this.firebaseDatabase = firebaseDatabase
        databaseReference = firebaseDatabase.getReference(getRootNode())
        this.mapper = mapper
    }

    fun updateDatabaseReference(){
        this.databaseReference = firebaseDatabase.getReference(getRootNode())
    }

    open fun addListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>){
        this.firebaseCallback = firebaseCallback
        listener = BaseValueEventListener(mapper!!, firebaseCallback)
        databaseReference.addValueEventListener(listener)
        isAttachListener = true
    }

    open fun addSingleListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>){
        this.firebaseCallback = firebaseCallback
        listener = BaseValueEventListener(mapper!!, firebaseCallback)
        databaseReference.addListenerForSingleValueEvent(listener)
    }

    open fun addListenerForQuery(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>){
        this.firebaseCallback = firebaseCallback
        listener = BaseValueEventListener(mapper!!, firebaseCallback)
        databaseReferenceQuery.addValueEventListener(listener)
        isAttachQueryListener = true
    }

    open fun getReference(): DatabaseReference = databaseReference

    open fun setReferenceQuery(databaseReferenceQuery: Query){
        this.databaseReferenceQuery = databaseReferenceQuery
    }

    open fun add(item: Map<String, Any?>): Task<Void> {
        return databaseReference.setValue(item)
    }

    open fun removeListener(){
        try {
            if(isAttachListener) {
                databaseReference.removeEventListener(listener)
                isAttachListener = false
            }
            if(isAttachQueryListener){
                databaseReferenceQuery.removeEventListener(listener)
                isAttachQueryListener = false
            }
        }catch (e: AssertionError){
            e.printStackTrace()
        }
    }

    interface FirebaseDatabaseRepositoryCallback<T> {
        fun onSuccess(result: MutableList<T>)
        fun onError(e: Exception)
    }
}