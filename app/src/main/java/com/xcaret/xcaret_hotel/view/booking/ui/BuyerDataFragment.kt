package com.xcaret.xcaret_hotel.view.booking.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.BuyerDataFragmentBinding
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.booking.vm.BuyerDataViewModel
import com.xcaret.xcaret_hotel.view.config.DropDownListener
import com.xcaret.xcaret_hotel.view.config.Utils
import com.xcaret.xcaret_hotel.view.config.onClick
import com.xcaret.xcaret_hotel.view.config.showSecure
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.menu.ui.DropDownCountryFragment
import com.xcaret.xcaret_hotel.view.menu.ui.DropDownStateFragment
import com.xcaret.xcaret_hotel.view.menu.ui.DropDownTitleFragment
import kotlinx.android.synthetic.main.buyer_data_fragment.*
import kotlinx.android.synthetic.main.buyer_data_fragment.btnNext
import kotlinx.android.synthetic.main.buyer_data_fragment.etAddress
import kotlinx.android.synthetic.main.buyer_data_fragment.etCP
import kotlinx.android.synthetic.main.buyer_data_fragment.etCity
import kotlinx.android.synthetic.main.buyer_data_fragment.etFirstName
import kotlinx.android.synthetic.main.buyer_data_fragment.etLastName
import kotlinx.android.synthetic.main.buyer_data_fragment.etPhone
import kotlinx.android.synthetic.main.buyer_data_fragment.layoutCountry
import kotlinx.android.synthetic.main.buyer_data_fragment.layoutState
import kotlinx.android.synthetic.main.buyer_data_fragment.layoutTitle
import kotlinx.android.synthetic.main.buyer_data_fragment.toolbar
import kotlinx.android.synthetic.main.buyer_data_fragment.txtPhoneCode
import kotlinx.android.synthetic.main.toolbar_generic.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BuyerDataFragment: BaseFragmentDataBinding<BuyerDataViewModel, BuyerDataFragmentBinding>() {
    override fun getLayout(): Int = R.layout.buyer_data_fragment
    override fun getViewModelClass(): Class<BuyerDataViewModel> = BuyerDataViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment.newInstance()
    }

    override fun setupUI(){
        viewModel.getDefaultTitle {
            viewModel.defaultTitleLiveData.value = it
            viewModel.buyerTitleLiveData.value = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents(){
        toolbar.txtToolbarTitle.text = getString(R.string.lbl_enter_your_data)
        toolbar.txtToolbarTitle.setKey(getString(R.string.rkey_lbl_enter_your_data))
        Utils.fixHeightTopView(toolbar)
        setObservers()
        setListeners()
        swUseSameData.isChecked = true
    }

    private fun getUserData(){
        loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
        viewModel.getUserData { user ->
            loadingDialog.dismiss()
            viewModel.buyerLiveData.value = user
            viewModel.email.value = user?.email

            user?.let { u ->
                doAsync {
                    val country = viewModel.findCountryByIso2(u.country_code)
                    val state = viewModel.findStateByAbbreviation(u.state_code)
                    val title = viewModel.findTitleByCode(u.title_code)
                    uiThread {
                        viewModel.buyerCountryLiveData.value = country
                        viewModel.buyerStateLiveData.value = state
                        viewModel.buyerTitleLiveData.value = title
                    }
                }
            }
        }
    }

    private fun setObservers(){
        viewModel.buyerCountryLiveData.observe(viewLifecycleOwner, Observer {
            setupPhoneNumber()
        })


    }

    private fun getDataForm(): User{
        var phone = etPhone.text.toString().trim()
        val phoneCode = txtPhoneCode.text.toString()
        if(phoneCode != getString(R.string.default_code_phone) && phone.isNotEmpty()){
            phone = "$phoneCode$phone"
        }

        val user = User(
            title_code = viewModel.buyerTitleLiveData.value?.value ?: "",
            firstName = etFirstName.text.toString().trim(),
            lastName = etLastName.text.toString().trim(),
            address = etAddress.text.toString().trim(),
            country_code = viewModel.buyerCountryLiveData.value?.iSO2 ?: "",
            state_code = viewModel.buyerStateLiveData.value?.abbreviation ?: "",
            objCountry = viewModel.buyerCountryLiveData.value,
            objState = viewModel.buyerStateLiveData.value,
            city = etCity.text.toString().trim(),
            cp = etCP.text.toString().trim(),
            phone = phone.replace("+", ""),
            special_request = etSpecialRequests.text.toString().trim(),
            iam_adult = swAdult.isChecked,
            email = viewModel.email.value ?: ""
        )
        return user
    }

    private fun showErrorForm(error: UserValidError){
        etFirstName.error = if(error.firstName != 0) getString(error.firstName) else null
        etLastName.error = if(error.lastName != 0) getString(error.lastName) else null
        etAddress.error = if(error.address != 0) getString(error.address) else null
        etCountry.error = if(error.country != 0) getString(error.country) else null
        etState.error = if(error.state != 0) getString(error.state) else null
        etCity.error = if(error.city != 0) getString(error.city) else null
        etCP.error = if(error.cp != 0) getString(error.cp) else null
        etPhone.error = if(error.phone != 0) getString(error.phone) else null
    }

    private fun setListeners(){
        toolbar.btnToolbarBack.onClick {
            _parentActivity?.onBackPressed()
        }
        btnNext.onClick {
            val user = getDataForm()
            val userValid = viewModel.isValidUser(user)
            if(userValid.hasError) showErrorForm(userValid)
            else{
                _parentActivity?._viewModel?.bookingData?.value?.buyerData = user.toBuyer()
                navigate(R.id.action_buyerDataFragment_to_pickupFragment)
            }
        }

        swUseSameData.setOnCheckedChangeListener { _, isChecked ->
            viewModel.useDataUser.value = isChecked
            if(isChecked) {
                getUserData()
            }
            else {
                _parentActivity?.showAlert(
                    icon = R.drawable.ic_info,
                    extra = _parentActivity?._viewModel?.currentHotelLive?.value?.name,
                    message = getString(R.string.rkey_lbl_modal_info_reservartion),
                    accept = getString(R.string.rkey_lbl_continue),
                    cancel = getString(R.string.rkey_btn_cancel),
                    acceptListener = object: DialogAlert.onClickListener{
                        override fun onClick(v: View?, dialog: DialogFragment) {
                            dialog.dismiss()
                            viewModel.buyerLiveData.value = null
                            viewModel.buyerTitleLiveData.value = viewModel.defaultTitleLiveData.value
                            viewModel.buyerCountryLiveData.value = null
                            viewModel.buyerStateLiveData.value = null
                        }
                    },
                    cancelListener = object: DialogAlert.onClickListener{
                        override fun onClick(v: View?, dialog: DialogFragment) {
                            dialog.dismiss()
                            swUseSameData.isChecked = true
                        }
                    }
                )



            }
        }

        swAdult.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isAdultLiveData.value = isChecked
        }

        layoutTitle.onClick {
            if(viewModel.useDataUser.value == false) showDialogTitle()
        }

        layoutCountry.onClick {
            if(viewModel.useDataUser.value == false) showDialogCountry()
        }

        layoutState.onClick {
            viewModel.buyerCountryLiveData.value?.let {
                if(viewModel.useDataUser.value == false) showDialogState()
            }
        }
    }

    private fun showDialogCountry(){
        val defaultCode = viewModel.buyerCountryLiveData.value?.iSO ?: ""
        val dialog = DropDownCountryFragment.newInstance(defaultCode)
        dialog.addListener(object: DropDownListener<Country> {
            override fun onSelectedItem(item: Country) {
                dialog.dismiss()
                viewModel.buyerCountryLiveData.value = item
            }
        })
        dialog.show(childFragmentManager, DropDownCountryFragment.TAG)
    }

    private fun showDialogTitle(){
        val defaultCode = if(viewModel.buyerTitleLiveData.value != null) viewModel.buyerTitleLiveData.value?.value else viewModel.defaultTitleLiveData.value?.value
        val dialog = DropDownTitleFragment.newInstance(defaultCode ?: "")
        dialog.addListener(object: DropDownListener<Title> {
            override fun onSelectedItem(item: Title) {
                dialog.dismiss()
                viewModel.buyerTitleLiveData.value = item
            }
        })
        dialog.show(childFragmentManager, DropDownTitleFragment.TAG)
    }

    private fun showDialogState(){
        val defaultCode = viewModel.buyerStateLiveData.value?.abbreviation ?: ""
        val dialog = DropDownStateFragment.newInstance(defaultCode, viewModel.buyerCountryLiveData.value?.id ?: 0)
        dialog.addListener(object: DropDownListener<State> {
            override fun onSelectedItem(item: State) {
                dialog.dismiss()
                viewModel.buyerStateLiveData.value = item
            }
        })
        dialog.show(childFragmentManager, DropDownStateFragment.TAG)
    }

    private fun setupPhoneNumber(){
        viewModel.buyerCountryLiveData.value?.let { country ->
            doAsync {
                val phoneCode = viewModel.findPhoneCodeByCountry(country.iSO ?: "")
                uiThread {
                    viewModel.buyerLiveData.value?.let { user ->
                        phoneCode?.let { pc ->
                            txtPhoneCode.text = "+${pc.code}"
                            if (user.phone.isNotEmpty()) {
                                var phone = "+${user.phone}"
                                phone = phone.replace("+${pc.code}", "")
                                    .replace("+ ${pc.code}", "")
                                etPhone.setText(phone)
                            }
                        } ?: kotlin.run {
                            txtPhoneCode.text = getString(R.string.default_code_phone)
                        }
                    } ?: kotlin.run {
                        phoneCode?.let { pc ->
                            txtPhoneCode.text = "+${pc.code}"
                        }?: kotlin.run {
                            txtPhoneCode.text = getString(R.string.default_code_phone)
                        }
                    }
                }
            }
        } ?: kotlin.run {
            etPhone.setText(viewModel.buyerLiveData.value?.phone)
        }
    }
}