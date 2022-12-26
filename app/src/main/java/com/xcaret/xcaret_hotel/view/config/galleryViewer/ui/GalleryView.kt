package com.xcaret.xcaret_hotel.view.config.galleryViewer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.storage.FirebaseStorage
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.domain.Gallery
import kotlinx.android.synthetic.main.gallery_viewer_layout.view.*
import com.stfalcon.imageviewer.StfalconImageViewer
import com.xcaret.xcaret_hotel.view.config.*
import java.lang.Exception
import java.util.*

class GalleryView (context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var GalleryAdapter = GenericAdapter<Gallery>(R.layout.item_gallery)
    private var dotPositionAdapter = GenericAdapter<Gallery>(R.layout.item_pointer_gallery)
    private lateinit var viewer: StfalconImageViewer<Gallery>
    private  var firebaseStorage : FirebaseStorage? = null
    private var currentHotelCode = ""
    private var currentItemPostion = 0

    init {
        inflate(context, R.layout.gallery_viewer_layout, this)
        init()
    }

    private fun init() {
        setUpListeners()
        rvGalleryImage.adapter = GalleryAdapter
        rvindicator.adapter = dotPositionAdapter

    }

    private fun setUpListeners(){
        fabGallery.setOnClickListener {
            //abrir el dialogo full screen
            showImageViewer()
        }
        GalleryAdapter.setOnListItemViewClickListener(object : GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                setUpImageViewer(position)
            }
        })
        rvGalleryImage.addOnItemChangedListener{_,adapterPositon->
            currentItemPostion = adapterPositon
            val item = dotPositionAdapter.getItem(adapterPositon)
            var oldPosition = -1
            dotPositionAdapter.getItems().find { it.isSelected }.let { oldItem ->
                oldItem?.isSelected = false
                oldPosition = dotPositionAdapter.getItems().indexOf(oldItem)
                dotPositionAdapter.notifyItemChanged(oldPosition)
            }
            if (oldPosition != adapterPositon){
                item?.isSelected = true
                dotPositionAdapter.notifyItemChanged(adapterPositon)
            }
        }

    }

    fun setUpAdapter(galleryList:List<Gallery>){
        if (galleryList.size> 1){
            fabGallery.makeVisible()
            rvindicator.makeVisible()
        }
        GalleryAdapter.addItems(galleryList)
        dotPositionAdapter.addItems(galleryList)
    }
    fun setUpViewer(currentHotelCode:String, storage:FirebaseStorage?){
        this.currentHotelCode = currentHotelCode
        this.firebaseStorage = storage

    }

    //region ImageViewer


    fun showImageViewer(){
        try {
            if(GalleryAdapter.getItems().isNotEmpty())
                setUpImageViewer()
            //viewer.show()
        }catch (exc: Exception){

        }
    }

    private fun setUpImageViewer(position: Int = 0) {
        viewer = StfalconImageViewer.Builder<Gallery>(context, GalleryAdapter.getItems(), ::loadPosterImage)
            .withStartPosition(position)
            .withHiddenStatusBar(false)
            .show()
    }

    private fun loadPosterImage(imageView: ImageView, poster: Gallery?) {
        val realPath = "${currentHotelCode.toLowerCase(Locale.ROOT)}${poster?.path}"
        //val realPath = "${poster?.path}"
        val referenceOrigin = firebaseStorage?.getReference(realPath)?.child("${poster?.name}.jpg")
        //val referenceOrigin = firebaseStorage.getReference(realPath).child("${poster?.name}")
        imageView.apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            background = ResourcesCompat.getDrawable(context.resources,R.drawable.bg_imageviewer,context.theme)
            if (poster != null) {
                if (referenceOrigin!= null)
                    loadImage(referenceOrigin)
            }
        }
    }
    //endregion
}