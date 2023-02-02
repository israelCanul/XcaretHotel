package com.xcaret.xcaret_hotel.photopass.ui


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.xcaret.xcaret_hotel.R

import com.xcaret.xcaret_hotel.databinding.AlbumPpFragmentBinding


import com.xcaret.xcaret_hotel.photopass.Utils
import com.xcaret.xcaret_hotel.photopass.domain.AlbumList
import com.xcaret.xcaret_hotel.photopass.vm.AlbumPPViewModel
import com.xcaret.xcaret_hotel.photopass.vm.AlbumPPViewModel.Companion.keysLabelPpAlbum
import com.xcaret.xcaret_hotel.view.config.GenericAdapter

import kotlinx.android.synthetic.main.album_pp_fragment.*
import java.time.LocalDateTime
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.doAsync
import java.util.*


class AlbumPPFragment : BaseFragment() {
    private var cont = 0
    private var listPhotoParkId = listOf<Int>()

    private lateinit var photoCodes:String
    private lateinit var dateExp:String
    private lateinit var token:String
    private val dateTime = LocalDateTime.now()

    private val albumAdapter = GenericAdapter<AlbumList>(R.layout.item_pp_album)

    private val _viewModel:  AlbumPPViewModel by lazy {
        ViewModelProvider(this).get(AlbumPPViewModel::class.java)
    }


    override fun getLayout(): Int = R.layout.album_pp_fragment
    override fun onViewFragmentCreated(view: View, savedInstanceState: Bundle?) {
        initComponent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: AlbumPpFragmentBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayout(), container, false)
        binding.albumPpViewModel = _viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
//        _viewModel.defaultLangUseCase.getFromFirebaseAndSave()// llamamos los datos desde firebase para su guardado
        return binding.root
    }


    private fun initComponent() {
        Log.i("Idioma", Locale.getDefault().language)
//        var options: FirebaseOptions = FirebaseOptions.Builder()
//            .setApplicationId("1:1023643505773:android:b887cebf80e57578")
//            .setApiKey("AIzaSyAF774Af10p7ewpQtEUtkytTOQyDR9IWsg")
//            .setDatabaseUrl("https://xcaretftvym2017.firebaseio.com")
//            .build()
//        this.context?.let {
////            FirebaseApp.initializeApp(it, options, "second_database_name")
//            var secondApp: FirebaseApp = FirebaseApp.getInstance("xcaret_database")
////            val secondDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(secondApp)
//           var miotraDatabase = FirebaseDatabase.getInstance(secondApp, FirebaseReference.FB_Photo_Pass)
//
//            val myRef = miotraDatabase.getReference("photousers/jpfEiJhdjma8Cgc0ck8QEF97e2Q2/listCodes")
////            Log.i("REF", myRef.toString())
////            myRef.setValue("Hello, Worldssss!")
//            myRef.addValueEventListener(object : ValueEventListener {
//
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    // This method is called once with the initial value and again
//                    // whenever data at this location is updated.
//                    val value = dataSnapshot.toString() //dataSnapshot.getValue<String>()
//                    Log.d("Valor", "Value is: $value")
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Failed to read value
//                    Log.w("Valor Error", "Failed to read value.", error.toException())
//                }
//            })
//        };



        val preference = getPreferences(requireContext())
        dateExp = preference.getString("DateExp", "2024-05-05T11:50:55") ?: "2024-05-05T11:50:55"
        token = preference.getString("Token", "") ?: ""

        _viewModel.token.value = token

        rvPpAlbum.adapter = albumAdapter


        ObserverLagDefault()//


//        _viewModel.getPhotoUser(object : FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<String> {
//            override fun onSuccess(result: MutableList<String>) {
//                Log.i("FotoCODE", "photoCode")
//                result.forEach { photoCode ->
//                    Log.i("FotoCODE", photoCode)
//                }
//            }
//            override fun onError(e: Exception) {
//                Log.d("error getPhotoCode", e.message ?: "")
//
//            }
//        })
//
//
        Log.i("FotoCODE", Utils.isInternetAvailable(requireContext()).toString())
        if(Utils.isInternetAvailable(requireContext())){

            val dateApi = LocalDateTime.parse(dateExp)
            Log.i("FotoCODE date", dateTime.isAfter(dateApi).toString())
            if(true){

                _viewModel.login { photoLogin, error->
                    Log.i("FotoCODE date",photoLogin.toString())
                    Log.i("FotoCODE date",error.toString())
                    photoLogin?.let {
                        runOnUiThread {
                            _viewModel.token.value = photoLogin.token
                        }
                        doAsync {
                            runOnUiThread {
                                _viewModel.loading.value = true
                            }
                            token = photoLogin.token ?: ""
                            dateExp = photoLogin.exp ?: "2024-05-05T11:50:55"
                            getPreferences(requireContext()).edit().putString("Token", photoLogin.token).apply()
                            getPreferences(requireContext()).edit().putString("DateExp", photoLogin.exp).apply()
                            validateAlbum()
                        }
                    }?: kotlin.run {
                        runOnUiThread {
                            _viewModel.loadApiError.value = true
                            _viewModel.messageError.value = "Conection fail ${error?.message}"
                            _viewModel.loading.value = false
                            error?.printStackTrace()
                        }
                    }
                }
            }else{ doAsync { validateAlbum() } }
        }
    }

    private fun validateAlbum(){
        val album = _viewModel.getAllAlbumNotData()
        val group = album?.groupBy {
            it.code
        }
        if (group?.isEmpty() == true ){
            _viewModel.loading.postValue(false)
        }
        group?.forEach { (index, list) ->
            if (list.isNotEmpty()) {
                _viewModel.updateAlbumDate(index!!)
            }
        }
    }
    private fun ObserverLagDefault(){
        _viewModel.getDefaultLangByLabel().observe(viewLifecycleOwner, Observer {
            Log.i("Lista PhotoTags", it.toString())
            it.let {
                _viewModel.getLabelDefault(_viewModel.labelPpAlbum, keysLabelPpAlbum, it)
            }
        })
    }


    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(AlbumPPFragment::class.java.name, Context.MODE_PRIVATE)
    }

//    override fun getViewModelClass(): Class<AlbumPPViewModel> = AlbumPPViewModel::class.java
//
//    override fun bindViewToModel() { binding.albumPpViewModel = viewModel }
//
//    override fun setupUI() {
//
//    }
}