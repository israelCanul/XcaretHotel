package com.xcaret.xcaret_hotel.view.menu.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.DialogAddEmailBinding
import com.xcaret.xcaret_hotel.view.base.BaseDialogBinding
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.onClick
import com.xcaret.xcaret_hotel.view.menu.vm.AddEMailViewModel
import kotlinx.android.synthetic.main.dialog_add_email.*

class AddEmailDialog: BaseDialogBinding<AddEMailViewModel, DialogAddEmailBinding>() {
    override fun getLayout(): Int = R.layout.dialog_add_email

    override fun getViewModelClass(): Class<AddEMailViewModel> = AddEMailViewModel::class.java

    override fun bindViewToModel() { binding.viewModel = viewModel }

    override fun setupUI() {}

    private var addEmailListener: AddEmailListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        fullScreen()
        Utils.fixHeightTopView(statusBar)
        Utils.fixHeightBottomView(auxNavigation)
        isCancelable = false
        setListeners()
    }

    private fun setListeners(){
        btnNext.onClick {
            val email = etEmail.text.toString()
            val confirmEmail = etConfirmEmail.text.toString()

            val isValid = viewModel.isValidAddEmail(email, confirmEmail)
            if(isValid.hasError){
                if(isValid.email != 0) etEmail.error = getString(isValid.email)
                else etEmail.error = null

                if(isValid.confirmEmail != 0) etConfirmEmail.error = getString(isValid.confirmEmail)
                else etConfirmEmail.error = null
            }else{
                addEmailListener?.changeEmail(this, email)
            }
        }

        txtClose.onClick {
            addEmailListener?.closeDialog(this)
        }
    }

    fun setEmailListener(addEmailListener: AddEmailListener): AddEmailDialog{
        this.addEmailListener = addEmailListener
        return this
    }

    companion object{
        const val TAG = "AddEmailDialog"
        fun newInstance() = AddEmailDialog()
    }

    interface AddEmailListener{
        fun changeEmail(dialog: DialogFragment, email: String)
        fun closeDialog(dialog: DialogFragment)
    }
}