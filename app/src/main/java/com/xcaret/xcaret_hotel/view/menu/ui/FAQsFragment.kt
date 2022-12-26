package com.xcaret.xcaret_hotel.view.menu.ui

import android.view.View
import androidx.core.os.bundleOf
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.FragmentFaqsBinding
import com.xcaret.xcaret_hotel.domain.LangCategory
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.GenericAdapter
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.onClick
import com.xcaret.xcaret_hotel.view.menu.vm.FaqViewModel

class FAQsFragment: BaseFragmentDataBinding<FaqViewModel, FragmentFaqsBinding>() {

    override fun getLayout(): Int = R.layout.fragment_faqs
    override fun getViewModelClass(): Class<FaqViewModel> = FaqViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val langCategoryAdapter = GenericAdapter<LangCategory>(R.layout.item_faqs_category)

    override fun setupUI() {
        initComponents()
        setObservers()
        setListeners()

    }

    private fun setListeners() {
        binding.btnBack.onClick {
            _parentActivity?.popBackStack()
        }
        langCategoryAdapter.setOnListItemViewClickListener(object :GenericAdapter.OnListItemViewClickListener{
            override fun onClick(view: View, position: Int) {
                val rest = langCategoryAdapter.getItem(position)
                val args = bundleOf(Constants.CATEGORY_FAQ_ID to rest?.parent_uid,
                    Constants.CATEGORY_FAQ_TITLE to rest?.title)
                navigate(R.id.action_FaqFragment_to_FaqDetailFragment, args)
            }

        })
    }

    private fun initComponents() {
        Utils.fixHeightTopView(binding.statusBarFix)
        binding.rcCategory.adapter = langCategoryAdapter
    }

    private fun setObservers() {

        viewModel.getFAQParent().observe(this, {parent ->
            parent.let {
                viewModel.getCategoriesFAQ(parent?.uid!!)
            }
        })

        viewModel.listCategoriesChilds.observe(this,{list->
            if (list != null){
                langCategoryAdapter.addItems(list as List<LangCategory>)
            }
        })

    }
}