package com.xcaret.xcaret_hotel.view.menu.ui

import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.base.BaseDialog
import com.xcaret.xcaret_hotel.view.config.labelview.view.LabelView
import com.xcaret.xcaret_hotel.view.config.onClick
import kotlinx.android.synthetic.main.dialog_take_or_choose_profile_image.*

class ProfilePictureDialog: BaseDialog() {

    private var profilePictureInterface: ProfilePictureInterface? = null

    override fun getLayout(): Int = R.layout.dialog_take_or_choose_profile_image

    override fun initComponents() {
        fullScreen()
        setColorNavBar()
        setColorStatusBar()
        addListenerToOption(btnTakePhoto, TypeSelectPicture.TAKE_PHOTO)
        addListenerToOption(btnChoosePhoto, TypeSelectPicture.CHOOSE_PHOT)
        addListenerToOption(btnCancel, TypeSelectPicture.CANCEL)
    }

    private fun addListenerToOption(labelView: LabelView, option: TypeSelectPicture){
        labelView.onClick {
            profilePictureInterface?.onSelectedOption(option)
        }
    }

    fun setProfilePictureInterface(profilePictureInterface: ProfilePictureInterface){
        this.profilePictureInterface = profilePictureInterface
    }

    interface ProfilePictureInterface{
        fun onSelectedOption(typeSelected: TypeSelectPicture)
    }

    enum class TypeSelectPicture(value: Int) {
        TAKE_PHOTO(0),
        CHOOSE_PHOT(1),
        CANCEL(2)
    }

    companion object{
        const val TAG = "ProfilePictureDialog"
        fun newInstance() = ProfilePictureDialog()
    }
}