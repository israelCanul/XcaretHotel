package com.xcaret.xcaret_hotel.view.general.ui

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.WeatherFragmentBinding
import com.xcaret.xcaret_hotel.domain.Weather
import com.xcaret.xcaret_hotel.domain.WebCam
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.general.vm.WeatherViewModel
import kotlinx.coroutines.launch
import org.jetbrains.anko.textResource

class WeatherFragment: BaseFragmentDataBinding<WeatherViewModel, WeatherFragmentBinding>(){
    override fun getLayout(): Int = R.layout.weather_fragment
    override fun getViewModelClass(): Class<WeatherViewModel> = WeatherViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val webCamAdapter = GenericAdapter<WebCam>(R.layout.item_webcam, 0.33f, 0f)
    private val dayWeatherAdapter = GenericAdapter<Weather>(R.layout.item_weather_day, 0.18f, 0f)

    override fun setupUI() {
        showSystemUI()
        initComponents()
        setObservers()
    }

    private fun initComponents(){
        Utils.fixHeightTopView(binding.auxHeaderLayout)
        binding.rvWebCams.adapter = webCamAdapter
        binding.rvWeatherWeek.adapter = dayWeatherAdapter
        viewModel.currentHotel.value = _parentActivity?._viewModel?.currentHotelLive?.value

        setListeners()
    }

    private fun setObservers(){
        viewModel.celsiusActive.observe(this, Observer {
            dayWeatherAdapter.getItems().forEach { weather -> weather.isCelsiusActive = it }
            dayWeatherAdapter.notifyDataSetChanged()
        })

        viewModel.getWeatherByDay().observe(this, Observer {
            viewModel.currentWeather.value = it
        })

        viewModel.getWeathersLimitTo().observe(this, Observer {
            it.forEachIndexed { index, weather ->
                val colorBackground = if(index%2 == 0) R.color.colorWeatherInpar else R.color.colorBackground1
                weather.colorBackground = ContextCompat.getColor(requireContext(), colorBackground)
                weather.isCelsiusActive = viewModel.celsiusActive.value ?: true
            }
            formatRangeDate(it)
            dayWeatherAdapter.addItems(it)
        })

        viewModel.findByCodeLiveWithoutHotel().observe(this, Observer {
            viewModel.categoryLive.value = it
            setObserverLive()
        })

        viewModel.currentMetric.asLiveData().observe(this, Observer {
            val resourceText = when(it){
                SettingsManager.TemperatureMetric.Celsius -> R.string.lbl_weather_cel_selected
                else -> R.string.lbl_weather_faren_selected
            }
            viewModel.celsiusActive.value = it == SettingsManager.TemperatureMetric.Celsius
            binding.txtCurrentMetric.textResource = resourceText
        })
    }

    private fun setObserverLive(){
        viewModel.getAllWebCamsByCategory().removeObservers(this)
        viewModel.getAllWebCamsByCategory().observe(this, Observer {
            webCamAdapter.addItems(it)
        })

    }

    private fun formatRangeDate(weather: List<Weather>){
        var txtRange = ""
        if(weather.isNotEmpty()){
            txtRange += DateUtil.changeFormatDate(weather.first().id, DateUtil.DATE_FORMAT_WEATHER, DateUtil.DAY_MONTH_FORMAT_TEXT)

            if(weather.size > 1){
                txtRange += " - " + DateUtil.changeFormatDate(weather.last().id, DateUtil.DATE_FORMAT_WEATHER, DateUtil.DAY_MONTH_FORMAT_TEXT)
            }
        }
        binding.txtRangeDate.text = txtRange
    }

    private fun setListeners(){
        webCamAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val webCam = webCamAdapter.getItem(position)
                webCam?.let { wc ->
                    wc.videoId?.let {
                        val strCode = "${webCam.code}_${viewModel.currentHotel.value?.code}"
                        logEvent(AnalyticConstant.ID_WEBCAM_CODE.replace("code_xx",strCode),
                        AnalyticConstant.ITEM_NAME_WEBCAM_CODE.replace("code_xx",strCode),
                        AnalyticConstant.CONTENT_TYPE_WEBCAM)

                        val args = bundleOf(Constants.VIDEO_ID to it)
                        findNavController().navigate(R.id.action_weatherFragment_to_youTubePlayerActivity, args)
                    }
                }
            }
        })

        binding.switchWeather.setOnClickListener {
            lifecycleScope.launch {
                val metric = if(viewModel.celsiusActive.value == true)
                    SettingsManager.TemperatureMetric.Fahrenheit
                else SettingsManager.TemperatureMetric.Celsius
                viewModel.setCurrentMetric(metric)
            }
        }

    }
}