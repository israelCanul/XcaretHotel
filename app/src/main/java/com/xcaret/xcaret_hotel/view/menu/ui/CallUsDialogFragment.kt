package com.xcaret.xcaret_hotel.view.menu.ui

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.domain.Contact
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.LogHX
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.config.discretescrollview.DSVOrientation
import com.xcaret.xcaret_hotel.view.config.discretescrollview.transform.DiscreteScrollItemTransformer
import com.xcaret.xcaret_hotel.view.menu.vm.CallUsViewModel
import kotlinx.android.synthetic.main.dialog_callus_fragment.*
import org.jetbrains.anko.support.v4.runOnUiThread


class CallUsDialogFragment: BaseDialog(){

    private val callUseViewModel = CallUsViewModel()

    override fun getLayout(): Int = R.layout.dialog_callus_fragment
    private val contactAdapter = GenericAdapter<Contact>(R.layout.item_contact)

    override fun initComponents() {
        fullScreen()
        setColorNavBar()
        setColorStatusBar()
        Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        rvContacts.setOrientation(DSVOrientation.VERTICAL)
        rvContacts.setOverScrollEnabled(true)
        rvContacts.setSlideOnFling(true)
        rvContacts.setItemTransformer(CustomTransformers())
        rvContacts.adapter = contactAdapter
        callUseViewModel.hotelLiveData.value = _parentActivity?.currentHotel
        callUseViewModel.typeFilterLive.value = arguments?.getInt(FILTER_TYPE)
        callUseViewModel.valueFilterLive.value = arguments?.getString(FILTER_VALUE)
        setObservers()
        setListeners()
    }

    private fun setObservers(){
        callUseViewModel.findTitle().observe(this, Observer {label ->
            label?.let { txtTitle.text = it.value }
        })
        when(callUseViewModel.typeFilterLive.value) {
            UID -> {
                callUseViewModel.categoryCallUseLiveData.value = callUseViewModel.valueFilterLive.value
                setObserverContact()
            }
            CODE -> {
                callUseViewModel.findCategoryByCode().observe(this, Observer {
                    callUseViewModel.categoryCallUseLiveData.value = it?.uid
                    setObserverContact()
                })
            }
        }
    }

    private fun setObserverContact(){
        callUseViewModel.findContactsByCategoryAndHotel().removeObservers(this)
        progress.visibility = View.VISIBLE
        rvContacts.visibility = View.GONE
        callUseViewModel.findContactsByCategoryAndHotel().observe(this, Observer {
            callUseViewModel.currentPosition.value = 0
            contactAdapter.addItems(it)
            runOnUiThread {
                rvContacts.visibility = View.VISIBLE
                progress.visibility = View.GONE
            }
        })
    }

    private fun setListeners(){
        txtClose.setOnClickListener { dismiss() }

        contactAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val currenPos = callUseViewModel.currentPosition.value
                if(currenPos == -1) return
                if(currenPos == position){
                    logEvent(
                        AnalyticConstant.ID_CALLFROM_CODE,
                        AnalyticConstant.ITEM_NAME_CALL_FORM_CODE,
                        AnalyticConstant.CONTENT_TYPE_MENU)
                    openDialer(contactAdapter.getItem(position)?.value ?: "")
                }
            }
        })

        rvContacts.addOnItemChangedListener { _, adapterPosition ->
            callUseViewModel.currentPosition.value = adapterPosition
        }
    }

    private fun openDialer(number: String){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

    class CustomTransformers: DiscreteScrollItemTransformer {

        override fun transformItem(item: View?, position: Float) {
            val closenessToCenter = 1f - Math.abs(position)
            val textActive = item?.findViewById<TextView>(R.id.contactName)?.text ?: ""
            val textInactive = item?.findViewById<TextView>(R.id.contactName2)?.text ?: ""
            LogHX.d("transformItem", "active: $textActive inactive: $textInactive pos: $closenessToCenter")
            item?.let {view ->
                if(closenessToCenter < 1.0f){
                    view.findViewById<ViewGroup>(R.id.layoutActive).visibility = View.GONE
                    view.findViewById<ViewGroup>(R.id.layoutInactive).visibility = View.VISIBLE
                }else{
                    view.findViewById<ViewGroup>(R.id.layoutActive).visibility = View.VISIBLE
                    view.findViewById<ViewGroup>(R.id.layoutInactive).visibility = View.GONE
                }
            }
        }

    }

    companion object{
        const val TAG = "CallUsDialogFragment"

        const val FILTER_VALUE = "FILTER_VALUE"
        const val FILTER_TYPE = "FILTER_TYPE"
        const val CODE = 1
        const val UID = 2

        fun newInstance(filterType: Int, filterValue: String): CallUsDialogFragment {
            val fragment = CallUsDialogFragment()
            val args = bundleOf(
                FILTER_TYPE to filterType,
                FILTER_VALUE to filterValue
            )
            fragment.arguments = args
            return fragment
        }
    }

}