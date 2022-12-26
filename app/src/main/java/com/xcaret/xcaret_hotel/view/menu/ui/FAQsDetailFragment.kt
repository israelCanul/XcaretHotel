package com.xcaret.xcaret_hotel.view.menu.ui

import android.view.View
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.FragmentFaqsDetailBinding
import com.xcaret.xcaret_hotel.domain.Faq
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.onClick
import com.xcaret.xcaret_hotel.view.menu.vm.FaqDetailViewModel

class FAQsDetailFragment: BaseFragmentDataBinding<FaqDetailViewModel, FragmentFaqsDetailBinding>() {

    override fun getLayout(): Int = R.layout.fragment_faqs_detail
    override fun getViewModelClass(): Class<FaqDetailViewModel> = FaqDetailViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val qaAdapter = GenericAdapter<Faq>(R.layout.item_expandable_parent)

    override fun setupUI() {
        initComponents()
        setObservers()
        setListeners()

    }

    private fun setListeners() {
        binding.btnBack.onClick {
            _parentActivity?.popBackStack()
        }
        qaAdapter.setOnListItemViewClickListener(object: GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                if(view.id == R.id.icShowAnswer){
                    qaAdapter.getItems().find { it.showQuestion }?.let { collapse ->
                        val posToCollapse = qaAdapter.getItems().indexOf(collapse)
                        collapse.showQuestion = false
                        qaAdapter.notifyItemChanged(posToCollapse)
                    }

                    val toExpand = qaAdapter.getItem(position)
                    toExpand?.let { te ->
                        te.showQuestion = true
                        qaAdapter.notifyItemChanged(position)
                    }
                }else if(view.id == R.id.icHideAnswer){
                    val toCollapse = qaAdapter.getItem(position)
                    toCollapse?.let { tc ->
                        tc.showQuestion = false
                        qaAdapter.notifyItemChanged(position)
                    }
                }
            }
        })
    }

    private fun initComponents() {
        viewModel.parentUid.value = arguments?.getString(Constants.CATEGORY_FAQ_ID)
        viewModel.FAQTitle.value = arguments?.getString(Constants.CATEGORY_FAQ_TITLE)
        Utils.fixHeightTopView(binding.statusBarFix)
        binding.rvFaqsQA.adapter = qaAdapter
        binding.txtDescriptionBar.text = viewModel.FAQTitle.value

    }

    private fun setObservers() {
        viewModel.parentUid.observe(this,{ parentUid ->
            viewModel.getFaqs()
        })
        viewModel.FaqList.observe(this, {list ->
            if (list.isNotEmpty()){
                list.let {
                    qaAdapter.addItems(it)
                }

            }
        })
    }

}