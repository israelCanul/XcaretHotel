package com.xcaret.xcaret_hotel.view.menu.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.ProfileFragmentBinding
import com.xcaret.xcaret_hotel.domain.*
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.analytics.AnalyticConstant
import com.xcaret.xcaret_hotel.view.config.analytics.logEvent
import com.xcaret.xcaret_hotel.view.config.inputview.view.InputView
import com.xcaret.xcaret_hotel.view.config.labelview.view.LabelView
import com.xcaret.xcaret_hotel.view.general.ui.DialogAlert
import com.xcaret.xcaret_hotel.view.general.ui.DialogSnack
import com.xcaret.xcaret_hotel.view.general.ui.LoadingDialogFragment
import com.xcaret.xcaret_hotel.view.menu.vm.ProfileViewModel
import com.xcaret.xcaret_hotel.view.security.ui.DialogEnterCode
import kotlinx.android.synthetic.main.profile_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ProfileFragment: BaseFragmentDataBinding<ProfileViewModel, ProfileFragmentBinding>() {
    override fun getLayout(): Int = R.layout.profile_fragment
    override fun getViewModelClass(): Class<ProfileViewModel> = ProfileViewModel::class.java
    override fun bindViewToModel() { binding.viewModel = viewModel }

    private val loadingDialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment.newInstance()
    }

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val choosePictureDialog: ProfilePictureDialog by lazy {
        ProfilePictureDialog.newInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()

        logEvent(
            AnalyticConstant.ID_MENU_PROFILE,
            AnalyticConstant.ITEM_NAME_MENU_PROFILE,
            AnalyticConstant.CONTENT_TYPE_MENU)
    }

    override fun setupUI() {
        settingsManager.getUID.asLiveData().observe(this, Observer {
            viewModel.uidLiveData.value = it
            setObservers()
        })
    }

    private fun initComponents(){
        Utils.fixHeightTopView(statusBarFix)
        setListeners()
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            it?.email?.let { email ->
                if(email.isEmpty()){
                    addEmail()
                }
                else if(email != viewModel.oldEmailLiveData.value){
                    updateFirebaseFromSF()
                }
            }
        })
    }

    private fun setupPhoneNumber(){
        viewModel.userLiveData.value?.let { user ->
            viewModel.countryLiveData.value?.let { _ ->
                doAsync {
                    val phoneCode = viewModel.findPhoneCodeByCountry()
                    uiThread {
                        phoneCode?.let { pc ->
                            txtPhoneCode.text = "+${pc.code}"
                            if (user.phone.isNotEmpty()) {
                                if (etPhone.text.toString() != user.phone) {
                                    var newNumber = ""
                                    if (etPhone.text.toString().isNotEmpty()) {
                                        var phone = ""
                                        phone = if (etPhone.text?.contains("+") == true){
                                            "+${etPhone.text}"
                                        }else{
                                            etPhone.text.toString()
                                        }
                                        Log.e("etphone", phone)
                                        phone = phone.replace("+${pc.code}", "")
                                            .replace("+ ${pc.code}", "")
                                        newNumber = phone

                                    }else
                                    {
                                        var phone = "+${user.phone}"
                                        Log.e("SetphoneNUmber",phone)
                                        phone = phone.replace("+${pc.code}", "")
                                            .replace("+ ${pc.code}", "")

                                        newNumber = phone
                                    }
                                    etPhone.setText(newNumber)

                                }
                            }
//                            if(user.phone.isNotEmpty()){
//                                var phone = "+${user.phone}"
//                                Log.e("SetphoneNUmber",phone)
//                                phone = phone.replace("+${pc.code}", "")
//                                    .replace("+ ${pc.code}", "")
//                                    .replace(pc.code ?: "", "")
//                                etPhone.setText(phone)
//                            }
                        } ?: runOnUiThread { txtPhoneCode.text = getString(R.string.default_code_phone) }
                    }
                }
            } ?: kotlin.run { etPhone.setText(user.phone) }
        }
    }

    private fun setObservers(){
        viewModel.getSession().removeObservers(this)
        viewModel.firstTitle().removeObservers(this)

        viewModel.getSession().observe(viewLifecycleOwner, Observer {
            cleanObserversDropDown()
            viewModel.oldEmailLiveData.value = viewModel.userLiveData.value?.email ?: ""
            viewModel.userLiveData.value = it
            setObserversDropDown()
        })

        viewModel.firstTitle().observe(viewLifecycleOwner, Observer {
            viewModel.defaultTitleLiveData.value = it
        })
    }

    private fun updateFirebaseFromSF(){
        loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
        viewModel.getOrCreateProfileSF {
            if(it) setObservers()
            loadingDialog.dismissSecure()
        }
    }

    private fun setObserversDropDown(){
        viewModel.countryLiveData.observe(viewLifecycleOwner, Observer {
            setupPhoneNumber()
        })

        viewModel.findCountryByIso2().observe(viewLifecycleOwner, Observer {
            viewModel.countryLiveData.value = it
        })

        viewModel.findStateByAbbreviation().observe(viewLifecycleOwner, Observer {
            viewModel.stateLiveData.value = it
        })

        viewModel.findTitleByCode().observe(viewLifecycleOwner, Observer {
            viewModel.titleLiveData.value = it
        })
    }

    private fun confirmBack(){
        if(viewModel.activeEditProfile.value == true) {
            viewModel.activeEditProfile.value = false
            _parentActivity?.showErrorAlert(
                getString(R.string.rkey_my_profile_confirm_back),
                getString(R.string.rkey_lbl_continue),
                acceptListener = object : DialogAlert.onClickListener {
                    override fun onClick(v: View?, dialog: DialogFragment) {
                        dialog.dismiss()
                        _parentActivity?.popBackStack()
                    }
                },
                cancelListener = object : DialogAlert.onClickListener {
                    override fun onClick(v: View?, dialog: DialogFragment) {
                        dialog.dismiss()
                    }
                }
            )
        }else _parentActivity?.popBackStack()
    }

    private fun setListeners(){
        btnBack.onClick {
            confirmBack()
        }

        onBackPressed {
            confirmBack()
        }

        layoutTitle.onClick {
            if(viewModel.activeEditProfile.value == true) showDialogTitle()
        }

        layoutCountry.onClick {
            if(viewModel.activeEditProfile.value == true) showDialogCountry()
        }

        layoutState.onClick {
            viewModel.countryLiveData.value?.let {
                if(viewModel.activeEditProfile.value == true) showDialogState()
            }
        }

        btnEdit.onClick {
            logEvent(
                AnalyticConstant.ID_PROFILE_EDIT,
                AnalyticConstant.ITEM_NAME_PROFILE_EDIT,
                AnalyticConstant.CONTENT_TYPE_PROFILE)

            viewModel.activeEditProfile.value = !viewModel.activeEditProfile.value!!

        }

        btnSave.onClick {
            val user = getUser()
            val allFieldsFilled = CheckFields()
            if(allFieldsFilled) {
                loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.updateProfile(user) { response ->
                    loadingDialog.dismissSecure()
                    if (response.success) {
                        _parentActivity?.showSnack(getString(R.string.rkey_lbl_info_saved_correct),Constants.SNACK_SUCCES)
                        viewModel.activeEditProfile.value = false

                        MarketingCloud.registerDataToMKTCloud(user)

                        logEvent(
                            AnalyticConstant.ID_PROFILE_SAVE,
                            AnalyticConstant.ITEM_NAME_PROFILE_SAVE,
                            AnalyticConstant.CONTENT_TYPE_PROFILE)

                    } else {
                        _parentActivity?.showSnack(getString(R.string.rkey_lbl_info_saved_error),Constants.SNACK_ERROR)
                        response.userValidError?.let { userValidError ->
                            if (userValidError.firstName != 0) etFirstName.error =
                                getString(userValidError.firstName)
                            if (userValidError.lastName != 0) etLastName.error =
                                getString(userValidError.lastName)
                        } ?: kotlin.run {
                            Toast.makeText(
                                requireContext(),
                                "unknown error, please retry.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            else
                {
                    _parentActivity?.showInfoAlert(
                        getString(R.string.rkey_complete_profile_message_alert),
                        acceptListener = object: DialogAlert.onClickListener{
                            override fun onClick(v: View?, dialog: DialogFragment) {
                                dialog.dismiss()
                            }
                        }
                    )

                }
        }

        txtUpdatePassword.onClick {
            if(viewModel.activeEditProfile.value == true) showDialogUpdatePassword()
            else Toast.makeText(requireContext(), "Please activate edit mode", Toast.LENGTH_SHORT).show()
        }

        btnUploadImage.onClick {
            if(viewModel.activeEditProfile.value == true){
                choosePictureDialog.show(childFragmentManager, ProfilePictureDialog.TAG)
            }
        }

        choosePictureDialog.setProfilePictureInterface(object: ProfilePictureDialog.ProfilePictureInterface{
            override fun onSelectedOption(typeSelected: ProfilePictureDialog.TypeSelectPicture) {
                choosePictureDialog.dismiss()
                when (typeSelected) {
                    ProfilePictureDialog.TypeSelectPicture.TAKE_PHOTO -> {
                        if(ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                            launchTakePhoto()
                        else requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
                    }
                    ProfilePictureDialog.TypeSelectPicture.CHOOSE_PHOT -> {
                        if(ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                            launchPickImage()
                        else requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_STORAGE)
                    }
                }
            }
        })

    }

    private fun CheckFields():Boolean {
        var missingField = true
        try {

            if (txtCountry.text.isNullOrBlank()){
                setDinamicMessage(getString(R.string.rkey_lbl_mandatory_field),txtCountry)
                txtCountry.requestFocus()
                missingField = false
            }
            if (txtState.text.isNullOrBlank()){
                setDinamicMessage(getString(R.string.rkey_lbl_mandatory_field),txtState)
                txtCountry.requestFocus()
                missingField = false
            }
            if (etAddress.text.isNullOrBlank()){
                setDinamicMessage(getString(R.string.rkey_lbl_mandatory_field),etAddress)
                missingField = false
            }
            if (etPhone.text.isNullOrBlank()){
                setDinamicMessage(getString(R.string.rkey_lbl_mandatory_field),etPhone)
                missingField = false
            }
            if (etFirstName.text.isNullOrBlank()){
                setDinamicMessage(getString(R.string.rkey_lbl_mandatory_field),etFirstName)
                missingField = false
            }
            if (etLastName.text.isNullOrBlank()){
                setDinamicMessage(getString(R.string.rkey_lbl_mandatory_field),etFirstName)
                missingField = false
            }

        }catch (exc:Exception){

        }
        return missingField
    }

    fun setDinamicMessage(key:String,txtComp :View) {
        findLabel(key) { label ->
            label.let {
                if (txtComp is InputView) {
                    txtComp as InputView
                    txtComp.error = label?.value
                }
                if (txtComp is EditText) {
                    txtComp as EditText
                    txtComp.error = label?.value
                }

            }
        }
    }


    fun findLabel(key: String, result: (res: LangLabel?) -> Unit){
        doAsync {
            val label = viewModel.labelUseCase.findLabelOutLive(key)
            uiThread {
                result(label)
            }
        }
    }

    private fun showDialogUpdatePassword(){
        val dialog = UpdatePasswordDialog.newInstance()
        dialog.setUpdatePasswordListener(object: UpdatePasswordDialog.UpdatePasswordListener{
            override fun onUpdatePassword(currenPassword: String, newPassword: String) {
                loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.updatePassword(currenPassword, newPassword){
                    runOnUiThread {
                        loadingDialog.dismissSecure()
                        val message = if (it) "Correct password update" else "Error updating password, retry"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
        dialog.show(childFragmentManager, UpdatePasswordDialog.TAG)
    }

    private fun showDialogCountry(){
        val defaultCode = viewModel.countryLiveData.value?.iSO ?: ""
        val dialog = DropDownCountryFragment.newInstance(defaultCode)
        dialog.addListener(object: DropDownListener<Country>{
            override fun onSelectedItem(item: Country) {
                dialog.dismiss()
                viewModel.countryLiveData.value = item
            }
        })
        dialog.show(childFragmentManager, DropDownCountryFragment.TAG)
    }

    private fun showDialogTitle(){
        val defaultCode = if(viewModel.titleLiveData.value != null) viewModel.titleLiveData.value?.value else viewModel.defaultTitleLiveData.value?.value
        val dialog = DropDownTitleFragment.newInstance(defaultCode ?: "")
        dialog.addListener(object: DropDownListener<Title>{
            override fun onSelectedItem(item: Title) {
                dialog.dismiss()
                viewModel.titleLiveData.value = item
            }
        })
        dialog.show(childFragmentManager, DropDownTitleFragment.TAG)
    }

    private fun showDialogState(){
        val defaultCode = viewModel.stateLiveData.value?.abbreviation ?: ""
        val dialog = DropDownStateFragment.newInstance(defaultCode, viewModel.countryLiveData.value?.id ?: 0)
        dialog.addListener(object: DropDownListener<State>{
            override fun onSelectedItem(item: State) {
                dialog.dismiss()
                viewModel.stateLiveData.value = item
            }
        })
        dialog.show(childFragmentManager, DropDownStateFragment.TAG)
    }

    private fun cleanObserversDropDown(){
        viewModel.findCountryByIso2().removeObservers(this)
        viewModel.findStateByAbbreviation().removeObservers(this)
        viewModel.findTitleByCode().removeObservers(this)
    }

    private fun getUser(): User {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val cp = etCP.text.toString().trim()
        var phone = etPhone.text.toString().trim()
        val city = etCity.text.toString().trim()
        val phoneCode = txtPhoneCode.text.toString()
        if(phoneCode != getString(R.string.default_code_phone) && phone.isNotEmpty()){
            phone = "$phoneCode$phone"
        }

        return User(
            salesForceId = viewModel.userLiveData.value?.salesForceId ?: "",
            firstName = firstName,
            lastName = lastName,
            email = viewModel.userLiveData.value?.email ?: "",
            name = "${firstName.trim()} ${lastName.trim()}",
            title_code = viewModel.titleLiveData.value?.value ?: viewModel.defaultTitleLiveData.value?.value ?: "",
            country_code = viewModel.countryLiveData.value?.iSO2 ?: "",
            state_code = viewModel.stateLiveData.value?.abbreviation ?: "",
            city = city,
            cognitoId = Session.getUID(requireContext()) ?: "",
            address = address,
            cp = cp,
            phone = phone.replace("+", ""),
            notify_promotions = stReceivePromotions.isChecked
        )
    }

    //region addEmail
    private fun addEmail(){
        AddEmailDialog()
            .setEmailListener(object: AddEmailDialog.AddEmailListener{
                override fun changeEmail(dialog: DialogFragment, email: String) {
                    dialog.dismiss()
                    viewModel.newEmail.value = email
                    addCognitoEmail(email)
                }

                override fun closeDialog(dialog: DialogFragment) {
                    dialog.dismiss()
                    if(viewModel.userLiveData.value?.email.isNullOrEmpty())
                        _parentActivity?.popBackStack()
                }
            })
            .show(childFragmentManager, AddEmailDialog.TAG)
    }

    private fun addCognitoEmail(email: String){
        loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
        viewModel.addEmail(email){ success ->
            runOnUiThread {
                loadingDialog.dismissSecure()
                if(success) validateEmail()
                else _parentActivity?.showErrorAlert(
                    getString(R.string.rkey_lbl_error_general_endpoint),
                    cancelListener = object: DialogAlert.onClickListener{
                        override fun onClick(v: View?, dialog: DialogFragment) {
                            dialog.dismiss()
                        }
                    },
                    acceptListener = object: DialogAlert.onClickListener{
                        override fun onClick(v: View?, dialog: DialogFragment) {
                            dialog.dismiss()
                            addCognitoEmail(email)
                        }
                    }
                )
            }
        }
    }

    private fun validateEmail(){
        val tokenDialog = DialogEnterCode.newInstance()
        tokenDialog.setEnterCodeListener(object : DialogEnterCode.EnterCodeListener{
            override fun onConfirmCode(code: String) {
                loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.confirmEmail(code){ success ->
                    runOnUiThread {
                        loadingDialog.dismissSecure()
                        if(success){
                            tokenDialog.dismiss()
                            viewModel.updateEmailFB(viewModel.newEmail.value ?: ""){ ok ->
                                if(ok) updateFirebaseFromSF()
                                else {
                                    _parentActivity?.showInfoAlert(
                                        getString(R.string.rkey_lbl_error_general_error),
                                        acceptListener = object: DialogAlert.onClickListener{
                                            override fun onClick(v: View?, dialog: DialogFragment) {
                                                dialog.dismiss()
                                            }
                                        }
                                    )
                                }
                            }
                        }else{
                            _parentActivity?.showInfoAlert(
                                getString(R.string.rkey_lbl_error_general_error),
                                acceptListener = object: DialogAlert.onClickListener{
                                    override fun onClick(v: View?, dialog: DialogFragment) {
                                        dialog.dismiss()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            override fun resendCode() {
                loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
                viewModel.resendConfirmEmail { success ->
                    runOnUiThread {
                        loadingDialog.dismissSecure()
                        if(success){
                            _parentActivity?.showSuccessAlert(
                                getString(R.string.rkey_token_has_been_sent),
                                object: DialogAlert.onClickListener{
                                    override fun onClick(v: View?, dialog: DialogFragment) {
                                        dialog.dismiss()
                                    }
                                }
                            )
                        }else{
                            _parentActivity?.showErrorAlert(
                                getString(R.string.rkey_lbl_error_general_endpoint),
                                cancelListener = object: DialogAlert.onClickListener{
                                    override fun onClick(v: View?, dialog: DialogFragment) {
                                        dialog.dismiss()
                                    }
                                },
                                acceptListener = object: DialogAlert.onClickListener{
                                    override fun onClick(v: View?, dialog: DialogFragment) {
                                        dialog.dismiss()
                                        resendCode()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            override fun closeDialog(dialog: DialogFragment) {
                dialog.dismiss()
                if(viewModel.userLiveData.value?.email.isNullOrEmpty())
                    _parentActivity?.popBackStack()
            }
        })
        tokenDialog.show(childFragmentManager, DialogEnterCode.TAG)
    }
    //endregion

    //region upload profile
    private fun launchTakePhoto(){
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, RESULT_TAKE_PHOTO)
    }

    private fun launchPickImage(){
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(pickPhoto, RESULT_CHOOSE_IMAHE)
    }

    private fun uploadImage(bitmap: Bitmap){
        loadingDialog.showSecure(childFragmentManager, LoadingDialogFragment.TAG)
        doAsync {
            val resizedImage = resizedBitmap(bitmap, MAX_SIZE_IMAGE, MAX_SIZE_IMAGE)
            val baos = ByteArrayOutputStream()
            resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            uiThread {
                val pathImage = "$REF_PHOTO_PROFILE${viewModel.userLiveData.value?.salesForceId?.trim()}${Constants.PROFILE_EXT}"
                val profilePictureRef = storage.reference.child(pathImage)
                val uploadTask = profilePictureRef.putBytes(data)
                uploadTask.addOnCompleteListener {
                    try {
                        runOnUiThread {
                            loadingDialog.dismissSecure()
                            if (it.isSuccessful) {
                                Glide.with(requireContext())
                                    .applyDefaultRequestOptions(RequestOptions().disallowHardwareConfig())
                                    .load(resizedImage.copy(Bitmap.Config.ARGB_8888, true))
                                    .into(imgProfile)
                            }
                        }
                    }catch (e: Exception){
                        loadingDialog.dismissSecure()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode == RESULT_OK) {
                when (requestCode) {
                    RESULT_TAKE_PHOTO -> {
                        data?.extras?.get("data")?.let { image ->
                            uploadImage(image as Bitmap)
                        }
                    }
                    RESULT_CHOOSE_IMAHE -> {
                        data?.data.let { uri ->
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    _parentActivity?.contentResolver,
                                    uri
                                )
                                uploadImage(bitmap)
                            } else {
                                val source = ImageDecoder.createSource(_parentActivity?.contentResolver!!, uri!!)
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                uploadImage(bitmap)
                            }
                        }
                    }
                    REQUEST_PERMISSION_READ_STORAGE -> {
                        launchPickImage()
                    }
                    REQUEST_PERMISSION_CAMERA -> {
                        launchTakePhoto()
                    }
                }
            }
        }catch (e: Exception) {}
    }

    private fun resizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap{
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width.toFloat()
        val scaleHeight = newHeight.toFloat() / height.toFloat()

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    //endregion

    companion object{
        private const val TAKE_PHOTO = 0
        private const val CHOOSE_IMAGE = 1
        private const val RESULT_TAKE_PHOTO = 100
        private const val RESULT_CHOOSE_IMAHE = 101
        private const val REQUEST_PERMISSION_READ_STORAGE = 102
        private const val REQUEST_PERMISSION_CAMERA = 103
        private const val MAX_SIZE_IMAGE = 250

        private const val REF_PHOTO_PROFILE = "profile/"
    }
}