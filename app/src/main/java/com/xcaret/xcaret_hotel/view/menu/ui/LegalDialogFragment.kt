package com.xcaret.xcaret_hotel.view.menu.ui

import android.view.View
import androidx.core.os.bundleOf
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.usecase.LangLabelUseCase
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.Constants
import com.xcaret.xcaret_hotel.view.config.Utils
import kotlinx.android.synthetic.main.dialog_legal_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class LegalDialogFragment: BaseDialog(){

    private val labelUseCase = LangLabelUseCase()

    private var keyTitle = ""
    private var keyContent = ""

    override fun getLayout(): Int = R.layout.dialog_legal_fragment

    override fun initComponents() {
        fullScreen()
        setListeners()
        setColorStatusBar()
        setColorNavBar()
        Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        val legalType = arguments?.getInt(Constants.LEGAL_TYPE) ?: Constants.LEGAL_TYPE
        keyTitle = getString(R.string.rkey_profile_terms_conditions)
        keyContent = getString(R.string.rkey_terms_condition_content)

        if(legalType == Constants.NOTICE_PRIVACY){
            keyTitle = getString(R.string.rkey_profile_notice_privacy)
            keyContent = getString(R.string.rkey_notice_privacy_content)
        }

        loadInfo()
    }

    private fun loadInfo(){
        doAsync {
            showLoading()
            val title = labelUseCase.findLabelOutLive(keyTitle)
            val content = labelUseCase.findLabelOutLive(keyContent)
            uiThread {
                txtTitle.text = title?.value
                txtContent.text = content?.value
                dismissLoading()
            }
        }
    }

    private fun showLoading(){
        txtTitle.visibility = View.GONE
        txtContent.visibility = View.GONE
        progress.visibility = View.VISIBLE
    }

    private fun dismissLoading(){
        progress.visibility = View.GONE
        txtTitle.visibility = View.VISIBLE
        txtContent.visibility = View.VISIBLE
    }

    private fun setListeners(){
        txtClose.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG_TERMS = "LegalDialogFragmentTerms"
        const val TAG_PRIVACY = "LegalDialogFragmentPrivacy"

        fun newInstance(legalType: Int): LegalDialogFragment {
            val fragment = LegalDialogFragment()
            val args = bundleOf(Constants.LEGAL_TYPE to legalType)
            fragment.arguments = args
            return fragment
        }
    }
}