package com.xcaret.xcaret_hotel.view.menu.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.domain.Language
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.discretescrollview.DSVOrientation
import com.xcaret.xcaret_hotel.view.menu.vm.LanguageViewModel
import kotlinx.android.synthetic.main.dialog_language_fragment.*
import java.lang.Exception


class LanguageDialogFragment: BaseDialog(){

    private val lifecycleOwner: LifecycleOwner? by lazy {
        context as? LifecycleOwner
    }

    private val viewModel = LanguageViewModel()
    private val languageAdapter = GenericAdapter<Language>(R.layout.item_laguage)

    override fun getLayout(): Int = R.layout.dialog_language_fragment
    override fun initComponents() {
        isCancelable = true
        fullScreen()
        setColorNavBar()
        Utils.fixHeightBottomView(auxNavigation)
        rvLanguages.setOrientation(DSVOrientation.VERTICAL)
        rvLanguages.setOverScrollEnabled(true)
        rvLanguages.setSlideOnFling(true)
        rvLanguages.setItemTransformer(CallUsDialogFragment.CustomTransformers())
        rvLanguages.adapter = languageAdapter

        setObserver()
        setListeners()
    }

    private fun setObserver(){
        viewModel.all().observe(this, Observer {
            it.forEach { lang -> lang.selected = false }
            var i = 0
            it.forEachIndexed { index, lang ->
                lang.selected = lang.code.equals(com.xcaret.xcaret_hotel.view.config.Language.getLangCode(requireContext()), ignoreCase = true)
                if(lang.selected)
                    i = index
            }
            languageAdapter.addItems(it)
            if(i > 0 && it.size > i)
                rvLanguages.apply {
                    scrollToPosition(i)
                }
        })

        viewModel.findTitle().observe(this, Observer {label ->
            label?.let { txtTitle.text = it.value }
        })
    }

    private fun setListeners(){
        txtClose.setOnClickListener { dismiss() }

        languageAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                try{
                    val currentLang = com.xcaret.xcaret_hotel.view.config.Language.getLangCode(requireContext())
                    val currenPos = viewModel.currentPosition.value
                    if(currenPos == -1) return
                    if(currenPos == position) {
                        val langSelected = languageAdapter.getItem(position)
                        if (langSelected?.code == currentLang) return
                        com.xcaret.xcaret_hotel.view.config.Language.changeLanguage(
                            langSelected?.code ?: "",
                            langSelected?.countryCode ?: "",
                            langSelected?.nameSF ?: "",
                            requireContext()
                        ) {
                            _parentActivity?._viewModel?.currentHotelLive?.value = null
                            dismiss()
                            _parentActivity?.navigate(R.id.action_menuFragment_to_splash)
                        }
                    }
                }catch( exc:Exception){
                    Log.e("OnClick_Language",exc.toString())
                }
            }
        })

        rvLanguages.addOnItemChangedListener { _, adapterPosition ->
            viewModel.currentPosition.value = adapterPosition
        }
    }

    companion object{
        const val TAG = "LanguageDialogFragment"

        fun newInstance(): LanguageDialogFragment{
            return LanguageDialogFragment()
        }
    }

}