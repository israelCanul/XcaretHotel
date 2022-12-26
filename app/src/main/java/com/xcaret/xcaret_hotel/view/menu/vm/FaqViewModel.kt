package com.xcaret.xcaret_hotel.view.menu.vm

import android.provider.SyncStateContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcaret.xcaret_hotel.data.usecase.*
import com.xcaret.xcaret_hotel.domain.Category
import com.xcaret.xcaret_hotel.domain.Faq
import com.xcaret.xcaret_hotel.domain.LangCategory
import com.xcaret.xcaret_hotel.view.config.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FaqViewModel: ViewModel() {

    private val faqUseCase = FaqUseCase()
    private val categoryUseCase = CategoryUseCase()
    private val langCategoryUseCase = LangCategoryUseCase()

    var listCategoriesChilds = MutableLiveData<List<LangCategory?>>(null)

    //fun getFaqs():LiveData<List<Faq>> = faqUseCase.getFaqs()
    fun getCategoriesFAQ(filterGroup :String){
        viewModelScope.launch {
            val childCategories = withContext(Dispatchers.IO) {
                langCategoryUseCase.findByFilterGrouper(filterGroup)
            }

            if (childCategories.isNotEmpty()){
                listCategoriesChilds.value = childCategories
            }
        }
    }
    //fun getCategoriesFAQ(filterGroup :String):LiveData<List<LangCategory?>> = langCategoryUseCase.findByFilterGrouper("niWaHoXyolXmoySc3rq7Azyhj")
    fun getFAQParent():LiveData<Category?> = categoryUseCase.findCategoryByCodeLive(Constants.FAQ_CODE)





}