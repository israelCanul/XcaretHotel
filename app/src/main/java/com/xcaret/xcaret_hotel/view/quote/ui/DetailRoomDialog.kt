package com.xcaret.xcaret_hotel.view.quote.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.stfalcon.imageviewer.StfalconImageViewer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.repository.FirebaseReference
import com.xcaret.xcaret_hotel.domain.Gallery
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.quote.vm.RoomDetailDialogViewModel
import kotlinx.android.synthetic.main.dialog_detail_room.*
import java.lang.Exception

class DetailRoomDialog: BaseDialog() {
    override fun getLayout(): Int = R.layout.dialog_detail_room

    private val viewModel = RoomDetailDialogViewModel()
    private val adapterLocation = GenericAdapter<Place>(R.layout.item_room_location)
    private lateinit var viewer: StfalconImageViewer<Gallery>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun initComponents() {
        fullScreen()
        setColorStatusBar()
        setColorNavBar()
        btnClose.bringToFront()
        //auxToolbar.updateHeight(0)

        //Utils.fixHeightTopView(auxToolbar)
        Utils.fixHeightBottomView(auxNavigation)
        isCancelable = false
        rvLocations.adapter = adapterLocation
        setListeners()
        setObservers()
        _parentActivity?._viewModel?.roomTypeLiveData?.observe(this, Observer { rt ->
            rt?.let { updateUI(it) }
            viewModel.getImages(rt?.uid!!)
        })
        viewModel.galleryList.observe(this,{
            if (it.isNotEmpty()){

            }
        })
    }

    private fun setUpImageViewer() {
        viewer = StfalconImageViewer.Builder<Gallery>(context, viewModel.galleryList.value, ::loadPosterImage)
            .withStartPosition(0)
            .withHiddenStatusBar(false)
            .show()
    }

    fun showImageViewer(){
        try {
            if(viewModel.galleryList.value?.isNotEmpty()==true)
                setUpImageViewer()
                //viewer.show()
        }catch (exc:Exception){

        }
    }

    private fun updateUI(roomType: RoomType){
        roomType.lang?.let { lang ->
            _parentActivity?.
            loadRemoteImage("${_parentActivity?.currentHotel?.code?.toUpperCase()}/${FirebaseReference.ROOM_TYPE}", lang.image ?: "", ivHeader)
            txtCategoryRoom.text = lang.title
            txtDescription.text = lang.descriptionLong
        }

        roomType.category?.let { category ->
            txtTypeRoom.text = category.lang?.title
        }
    }

    private fun setObservers(){
        progress.makeVisible()
        rvLocations.makeGone()
        viewModel.getLocations(_parentActivity?._viewModel?.roomTypeLiveData?.value?.uid ?: "").removeObservers(this)
        viewModel.getLocations(_parentActivity?._viewModel?.roomTypeLiveData?.value?.uid ?: "").observe(this, Observer {
            adapterLocation.addItems(it)
            progress.makeGone()
            rvLocations.makeVisible()
        })
    }

    private fun loadPosterImage(imageView: ImageView, poster: Gallery?) {
        val realPath = "${_parentActivity?.currentHotel?.code?.toLowerCase()}${poster?.path}"
        val referenceOrigin =
            poster?.name?.let { _parentActivity?.firebaseStorage?.getReference(realPath)?.child(it) }
        imageView.apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            background = this.resources.getDrawable(R.drawable.bg_imageviewer)
            if (poster != null) {
                loadImage(referenceOrigin!!)
            }
        }
    }

    private fun setListeners(){
        btnClose.onClick { dismiss() }
        ivHeader.onClick {
           // showImageViewer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.galleryList.value = mutableListOf<Gallery>()
    }

    companion object{
        const val TAG = "DetailRoomDialog"
        fun newInstance() = DetailRoomDialog()
    }
}