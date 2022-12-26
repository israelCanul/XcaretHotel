package com.xcaret.xcaret_hotel.view.quote.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.DialogCurrencyBinding
import com.xcaret.xcaret_hotel.domain.Currency
import com.xcaret.xcaret_hotel.view.base.BaseDialogBinding
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.Language
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.discretescrollview.DSVOrientation
import com.xcaret.xcaret_hotel.view.config.onClick
import com.xcaret.xcaret_hotel.view.menu.ui.CallUsDialogFragment
import com.xcaret.xcaret_hotel.view.quote.vm.CurrencyViewModel
import kotlinx.android.synthetic.main.dialog_currency.*
import kotlinx.android.synthetic.main.dialog_currency.txtClose
import org.jetbrains.anko.support.v4.runOnUiThread

class CurrencyDialog: BaseDialogBinding<CurrencyViewModel, DialogCurrencyBinding>() {
    override fun getLayout(): Int = R.layout.dialog_currency
    override fun getViewModelClass(): Class<CurrencyViewModel> = CurrencyViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    override fun setupUI() {}

    private val currencyAdapter = GenericAdapter<Currency>(R.layout.item_currency)
    private var currencyListener: CurrencyListener? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTransitionStyle(R.style.DialogAnimation)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        fullScreen()
        setTransparentStatusBar()
        setTransparentNavigationBar()

        //hideSystemUI()
        Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        rvCurrency.setOrientation(DSVOrientation.VERTICAL)
        rvCurrency.setOverScrollEnabled(true)
        rvCurrency.setSlideOnFling(true)
        rvCurrency.setItemTransformer(CallUsDialogFragment.CustomTransformers())
        rvCurrency.adapter = currencyAdapter

        setListeners()
        setObservers()
    }

    private fun setObservers(){
        viewModel.data.observe(this, Observer {
            runOnUiThread {
                currencyAdapter.addItems(it)
                val currency = Language.getCurrency(requireContext())
                it.find { it.iso.equals(currency, ignoreCase = true) }?.let { default ->
                    val index = it.indexOf(default)
                    rvCurrency.scrollToPosition(index)
                }
            }
        })

        viewModel.all().observe(this, Observer {
            runOnUiThread { viewModel.data.value = it }
        })
    }

    private fun setListeners(){
        txtClose.onClick { dismiss() }

        currencyAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val currency = Language.getCurrency(requireContext())
                val newCurrency = currencyAdapter.getItem(position)
                if(!currency.equals(newCurrency?.iso ?: Language.LANG_CURRENCY_ISO_DEFAULT, ignoreCase = true))
                    currencyListener?.onSelectCurrency(currencyAdapter.getItem(position)!!)
            }
        })
    }

    fun addCurrencyListener(currencyListener: CurrencyListener): CurrencyDialog{
        this.currencyListener = currencyListener
        return this
    }

    companion object{
        const val TAG = "CurrencyDialog"

        fun newInstance() = CurrencyDialog()
    }

    interface CurrencyListener{
        fun onSelectCurrency(item: Currency)
    }
}