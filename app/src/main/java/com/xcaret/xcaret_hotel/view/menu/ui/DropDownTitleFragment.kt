package com.xcaret.xcaret_hotel.view.menu.ui


import android.view.View
import androidx.core.os.bundleOf
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.data.usecase.LangTitleUseCase
import com.xcaret.xcaret_hotel.domain.Title
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.*
import kotlinx.android.synthetic.main.dialog_drop_down_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DropDownTitleFragment<T>: BaseDialog() {

    private val titleUseCase: LangTitleUseCase by lazy { LangTitleUseCase() }
    private val langLabelUseCase = LangLabelUseCase()
    private val adapter = GenericAdapter<Title>(R.layout.item_title_spinner)
    private var listener: DropDownListener<T>? = null

    override fun getLayout(): Int = R.layout.dialog_drop_down_fragment

    override fun initComponents() {
        updateUI()
        recyclerGeneric.adapter = adapter
        setListeners()
        doAsync {
            val data = titleUseCase.all()
            val selected = arguments?.getString(Constants.DROP_DOWN_SELECTED)
            LogHX.i("seleced title", selected)
            data.filter { it.is_selected }.forEach { it.is_selected = false }
            data.find { it.value == selected }?.is_selected = true
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
            val label = langLabelUseCase.findLabelOutLive(getString(R.string.select_your_title))
            uiThread {
                label?.let { txtTitle.text = it.value ?: getString(R.string.lbl_select) }
            }
        }
    }

    private fun updateUI(){
        try {
            val params = statusBar.layoutParams
            params.height = (Utils.getScreenSize(requireContext()).heightPixels * 0.5).toInt()
            statusBar.layoutParams = params
        }catch (e: Exception) { e.printStackTrace() }
        Utils.fixHeightBottomView(auxNavigation)
        fullScreen()
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
    }

    companion object{
        const val TAG = "DropDownDialogFragmentTitle"

        fun newInstance(selected: String): DropDownTitleFragment<Title>{
            val args = bundleOf(Constants.DROP_DOWN_SELECTED to selected)
            val fragment = DropDownTitleFragment<Title>()
            fragment.arguments = args
            return fragment
        }
    }
}