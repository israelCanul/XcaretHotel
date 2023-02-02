package com.xcaret.xcaret_hotel.photopass.data.repository

import android.content.Context
import android.util.Log
import com.xcaret.xcaret_hotel.BuildConfig
import com.xcaret.xcaret_hotel.photopass.data.mapper.FirebasePhotoMapper
import java.lang.ref.PhantomReference
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.data.repository.FirebaseReference
import kotlin.math.log

abstract class SingleFirebaseDatabaseRepository<Model> {
    private lateinit var databaseReference: DatabaseReference
    private var BaseApp = FirebaseApp.getInstance("xcaret_database")
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>
    private var mapper: FirebasePhotoMapper<*, *>? = null
    private lateinit var listener: BaseValueEventListener<Model>
    private lateinit var firebaseCallbackSingleObject: FirebaseDatabaseRepositoryCallbackSingleObject<Model>
    private var isAttachListener = false
    protected abstract fun getRootNode(): String // para inicializar la ruta de la BD de la clase que herede

    constructor(mapper: FirebasePhotoMapper<*, *>,
                firebaseDatabaseString: String = BuildConfig.DBCore, photo: Boolean = true){
        Log.i("Constructor2", firebaseDatabaseString.toString())
        init(mapper, firebaseDatabaseString)
    }

    fun init(mapper: FirebasePhotoMapper<*, *>,
             firebaseDatabase: String = BuildConfig.DBCore){
        BaseApp = FirebaseApp.getInstance("xcaret_database")
        Log.i("databaseReferenceString Base APP", BaseApp.toString())
        this.firebaseDatabase =  FirebaseDatabase.getInstance(BaseApp)
        databaseReference = this.firebaseDatabase.getReference(getRootNode())
        Log.i("databaseReferenceString Kha ? ", databaseReference.toString())
        this.mapper = mapper
    }
    open fun addListener(firebaseCallback: FirebaseDatabaseRepositoryCallback<Model>){
        this.firebaseCallback = firebaseCallback
        listener = BaseValueEventListener(mapper!!, firebaseCallback)
        databaseReference.addValueEventListener(listener)
        databaseReference.addChildEventListener(childListener)
        isAttachListener = true
    }


    //HELPER CLASSES AND FUNCTIONS
    companion object{
        fun initializeBDXcaret(context: Context){
            // se inicializa la BD de xcaret
            var options: FirebaseOptions = FirebaseOptions.Builder()
                .setApplicationId("1:1023643505773:android:b887cebf80e57578")
                .setApiKey("AIzaSyAF774Af10p7ewpQtEUtkytTOQyDR9IWsg")
                .setDatabaseUrl("https://xcaretftvym2017.firebaseio.com")
                .build()
            FirebaseApp.initializeApp(context, options, "xcaret_database")
//            var xcaretApp: FirebaseApp = FirebaseApp.getInstance("xcaret_database")
//            FirebaseDatabase.getInstance(xcaretApp, FirebaseReference.FB_Photo_Pass).setPersistenceEnabled(true)
        }
    }
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
    interface FirebaseDatabaseRepositoryCallback<T> {
        fun onSuccess(result: MutableList<T>)
        fun onRemove(item: T)
        fun onError(e: Exception)
    }
    interface FirebaseDatabaseRepositoryListCallback<T> {
        fun onSuccess(result: List<T>)
        fun onRemove(item: T)
        fun onError(e: Exception)
    }
    interface FirebaseDatabaseRepositoryCallbackSingleObject<T> {
        fun onSuccess(result: T)
        fun onRemove(item: T)
        fun onError(e: Exception)
    }
    @Suppress("UNCHECKED_CAST")
    class BaseValueEventListener<Model> : ValueEventListener {
        private var mapper: FirebasePhotoMapper<*, *>
        private var callback: FirebaseDatabaseRepositoryCallback<Model>
        constructor(mapper: FirebasePhotoMapper<*, *>, callback: FirebaseDatabaseRepositoryCallback<Model>){
            this.mapper = mapper
            this.callback = callback
        }
        override fun onCancelled(p0: DatabaseError) {
            callback.onError(p0.toException())
        }
        override fun onDataChange(p0: DataSnapshot) {
            val data = mapper.mapList(p0).toMutableList()
            callback.onSuccess(data as MutableList<Model>)
        }
    }
}