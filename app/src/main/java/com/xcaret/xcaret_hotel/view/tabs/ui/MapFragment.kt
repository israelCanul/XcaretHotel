package com.xcaret.xcaret_hotel.view.tabs.ui

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.MapFragmentBinding
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.MapConfig
import com.xcaret.xcaret_hotel.domain.Place
import com.xcaret.xcaret_hotel.view.adapters.FilterMapAdapter
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.Search
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.config.discretescrollview.DSVOrientation
import com.xcaret.xcaret_hotel.view.config.discretescrollview.DiscreteScrollView
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import com.xcaret.xcaret_hotel.view.tabs.vm.MapViewModel
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.android.synthetic.main.nav_header_slide.*
import kotlinx.android.synthetic.main.nav_header_slide.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread


class MapFragment: BaseFragmentDataBinding<MapViewModel, MapFragmentBinding>(), OnMapReadyCallback ,OnMapsSdkInitializedCallback{
    override fun getLayout(): Int = R.layout.map_fragment
    override fun getViewModelClass(): Class<MapViewModel> = MapViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val TAG = "MapFragment"
    private var gMap: GoogleMap? = null
    private val minZoom = 17f
    private val defaultZoom = 18f
    private val maxZoom = 19f

    private val limitBoundsBuilder = LatLngBounds.Builder()
    private val filterAdapter = FilterMapAdapter()

    private val placeAdapter = GenericAdapter<Place>(R.layout.item_place, 0.9f, 0f)
    private val serviceAdapter = GenericAdapter<Place>(R.layout.item_service, 0.9f, 0f)
    private val hotelAdapter = GenericAdapter<Hotel>(R.layout.item_hotel_map, percentageWith = 0.42f)

    private val iconGenerator: IconGenerator by lazy {
        IconGenerator(requireContext())
    }

    private val mLayoutInflater: LayoutInflater by lazy {
        requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun setupUI() {
        viewModel.placeUIDLive.value = arguments?.getString(Constants.PLACE_UID) ?: ""
        viewModel.hotelLive.value = _parentActivity?._viewModel?.currentHotelLive?.value
        binding.drawerLayout.txtHotelName.text = viewModel.hotelLive.value?.name

        settingsManager.getUID.asLiveData().observe(this, Observer {
            viewModel.getSession().removeObservers(this)
            viewModel.uidLiveData.value = it
        })

        logEvent(AnalyticConstant.ID_NAV_MAP,
            AnalyticConstant.ITEM_NAME_NAV_MAP,
            AnalyticConstant.CONTENT_TYPE_NAVTABBAR)
    }

    override fun onResume() {
        super.onResume()
        onlyRefreshUI()
        logEvent(AnalyticConstant.ID_NAV_MAP,
            AnalyticConstant.ITEM_NAME_NAV_MAP,
            AnalyticConstant.CONTENT_TYPE_NAVTABBAR)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsInitializer.initialize(requireContext(),MapsInitializer.Renderer.LATEST,this)
        initComponents()
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> LogHX.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> LogHX.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }

    fun update(args: Bundle? = Bundle.EMPTY){
        LogHX.d("updateMap", "ok")
        setPreObservers()

        when {
            MainActivity.HAS_REQUEST_PERMISSION_MAP -> {
                MainActivity.HAS_REQUEST_PERMISSION_MAP = false
                getLocationPermission()
            }
            isGrantedLocationPermission() -> {
                getDeviceLocation()
            }
            else -> {
                viewModel.myLiveLocation.value = null
            }
        }
    }

    fun onlyRefreshUI(){
        if(viewModel.placeUIDLive.value.isNullOrEmpty()) {
            btnBack.visibility = View.GONE
        }
        else {
            btnBack.visibility = View.VISIBLE
        }

        adjustHeightSpaceAux()
    }

    private fun adjustHeightSpaceAux(){
        val params = spaceBottom.layoutParams
        val height = if(btnBack.visibility == View.VISIBLE)
            requireContext().resources.getDimensionPixelSize(R.dimen.height_default_space)
        else requireContext().resources.getDimensionPixelSize(R.dimen.height_large_space)
        params.height = height
        spaceBottom.layoutParams = params
    }

    private fun setPreObservers(){
        viewModel.alertEmptyResultLive.value = getString(R.string.alert_empty_search)

        viewModel.hintSearch().observe(this, Observer {label ->
            label?.let {
                edSearch.hint = it.value
            }
        })

        viewModel.searchAlertEmptyResult().observe(this, Observer {lang ->
            lang?.let { viewModel.alertEmptyResultLive.value = it.value }
        })

        viewModel.searchQueryLive.observe(this, Observer {
            if(!it.isNullOrEmpty()) {
                viewModel.search().removeObservers(this)
                Handler().postDelayed({
                    activateSearch()
                }, 500)
            }
        })

        viewModel.hotelLive.observe(this, Observer {
            cleanMap()
            cleanObservers()
            setObservers()
            if(it != null)
                binding.drawerLayout.txtHotelName.text = it.name
        })

        viewModel.getSession().observe(this, Observer {
            binding.drawerLayout.txtUserNameMaps.text = it?.name
        })

        viewModel.myLiveLocation.observe(this, Observer {
            it?.let {
                myLocation.visibility = View.VISIBLE
            } ?: kotlin.run { myLocation.visibility = View.GONE }
        })
    }

    private fun cleanObservers(){
        viewModel.findCategoryUIHouseByList().removeObservers(this)
        viewModel.findCategoryUIRestByList().removeObservers(this)
        viewModel.findCategoryUIPoolByList().removeObservers(this)
        viewModel.findFiltersByHotel().removeObservers(this)
        viewModel.categoryLive.removeObservers(this)
        viewModel.categoryListLive.removeObservers(this)
        viewModel.placesLive.removeObservers(this)
        viewModel.oldSelectedPos.removeObservers(this)
        viewModel.currentPosSelected.removeObservers(this)
    }

    private fun setObservers(){
        viewModel.findCategoryUIHouseByList().observe(this, Observer {
            viewModel.listCatHomesUID.value = it
        })

        viewModel.findCategoryUIRestByList().observe(this, Observer {
            viewModel.listCatRestUID.value = it
        })

        viewModel.findCategoryUIPoolByList().observe(this, Observer {
            viewModel.listCatPoolUID.value = it
        })

        viewModel.findFiltersByHotel().observe(this, Observer { originalFilters ->
            val rootFilters = originalFilters.filter { it.type == Constants.ROOT }
            if(rootFilters.isNotEmpty()){
                rootFilters.forEach {filterMap ->
                    filterMap.childs.addAll(originalFilters.filter { it.parentUID == filterMap.uid && it.type == Constants.BRANCH })
                }
                filterAdapter.addItems(rootFilters)
            }
        })

        viewModel.categoryLive.observe(this, Observer {
            viewModel.searchQueryLive.value = ""
            if(viewModel.isOpenSearchInput.value == true) closeSearch()
            viewModel.allByHotel().removeObservers(this)
            viewModel.findByListCategory().removeObservers(this)

            if(viewModel.filterSelectedLive.value?.code == Constants.FILTER_MAP_ALL)
                allPlaceByHotel()
            else
                setObserverByFilterCategory()
        })

        viewModel.categoryListLive.observe(this, Observer {
            setObserverPlace()
        })

        viewModel.placesLive.observe(this, Observer {
            cleanMap()
            updateUI()
        })

        viewModel.oldSelectedPos.observe(this, Observer {position ->
            if(position >= 0 )toggleMarker(position, false)
        })

        viewModel.currentPosSelected.observe(this, Observer { position ->
            toggleMarker(position, true)
        })

        allPlaceByHotel()
        viewModel.readyMap.value = true
        LogHX.d("onReadyObservers", "Ok")
    }

    private fun allPlaceByHotel(){
        viewModel.allByHotel().removeObservers(this)
        viewModel.allByHotel().observe(this, Observer {
            completeInfoByPlace(it)
        })
    }

    private fun completeInfoByPlace(list: List<Place>){
        //val auxList = if(viewModel.placeUIDLive.value.isNullOrEmpty()) list
        //else list.filter { it.uid == viewModel.placeUIDLive.value }

        doAsync {
            val parents = list.groupBy { it.parentUID }
            val parentsLocation = viewModel.findWithIn(parents.keys.toList().filterNotNull())
            list.forEach { place ->
                place.hotelCode = viewModel.hotelLive.value?.code ?: ""
                val housesData = _parentActivity?._viewModel?.housesLiveData?.value
                place.isBuilding = housesData?.firstOrNull { it.uid == place.uid } != null
                place.location =
                    if (place.isBuilding) housesData?.firstOrNull { it.uid == place.uid }
                    else parentsLocation.firstOrNull { it.uid == place.parentUID }
                place.extraInfoBuilding = place.location?.extraInfoBuilding

                var formatLocation = ""
                place.location?.let { location ->
                    location.lang?.let { langLocation ->
                        formatLocation = langLocation.title ?: ""
                        if (formatLocation.isNotEmpty() && !place.level.isNullOrEmpty()) {
                            formatLocation = "  ${_parentActivity?._viewModel?.labelFloor?.value} ${place.level}"
                        }
                    }
                }
                place.formatLocation = formatLocation.trim()
            }
            runOnUiThread {
                viewModel.completeCategory(list) {
                    viewModel.placesLive.value = list
                }
            }
        }
    }

    private fun activateSearch() {
        hideShowLoaderSearch(true)
        viewModel.searchQueryLive.value?.let { term ->
            if(term.isNotEmpty()) Search(term)
        }
        viewModel.search().observe(this, Observer {listPlace ->
            if(listPlace.isEmpty()) Toast.makeText(requireContext(), viewModel.alertEmptyResultLive.value, Toast.LENGTH_LONG).show()
            else {
                cleanMap()
                completeInfoByPlace(listPlace)
            }
            runOnUiThread { hideShowLoaderSearch(false) }
        })
    }

    private fun hideShowLoaderSearch(show: Boolean) {
        if(show){
            btnAux.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }else{
            progress.visibility = View.GONE
            btnAux.visibility = View.VISIBLE
        }
    }

    private fun toggleMarker(position: Int, isSelected: Boolean = false){
        gMap?.let {
            if(position > -1){
                val sizeList = viewModel.placesLive.value?.size ?: 0
                if(sizeList > position){
                    val place = viewModel.placesLive.value?.get(position)!!
                    place.marker?.setIcon(BitmapDescriptorFactory.fromBitmap(createIcon(place, isSelected)))
                    place.marker?.zIndex = 100f
                    if(isSelected){
                        place.latitude?.let { lat ->
                            place.longitude?.let { lon ->
                                val latLng = LatLng(lat, lon)
                                goTo(latLng)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateUI() {
        placeAdapter.addItems(listOf())
        serviceAdapter.addItems(listOf())

        viewModel.placesLive.value?.let { places ->
            if(places.isEmpty()) return
            val isPlace = places[0].type == Constants.PLACE
            if (isPlace) {
                rvPlaces?.visibility = View.VISIBLE
                rvServices?.visibility = View.GONE
                placeAdapter.addItems(viewModel.placesLive.value ?: listOf())
            } else {
                rvServices?.visibility = View.VISIBLE
                rvPlaces?.visibility = View.GONE
                serviceAdapter.addItems(viewModel.placesLive.value ?: listOf())
            }

            updateGoogleLogo()
            drawMarkers()
        }
    }

    private fun updateGoogleLogo(){
        try {
            val resourceHeight = if(btnBack.visibility == View.VISIBLE) R.dimen.height_default_space else R.dimen.height_large_space
            val positionY = requireContext().resources.getDimensionPixelSize(resourceHeight) -
                    requireContext().resources.getDimensionPixelSize(R.dimen.margin_mini)
            gMap?.setPadding(0, 0, 0, positionY)
        }catch (e: Exception){}
    }

    private fun drawMarkers(){
        var indexAux = 0
        viewModel.placeUIDLive.value?.let { uid ->
            viewModel.placesLive.value?.find { it.uid == uid }?.let { place ->
                indexAux = viewModel.placesLive.value?.indexOf(place) ?: 0
            }
        }
        viewModel.placesLive.value?.forEachIndexed { index, place ->
            place.latitude?.let { lat ->
                place.longitude?.let { lon ->
                    val latLng = LatLng(lat, lon)
                    if(lat != 0.0 && lon != 0.0) {
                        place.marker = gMap?.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(place.lang?.title)
                                .icon(BitmapDescriptorFactory.fromBitmap(createIcon(place, index == 0)))
                                .anchor(iconGenerator.anchorU, iconGenerator.anchorV))
                        place.marker?.let { viewModel.auxMarkerList.value?.add(it) }
                    }
                    if(index == indexAux) {
                        viewModel.oldSelectedPos.value = viewModel.currentPosSelected.value
                        viewModel.currentPosSelected.value = index
                        if (rvPlaces.visibility == View.VISIBLE) rvPlaces.scrollToPosition(index)
                        if (rvServices.visibility == View.VISIBLE) rvServices.scrollToPosition(index)
                        if(viewModel.placeUIDLive.value.isNullOrEmpty()) {
                            if (viewModel.showMyLocation.value == true) {
                                viewModel.showMyLocation.value = false
                                viewModel.myLiveLocation.value?.let { goTo(it) }
                                    ?: kotlin.run { goTo(latLng) }
                            } else goTo(latLng)
                        }else goTo(latLng)
                    }
                }
            }
        }
    }

    private fun createIcon(place: Place, selected: Boolean = false): Bitmap {
        iconGenerator.setBackground(ColorDrawable(Color.TRANSPARENT))
        val sizeBody = resources.getDimensionPixelSize(if(selected) R.dimen.map_marker_size_selected else R.dimen.map_marker_size)
        //val sizeBorder = resources.getDimensionPixelSize(if(selected) R.dimen.map_marker_border_selected else R.dimen.map_marker_border)

        val view = mLayoutInflater.inflate(R.layout.item_map_marker, null, false)
        val params = ViewGroup.LayoutParams(sizeBody, sizeBody)
        params.height = sizeBody
        params.width = sizeBody
        view.layoutParams = params



        //val colorMarker = if(selected) ContextCompat.getColor(requireContext(), R.color.color6) else Utils.getColorRGB("229,97,179")
        var colorMarker = Utils.getColorRGB(place.colorMarkerMap)
        colorMarker?.let {
            if(selected) ContextCompat.getColor(requireContext(), R.color.colorMapIconSelected)
            else ContextCompat.getColor(requireContext(), R.color.colorMapIconUnselected)
        }
        val shapeCircle = getCircleDrawable(android.R.color.white, colorMarker!!)
        view.findViewById<ImageView>(R.id.background).setImageDrawable(shapeCircle)

        val ivLogo = view.findViewById<ImageView>(R.id.ivLogo)
        if(place.iconMap != "medical_service_icon")
            ivLogo.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
        if(selected){
            val iconMarker = Utils.getDrawableId(context, place.iconMap)
            if(iconMarker != 0)
                ivLogo.setImageResource(iconMarker!!)
            ivLogo.visibility = View.VISIBLE
        }else ivLogo.visibility = View.GONE

        iconGenerator.setContentView(view)
        return iconGenerator.makeIcon()
    }

    private fun cleanMap(){
        /*viewModel.placesLive.value?.forEach {
            it.marker?.remove()
            it.marker = null
        }*/
        viewModel.auxMarkerList.value?.forEach {
            it.remove()
        }
        viewModel.auxMarkerList.value?.clear()
        //gMap?.clear()
    }
    private fun setObserverByFilterCategory(){
        viewModel.allByFilterGroup().removeObservers(this)
        viewModel.allByFilterGroup().observe(this, Observer {categoryList ->
            val listIds = mutableListOf<String>()
            listIds.add(viewModel.categoryLive.value ?: "")
            categoryList.forEach {
                if(viewModel.filterSelectedLive.value?.code == Constants.FILTER_MAP_REST_CODE){
                    if(it.code != Constants.CATEGORY_REST_BAR_SNACK && it.code != Constants.CATEGORY_COFFE_SHOP) listIds.add(it.uid)
                }else listIds.add(it.uid)
            }
            viewModel.categoryListLive.value = listIds
        })
    }

    private fun setObserverPlace(){
        viewModel.findByListCategory().removeObservers(this)
        viewModel.findByListCategory().observe(this, Observer {
            completeInfoByPlace(it)
        })
    }

    private fun initComponents(){
        rvHotels.adapter = hotelAdapter
        viewModel.allHotel().observe(viewLifecycleOwner, Observer {
            val orderedList = it.sortedBy { it.order }
            if(orderedList.isNotEmpty()) orderedList[0].isSelected = true
            hotelAdapter.addItems(orderedList)
            viewModel.hotelLive.value = it.find { it.isSelected }
            initGoogleMap()
        })

        lvExp.setGroupIndicator(null)
        lvExp.setChildIndicator(null)
        lvExp.dividerHeight = 0

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        Utils.fixMarginTopView(txtCloseFilter)
        setupDiscreteScroll(rvPlaces)
        setupDiscreteScroll(rvServices)
        rvPlaces.adapter = placeAdapter
        rvServices.adapter = serviceAdapter
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        Utils.fixHeightTopView(auxStatusBar)
        lvExp.setAdapter(filterAdapter)
        setListeners()
    }

    private fun setupDiscreteScroll(discrete: DiscreteScrollView){
        discrete.setOverScrollEnabled(true)
        discrete.setSlideOnFling(true)
        discrete.setOrientation(DSVOrientation.HORIZONTAL)
    }

    private fun setListeners(){
        hotelAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if(hotelAdapter.getItem(position)?.isSelected == true) return
                hotelAdapter.getItems().find { it.isSelected }?.isSelected = false
                hotelAdapter.getItem(position)?.isSelected = true
                hotelAdapter.notifyItemRangeChanged(0, hotelAdapter.getItems().size)
                hotelAdapter.getItems().find { it.isSelected }?.let {
                    viewModel.hotelLive.value = it
                }
            }
        })
        btnSearch.setOnClickListener {
            openSearch()
        }

        btnSearchClose.setOnClickListener {
            closeSearch()
        }

        filterFab.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        txtCloseFilter.setOnClickListener {
            drawerLayout.closeDrawers()
        }

        drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {
                if(newState == DrawerLayout.LOCK_MODE_LOCKED_OPEN && btnBack.visibility != View.VISIBLE)
                    _parentActivity?.toggleBottomNav()
            }
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit
            override fun onDrawerClosed(drawerView: View) {
                //_parentActivity?.toggleBottomNav()
            }

            override fun onDrawerOpened(drawerView: View) {
                //_parentActivity?.toggleBottomNav()
            }

        })

        lvExp.setOnGroupClickListener { _, _, pos, _ ->
            val category = filterAdapter.getItem(pos)
            val isSingleView = !viewModel.placeUIDLive.value.isNullOrEmpty()
            viewModel.placeUIDLive.value = ""
            if(category.childs.isNullOrEmpty()) {
                if (viewModel.categoryLive.value != category.categoryUID || isSingleView) {
                    cleanMap()
                    viewModel.filterSelectedLive.value = category
                    viewModel.categoryLive.value = category.categoryUID
                    drawerLayout.closeDrawers()
                }
                true
            }else false
        }

        lvExp.setOnChildClickListener { _, _, gPos, cPos, _ ->
            drawerLayout.closeDrawers()
            val category = filterAdapter.getItem(gPos).childs[cPos]
            if(viewModel.categoryLive.value != category.categoryUID){
                cleanMap()
                viewModel.filterSelectedLive.value = category
                viewModel.categoryLive.value = category.categoryUID
            }
            true
        }

        rvPlaces.addOnItemChangedListener { _, adapterPosition ->
            viewModel.placesLive.value?.let { _ ->
                viewModel.oldSelectedPos.value = viewModel.currentPosSelected.value
                viewModel.currentPosSelected.value = adapterPosition
            }
        }

        placeAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if(!viewModel.placeUIDLive.value.isNullOrEmpty()) return
                val place = placeAdapter.getItem(position)
                place?.let {
                    if(viewModel.hotelLive.value?.code != Constants.HOTEL_XCARTE_MEXICO) return@let
                    if(viewModel.listCatHomesUID.value?.contains(it.categoryUID) == true){
                        val args = bundleOf(Constants.CAT_UID to it.categoryUID)
                        findNavController().navigate(R.id.action_mapFragment_to_buildingDetailFragment, args)
                    }else if(viewModel.listCatRestUID.value?.contains(it.categoryUID) == true){
                        val args = bundleOf(
                            Constants.REST_UID to it.uid,
                            Constants.CALLING_FROM_MAP to true
                        )
                        findNavController().navigate(R.id.action_mapFragment_to_restaurantDetailFragment, args)
                    }else if(viewModel.listCatPoolUID.value?.contains(it.categoryUID) == true){
                        val args = bundleOf(
                            Constants.POOL_UID to it.uid,
                            Constants.CALLING_FROM_MAP to true
                        )
                        findNavController().navigate(R.id.action_mapFragment_to_poolDetailFragment, args)
                    }
                }
            }
        })

        rvServices.addOnItemChangedListener{_, adapterPosition ->
            viewModel.placesLive.value?.let { _ ->
                viewModel.oldSelectedPos.value = viewModel.currentPosSelected.value
                viewModel.currentPosSelected.value = adapterPosition
            }
        }

        edSearch.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Utils.hideKeyboardFrom(requireContext(), requireView())
                viewModel.searchQueryLive.value = edSearch.text.toString().trim()
                true
            }
            false
        }

        myLocation.setOnClickListener {
            viewModel.myLiveLocation.value?.let {
                goTo(it, viewModel.mapConfigLive.value?.defaultZoom?.toFloat() ?: defaultZoom)
            }
        }

        btnAux.setOnClickListener {
            if(edSearch.text.toString().isNotEmpty()){
                edSearch.setText("")
            }
        }

        edSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?){}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    if(it.isNotEmpty()) btnAux.setImageResource(R.drawable.ic_baseline_close_24)
                    else btnAux.setImageResource(R.drawable.ic_search)
                }
            }
        })

        btnBack.setOnClickListener {
            _parentActivity?.onBackPressed()
        }
    }

    private fun initGoogleMap(){
        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if(mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment!!).commit()
            mapFragment.getMapAsync(this)
        }
    }
    override fun onMapReady(p0: GoogleMap) {
        LogHX.d("onMapReady", "Ok")
        this.gMap = p0
        setListenerMap()
        //setTheme()
        setupMap()
        drawMarkers()
        //setObservers()
        update()
    }

    private fun setListenerMap(){
        gMap?.setOnMarkerClickListener {marker ->
            viewModel.placesLive.value?.let { placeList ->
                val index = placeList.indexOfFirst { it.marker ==  marker}
                if(index > -1){
                    viewModel.oldSelectedPos.value = viewModel.currentPosSelected.value
                    viewModel.currentPosSelected.value = index

                    if(rvPlaces.visibility == View.VISIBLE)
                        rvPlaces.scrollToPosition(index)
                    else rvServices.scrollToPosition(index)
                }
            }
            true
        }
    }

    private fun setupMap(){
        viewModel.findConfig().observe(this, Observer { mapConfigurations ->
            viewModel.mapConfigLive.value = mapConfigurations
            mapConfigurations?.let { mapConfig ->
                //limitBoundsBuilder.include(LatLng(mapConfig.bound1Lat ?: 0.0, mapConfig.bound1Lon ?: 0.0))
                //limitBoundsBuilder.include(LatLng(mapConfig.bound2Lat ?: 0.0, mapConfig.bound2Lon ?: 0.0))
                limitBoundsBuilder.include(LatLng(20.584369,-87.113797))
                limitBoundsBuilder.include(LatLng(20.590522,-87.107225))

                gMap?.setLatLngBoundsForCameraTarget(limitBoundsBuilder.build())
                gMap?.setMinZoomPreference(mapConfig.minZoom?.toFloat() ?: minZoom)
                gMap?.setMaxZoomPreference(mapConfig.maxZoom?.toFloat() ?: maxZoom)
                gMap?.isBuildingsEnabled = false
                gMap?.uiSettings?.isCompassEnabled = true
                gMap?.uiSettings?.isTiltGesturesEnabled = false
                gMap?.uiSettings?.isRotateGesturesEnabled = false
                gMap?.uiSettings?.isMyLocationButtonEnabled = false
                goTo(LatLng(mapConfig.latitude ?: 0.0, mapConfig.longitude ?: 0.0), mapConfig.defaultZoom?.toFloat() ?: defaultZoom)
                LogHX.d("setupMap", "Ok")
                setupOverlay(mapConfig)
            }
        })
    }

    private fun setTheme(resStyle: Int){
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success: Boolean? = gMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), resStyle
                )
            )
            if (success == false) {
                LogHX.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            LogHX.e(TAG, "Can't find style. Error: $e")
        }
    }

    private fun setupOverlay(mapConfig: MapConfig){
        val resourceId = Utils.getDrawableId(requireContext(), mapConfig.imgOverlay)
        resourceId?.let {
            val overlayBoundsBuilder = LatLngBounds.Builder()
            //overlayBoundsBuilder.include(LatLng(mapConfig.overlay1Lat ?: 0.0, mapConfig.overlay1Lon ?: 0.0))
            //overlayBoundsBuilder.include(LatLng(mapConfig.overlay2Lat ?: 0.0, mapConfig.overlay2Lon ?: 0.0))
            overlayBoundsBuilder.include(LatLng(20.584369,-87.113797))
            overlayBoundsBuilder.include(LatLng(20.590522,-87.107225))
            viewModel.limitLatLngBoundsLive.value = overlayBoundsBuilder.build()

            val drawable = ContextCompat.getDrawable(requireContext(), resourceId)
            val metrics = resources.displayMetrics
            val defaultWidth = metrics.widthPixels
            val defaultHeight = metrics.heightPixels
            val intrinsicWidth = drawable?.intrinsicWidth ?: defaultWidth
            val intrinsicHeight = drawable?.intrinsicHeight ?: defaultHeight

            Glide.with(requireContext())
                .asBitmap()
                .load(resourceId)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into<CustomTarget<Bitmap>>(object : CustomTarget<Bitmap>(intrinsicWidth, intrinsicHeight) {

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val bgMap = BitmapDescriptorFactory.fromBitmap(resource)
                        val newarkMap = GroundOverlayOptions()
                            .image(bgMap)
                            .positionFromBounds(viewModel.limitLatLngBoundsLive.value!!)
                            .zIndex(0f)
                        setTheme(R.raw.style_map)
                        gMap?.addGroundOverlay(newarkMap)
                        LogHX.d("addGroundOverlay", "Ok")
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        setTheme(R.raw.style_map_error)
                    }
                })
        }

    }

    private fun goTo(position: LatLng, zoom: Float = defaultZoom){
        val currentZoom = gMap?.cameraPosition?.zoom ?: defaultZoom
        val fixZoom = if(currentZoom > defaultZoom) currentZoom else defaultZoom
        gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, fixZoom))
    }

    private fun getCircleDrawable(borderColor: Int, bodyColor: Int): Drawable{
        val drawable = GradientDrawable()
        drawable.color = ColorStateList.valueOf(bodyColor)
        drawable.setStroke(6, ContextCompat.getColor(requireContext(), borderColor))
        drawable.shape = GradientDrawable.OVAL
        return drawable
    }

    //region anim search
    private fun openSearch() {
        if(layoutSearch.visibility == View.VISIBLE) return
        viewModel.isOpenSearchInput.value = true
        edSearch.setText("")
        layoutSearch.visibility = View.VISIBLE
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            layoutSearch,
            (btnSearch.right + btnSearch.left) / 2,
            (btnSearch.top + btnSearch.bottom) / 2,
            0f, layoutSearch.width.toFloat()
        )
        circularReveal.duration = 300
        circularReveal.start()

        circularReveal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) {
                btnSearch.visibility = View.INVISIBLE
                circularReveal.removeAllListeners()
            }
        })
    }

    private fun closeSearch() {
        if(layoutSearch.visibility == View.INVISIBLE) return
        viewModel.isOpenSearchInput.value = false
        val circularConceal = ViewAnimationUtils.createCircularReveal(
            layoutSearch,
            (btnSearch.right + btnSearch.left) / 2,
            (btnSearch.top + btnSearch.bottom) / 2,
            layoutSearch.width.toFloat(), 0f
        )

        circularConceal.duration = 300
        circularConceal.start()
        circularConceal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) {
                layoutSearch.visibility = View.INVISIBLE
                btnSearch.visibility = View.VISIBLE
                edSearch.setText("")
                circularConceal.removeAllListeners()
            }
        })
    }
    //endregion

    //region mylocation
    @SuppressLint("MissingPermission")
    private fun getLocationPermission() {
        if (isGrantedLocationPermission()) {
            gMap?.isMyLocationEnabled = true
            getDeviceLocation()
        } else {
            gMap?.isMyLocationEnabled = false
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    private fun isGrantedLocationPermission(): Boolean{
        return (ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener { location : Location? ->
                location?.let {loc ->
                    val myLocation = LatLng(loc.latitude, loc.longitude)
                    val latLngBounds = viewModel.limitLatLngBoundsLive.value
                    latLngBounds?.let { mLatLngBounds ->
                        if(mLatLngBounds.contains(myLocation)) {
                            viewModel.myLiveLocation.value = LatLng(loc.latitude, loc.longitude)
                        }else {
                            viewModel.myLiveLocation.value = null
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            LogHX.e("Exception: ", e.message)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getLocationPermission()
            }
        }

    }



    //endregion
}