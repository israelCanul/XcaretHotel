package com.xcaret.xcaret_hotel.view.menu.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Filter
import androidx.core.os.bundleOf
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.data.usecase.StateUseCase
import com.xcaret.xcaret_hotel.domain.State
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.dialog_drop_down_fragment.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.Normalizer

class DropDownStateFragment<T>: BaseDialog() {

    private val stateUseCase: StateUseCase by lazy { StateUseCase() }
    private val langLabelUseCase = LangLabelUseCase()
    private val adapter = GenericAdapter<State>(R.layout.item_state_spinner)
    private var listener: DropDownListener<T>? = null

    override fun getLayout(): Int = R.layout.dialog_drop_down_fragment

    override fun initComponents() {
        updateUI()
        recyclerGeneric.adapter = adapter
        setListeners()
        doAsync {
            val countryId = arguments?.getInt(Constants.DROP_DOWN_COUNTRY_ID) ?: -1
            val data = stateUseCase.allByCountryId(countryId)
            val selected = arguments?.getString(Constants.DROP_DOWN_SELECTED)
            data.filter { it.is_selected }.forEach { it.is_selected = false }
            data.find { it.abbreviation == selected }?.is_selected = true
            val itemSelected = data.find { it.is_selected }
            uiThread {
                recyclerGeneric.visibility = View.VISIBLE
                progress.visibility = View.GONE
                adapter.addItems(data)

                itemSelected?.let { iSelected ->
                    recyclerGeneric.scrollToPosition(data.indexOf(iSelected))
                }
            }
        }

        doAsync {
            val label = langLabelUseCase.findLabelOutLive(getString(R.string.select_your_state))
            uiThread {
                label?.let { txtTitle.text = it.value ?: getString(R.string.lbl_select) }
            }
        }
    }

    private fun updateUI(){
        fullScreen()
        Utils.fixHeightTopView(statusBar)
        txtTitle.makeGone()
        layoutFilter.makeVisible()
        layoutMain.backgroundResource = R.color.colorBackground1
        Utils.fixHeightBottomView(auxNavigation)
    }

    fun addListener(listener: DropDownListener<T>){
        this.listener = listener
    }

    private fun setListeners(){
        txtClose.setOnClickListener {
            dismiss()
        }

        adapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                adapter.getItems().filter { it.is_selected }.forEach { it.is_selected = false }
                adapter.getItems()[position].is_selected = true
                adapter.notifyDataSetChanged()
                listener?.onSelectedItem(adapter.getItem(position) as T)
            }
        })

        adapter.addFilter(object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val filterResults = FilterResults()
                if(constraint.isNullOrEmpty())
                    filterResults.values = adapter.getOriginalItems()
                else {
                    var queryString = constraint.toString().trim()
                    queryString = queryString.normalize()
                    adapter.getItems().forEach { State ->
                        State.name_normalized = State.name?.normalize()
                    }
                    filterResults.values = adapter.getItems().filter {
                        it.name_normalized?.contains(queryString, ignoreCase = true) == true
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    results?.values?.let { values ->
                        val items = values as List<State>
                        adapter.addFilterResult(items)
                    }
                }catch (e: Exception){}
            }
        })

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter?.filter(s)
            }
        })
    }

    fun removeAccent(src:String):String{
        val str = Normalizer.normalize(src,Normalizer.Form.NFD).replace("[^\\p{ASCII}]", "")
        return str
    }

    override fun dismiss() {
        super.dismiss()
        this.listener = null
        adapter.setOnListItemViewClickListener(null)
    }

    companion object{
        const val TAG = "DropDownDialogFragmentState"

        fun newInstance(selected: String, countryId: Int): DropDownStateFragment<State>{
            val args = bundleOf(
                Constants.DROP_DOWN_SELECTED to selected,
                Constants.DROP_DOWN_COUNTRY_ID to countryId
            )
            val fragment = DropDownStateFragment<State>()
            fragment.arguments = args
            return fragment
        }
    }
}