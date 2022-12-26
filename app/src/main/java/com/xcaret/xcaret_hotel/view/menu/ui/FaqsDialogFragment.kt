package com.xcaret.xcaret_hotel.view.menu.ui

import android.view.View
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.domain.Faq
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.menu.vm.FaqViewModel
import kotlinx.android.synthetic.main.dialog_language_fragment.*
import kotlinx.android.synthetic.main.dialog_language_fragment.auxNavigation
import kotlinx.android.synthetic.main.fragment_faqs_dialog.*

class FaqsDialogFragment : BaseDialog() {

    private val viewModel = FaqViewModel()
    private var faqsAdapter = GenericAdapter<Faq>(R.layout.item_expandable_parent)

    override fun getLayout(): Int = R.layout.fragment_faqs_dialog

    override fun initComponents() {
        isCancelable = true
        fullScreen()
        Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        rvFaqs.adapter = faqsAdapter
        setObserver()
        setListeners()
    }

    private fun setObserver(){
//        viewModel.getFaqs().observe(this, {Faqs->
//            val list = Faqs
//            faqsAdapter.addItems(Faqs)
//
//        })
    }
    private fun setListeners(){
        txtCloseFaq.setOnClickListener{dismiss()}
        faqsAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if(view.id == R.id.icShowAnswer){
                    faqsAdapter.getItems().find { it.showQuestion }?.let { collapse ->
                        val posToCollapse = faqsAdapter.getItems().indexOf(collapse)
                        collapse.showQuestion = false
                        faqsAdapter.notifyItemChanged(posToCollapse)
                    }

                    val toExpand = faqsAdapter.getItem(position)
                    toExpand?.let { te ->
                        te.showQuestion = true
                        faqsAdapter.notifyItemChanged(position)
                    }
                }else if(view.id == R.id.icHideAnswer){
                    val toCollapse = faqsAdapter.getItem(position)
                    toCollapse?.let { tc ->
                        tc.showQuestion = false
                        faqsAdapter.notifyItemChanged(position)
                    }
                }
            }
        })

    }


    companion object {
        const val TAG =" FaqsDialogFragment"
        @JvmStatic
        fun newInstance() :FaqsDialogFragment {
            return FaqsDialogFragment() }
    }
}