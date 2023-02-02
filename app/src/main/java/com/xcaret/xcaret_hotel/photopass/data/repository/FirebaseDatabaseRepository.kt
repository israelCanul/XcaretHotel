package com.xcaret.xcaret_hotel.photopass.data.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.data.repository.FirebaseReference

import com.xcaret.xcaret_hotel.photopass.data.mapper.FirebasePhotoMapper
import com.xcaret.xcaret_hotel.photopass.data.mapper.FirebasePhotoMapperSingleObject

import java.lang.AssertionError

abstract class FirebaseDatabaseRepository<Model> {
    private lateinit var databaseReference: DatabaseReference

    private lateinit var xcaretApp: FirebaseApp
    private lateinit var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>
    private lateinit var listener: BaseValueEventListener<Model>
    private var mapper: FirebasePhotoMapper<*, *>? = null

    private lateinit var firebaseCallbackSingleObject: FirebaseDatabaseRepositoryCallbackSingleObject<Model>
    private lateinit var listenerSingleObject: SingleObjectBaseValueListener<Model>
    private var mapperSingleObject: FirebasePhotoMapperSingleObject<*, *>? = null

    private lateinit var firebaseDatabase: FirebaseDatabase

    private var isAttachListener = false
    private var isAttachSingleListener = false
    private var isAttachListenerSingleObject = false

    protected abstract fun getRootNode(): String

    private val childListener = object: ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("onChildMoved", getRootNode())
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("onChildChanged", getRootNode())
        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("onChildAdded", getRootNode())
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val data = mapper?.map(snapshot)
            data?.let {
                if(::firebaseCallback.isInitialized){
                    firebaseCallback.onRemove(it as Model)
                }
                if(::firebaseCallbackSingleObject.isInitialized){
                    firebaseCallbackSingleObject.onRemove(it as Model)
                }
            }
        }

    }
    constructor()
    constructor(mapper: FirebasePhotoMapper<*, *>,
                firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.DBCore)){
        Log.i("Constructor1", firebaseDatabase.toString())
        init(mapper, firebaseDatabase)
    }
    constructor(mapper: FirebasePhotoMapper<*, *>,
                firebaseDatabaseString: String = BuildConfig.DBCore, photo: Boolean = true){
        Log.i("Constructor2", firebaseDatabase.toString())
        init(mapper, firebaseDatabaseString)
    }

    constructor(mapperSingleObject: FirebasePhotoMapperSingleObject<*, *>,
                firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.DBCore)){
        Log.i("Constructor3", firebaseDatabase.toString())
        initSingle(mapperSingleObject, firebaseDatabase)
    }
    fun init(mapper: FirebasePhotoMapper<*, *>,
             firebaseDatabase: String = BuildConfig.DBCore){
        xcaretApp = FirebaseApp.getInstance("xcaret_database")
        this.firebaseDatabase =  FirebaseDatabase.getInstance(xcaretApp, firebaseDatabase)
        databaseReference = this.firebaseDatabase.getReference(getRootNode())
        Log.i("databaseReferenceString", databaseReference.toString())
        this.mapper = mapper
    }

    fun init(mapper: FirebasePhotoMapper<*, *>,
             firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.DBCore)){
        xcaretApp = FirebaseApp.getInstance("xcaret_database")

        this.firebaseDatabase = firebaseDatabase
        databaseReference = firebaseDatabase.getReference(getRootNode())
        Log.i("databaseReference", databaseReference.toString())
        this.mapper = mapper
    }

    fun initSingle(mapperSingleObject: FirebasePhotoMapperSingleObject<*, *>,
                   firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.DBCore)){
        this.firebaseDatabase = firebaseDatabase
        databaseReference = firebaseDatabase.getReference(getRootNode())
        this.mapperSingleObject = mapperSingleObject
    }

    open fun addChild(child: String){
        databaseReference = firebaseDatabase.getReference(getRootNode()).child(child)
    }

    open fun addListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>){
        this.firebaseCallback = firebaseCallback
        listener = BaseValueEventListener(mapper!!, firebaseCallback)
        databaseReference.addValueEventListener(listener)
        databaseReference.addChildEventListener(childListener)
        isAttachListener = true
    }

    open fun addSingleListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>){
        this.firebaseCallback = firebaseCallback
        listener = BaseValueEventListener(mapper!!, firebaseCallback)
        databaseReference.addListenerForSingleValueEvent(listener)
    }

    open fun addListenerSingleObject(firebaseCallbackSingleObject: FirebaseDatabaseRepositoryCallbackSingleObject<Model>){
        this.firebaseCallbackSingleObject = firebaseCallbackSingleObject
        listenerSingleObject = SingleObjectBaseValueListener(mapperSingleObject!!, firebaseCallbackSingleObject)
        databaseReference.addValueEventListener(listenerSingleObject)
        databaseReference.addChildEventListener(childListener)
        isAttachListenerSingleObject = true
    }

    open fun add(item: Map<String, Any?>): Task<Void> {
        return databaseReference.setValue(item)
    }

    open fun removeListener(){
        try {
            if(isAttachListener) {
                databaseReference.removeEventListener(listener)
                databaseReference.removeEventListener(childListener)
                isAttachListener = false
            }

            if(isAttachSingleListener) {
                databaseReference.removeEventListener(listener)
                databaseReference.removeEventListener(childListener)
                isAttachSingleListener = false
            }

            if(isAttachListenerSingleObject) {
                databaseReference.removeEventListener(listener)
                databaseReference.removeEventListener(childListener)
                isAttachListenerSingleObject = false
            }
        }catch (e: RuntimeException){}

        if(::listener.isInitialized){
            databaseReference.removeEventListener(listener)
        }
    }

    interface FirebaseDatabaseRepositoryCallback<T> {
        fun onSuccess(result: MutableList<T>)
        fun onRemove(item: T)
        fun onError(e: Exception)
    }

    interface FirebaseDatabaseRepositoryCallbackSingleObject<T> {
        fun onSuccess(result: T)
        fun onRemove(item: T)
        fun onError(e: Exception)
    }
}