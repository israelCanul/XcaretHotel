package com.xcaret.xcaret_hotel.view.binding

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.data.repository.FirebaseReference
import com.xcaret.xcaret_hotel.domain.Hotel
import com.xcaret.xcaret_hotel.domain.RoomType
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.config.neumorphism.NeomorphFrameLayout
import com.xcaret.xcaret_hotel.view.config.neumorphism.NeumorphCardView
import com.xcaret.xcaret_hotel.view.config.shapeofview.shapes.RoundRectView
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import com.xcaret.xcaret_hotel.view.menu.ui.CallUsDialogFragment
import kotlinx.android.synthetic.main.profile_fragment.*
import org.jetbrains.anko.*

@BindingAdapter(value = ["android:reference", "android:thumb", "android:onlyThumbs", "android:default"], requireAll = false)
fun loadFromStorage(imageView: ImageView, reference: String?, image: String?, onlyThumbs: Boolean?, default: String?){
    try{
        imageView.context?.let { ctx ->
            if(ctx is MainActivity){
                image?.let { ctx.loadRemoteImage(
                    reference ?: "",
                    image ?: "",
                    imageView,
                    onlyThumb = (onlyThumbs == true),
                    default = default ?: ""
                    )
                }
            }
        }
    }
    catch (e: java.lang.Exception){}
}

@BindingAdapter(value = ["android:referenceByHotel", "android:imageByHotel", "android:onlyThumbs"], requireAll = false)
fun loadFromStorageByHotel(imageView: ImageView, reference: String?, image: String?, onlyThumbs: Boolean?){
    try{
        imageView.context?.let { ctx ->
            if(ctx is MainActivity){
                image?.let { ctx.loadRemoteImage(
                    "${ctx._viewModel.currentHotelLive.value?.code?.toLowerCase()}/$reference" ?: "",
                    image ?: "",
                    imageView,
                    onlyThumb = (onlyThumbs == true))
                }
            }
        }
    }
    catch (e: java.lang.Exception){}
}

@BindingAdapter("android:src")
fun imageSrc(imgView: ImageView, resource: String?){
    try{
        val resourceId = Utils.getDrawableId(imgView.context, resource)
        resourceId?.let { rId ->
            var default: Int? = R.drawable.hxm_default
            imgView.context?.let { ctx ->
                if (ctx is MainActivity) default = Utils.getDefaultImage(ctx, ctx._viewModel.currentHotelLive.value?.code)
            }
            if(resource?.contains("hxa_building")==true) {
                default = R.drawable.filter_default
            }

            Glide.with(imgView.context)
                .load(rId)
                .error(default)
                .placeholder(default!!)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imgView)
        }
    }
    catch (e: Exception){}
}

@BindingAdapter("android:src")
fun src(imgView: ImageView, resource: Int?){
    try{
        resource?.let { rId ->
            imgView.setImageResource(rId)
        }
    }
    catch (e: Exception){}
}

@BindingAdapter("android:backgroundImage")
fun imageBackground(view: ViewGroup, resource: String?){
    try{
        val img = Utils.getDrawableByResoulution(view.context, resource ?: "")
        img?.let {
            view.setBackgroundResource(img)
        }
    }
    catch (e: Exception){}
}

@BindingAdapter("android:backgroundColor")
fun backgroundColor(view: ViewGroup, resource: Int){
    try{
        view.backgroundColor = resource
    }
    catch (e: Exception){}
}

@BindingAdapter("android:backgroundColor")
fun backgroundColor(view: NeomorphFrameLayout, resource: Int){
    try{
        view.setNeumorphBackgroundColor(resource)
    }
    catch (e: Exception){}
}

@BindingAdapter("android:backgroundResource")
fun backgroundResource(view: ViewGroup, resource: Int){
    try{
        view.backgroundResource = resource
    }
    catch (e: Exception){}
}

@BindingAdapter("android:backgroundColor")
fun backgroundColor(view: View, resource: String?){
    try{
        resource?.let { res ->
            Utils.getColorRGB(res)?.let { rgb ->
                view.setBackgroundColor(rgb)
            }
        }
    }
    catch (e: Exception){}
}

@BindingAdapter("android:percentageHeight")
fun percentageHeight(view: ViewGroup, percentage: Float){
    try{
        val size = Utils.getScreenSize(view.context)
        val height = size.widthPixels * percentage
        val params = view.layoutParams
        params.height = height.toInt()
        view.layoutParams = params
    }
    catch (e: Exception){}
}

@BindingAdapter("app:height")
fun height(view: ViewGroup, resource: Int?){
    try{
        resource?.let { height ->
            val params = view.layoutParams
            params.height = height
            view.layoutParams = params
        }
    }
    catch (e: Exception){}
}

@BindingAdapter("android:genericAdapter")
fun setAdapter(recyclerView: RecyclerView, adapter: GenericAdapter<*>?){
    try{
        recyclerView.adapter = adapter
    }
    catch (e: Exception){}
}

@BindingAdapter("app:hasFixedSize")
fun hasFixedSize(recyclerView: RecyclerView, fixedSize: Boolean? = false){
    try{
        recyclerView.setHasFixedSize(fixedSize == true)
    }
    catch (e: Exception){}
}

@BindingAdapter("android:textBreakLine")
fun setTextBreakLine(textView: AppCompatTextView, text: String?){
    var first = ""
    text?.let {
        val split = text.split(" ")

        if(split.isNotEmpty()){
            first = "${split[0]}\n"
            split.forEachIndexed { index, c ->
                if(index != 0) first += "${c.toString().trim()} "
            }
        }

        LogHX.d("textBreakLine", first)
        textView.text = first.trim()
    }
}

@BindingAdapter("openFor")
fun setOpenFor(textView: TextView, text: String?){
    val split = text?.split(",") ?: listOf("")
    var formatText = ""
    var extra = ""
    val number = 3

    if(split.size == 1) formatText = split[0].trim()
    else if(split.size < number){
        split.forEach {
            formatText += "${it.trim()}\n"
        }
    }else{
        split.forEachIndexed { index, s ->
            if(index < number)
                formatText += "$s\n"
            else
                extra += "$s "
        }
    }
    textView.text = "${formatText.trim()} ${extra.trim()}".trim()
}

@BindingAdapter("android:textSize")
fun setTextSize(textView: AppCompatTextView, size: Int){
    textView.textSize = textView.resources.getDimension(size)
}

@BindingAdapter("android:textColor")
fun setTextColor(textView: AppCompatTextView, color: Int){
    textView.textColorResource = color
}

@BindingAdapter("android:shapeRoundRectBorderColor")
fun shapeRoundRectBorderColor(roundRectView: RoundRectView, color: Int?){
    try{
        color?.let {
            roundRectView.setBorderColor(color)
        }
    }
    catch (e: Exception){
        e.printStackTrace()
    }
}

@BindingAdapter("android:customMargin")
fun customMargin(viewGroup: ViewGroup, dimen: Float){
    try{
        val marginParams = viewGroup.layoutParams as ViewGroup.MarginLayoutParams
        marginParams.margin = dimen.toInt()
        viewGroup.layoutParams = marginParams
    }
    catch (e: Exception){
        e.printStackTrace()
    }
}

@BindingAdapter("android:customMarginVertical")
fun customMarginVertical(viewGroup: ViewGroup, dimen: Float){
    try{
        val marginParams = viewGroup.layoutParams as ViewGroup.MarginLayoutParams
        marginParams.verticalMargin = dimen.toInt()
        viewGroup.layoutParams = marginParams
    }
    catch (e: Exception){
        e.printStackTrace()
    }
}

@BindingAdapter("android:customMarginHorizontal")
fun customMarginHorizontal(viewGroup: ViewGroup, dimen: Float){
    try{
        val marginParams = viewGroup.layoutParams as ViewGroup.MarginLayoutParams
        marginParams.horizontalMargin = dimen.toInt()
        viewGroup.layoutParams = marginParams
    }
    catch (e: Exception){
        e.printStackTrace()
    }
}

@BindingAdapter("android:elevation")
fun elevation(viewGroup: ViewGroup, dimen: Float){
    try{
        viewGroup.elevation = dimen
    }
    catch (e: Exception){
        e.printStackTrace()
    }
}

@BindingAdapter(value = arrayOf("android:date", "android:format"), requireAll = true)
fun format(textView: TextView, date: String?, format: String?){
    try {
        format?.let {f ->
            date?.let { d ->
                textView.text =
                    DateUtil.changeFormatDate(d, DateUtil.DATE_FORMAT_WEATHER, f)
            }
        }
    }catch (e: Exception){}
}

@BindingAdapter("android:drawableEndCompat")
fun drawableEndCompat(textView: TextView, resource: String?){
    try {
        val iconId = Utils.getDrawableId(textView.context, resource) ?: R.drawable.ic_default_main_activity
        val img = ContextCompat.getDrawable(textView.context, iconId)
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
    }catch (e: Exception){}
}

@BindingAdapter("android:drawableTopCompat")
fun drawableTopCompat(textView: TextView, resource: String?){
    try {
        val iconId = Utils.getDrawableId(textView.context, resource) ?: R.drawable.ic_default_main_activity
        val img = ContextCompat.getDrawable(textView.context, iconId)
        textView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null)
    }catch (e: Exception){}
}

@BindingAdapter("android:drawableStartCompat")
fun drawableStartCompat(textView: TextView, resource: String?){
    try {
        val iconId = Utils.getDrawableId(textView.context, resource) ?: R.drawable.ic_default_main_activity
        val img = ContextCompat.getDrawable(textView.context, iconId)
        textView.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)
    }catch (e: Exception){}
}

@BindingAdapter("app:layoutConstraintGuidePercent")
fun layoutConstraintGuidePercent(guideLine: Guideline, percentage: Float){
    LogHX.i("GuideLinePercentage", percentage.toString())
    val params = guideLine.layoutParams as ConstraintLayout.LayoutParams
    params.guidePercent = percentage
    guideLine.layoutParams = params
    guideLine.parent.requestLayout()
}

@BindingAdapter("app:gravity")
fun gravity(textView: AppCompatTextView, gravity: Int){
    textView.gravity = gravity
}

@BindingAdapter("app:tint")
fun tint(imageView: ImageView, colorRes: String?){
    try{
        val color = Utils.getColorRGB(colorRes)
        color?.let { imageView.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN) }
    }catch (e: Exception){}
}

@BindingAdapter("app:tint")
fun tint(imageView: ImageView, color: Int?){
    try{
        color?.let { imageView.setColorFilter(it, android.graphics.PorterDuff.Mode.SRC_IN) }
    }catch (e: Exception){}
}

@BindingAdapter("android:goToMap")
fun goToMap(view: View, placeUID: String?){
    placeUID?.let { pUID ->
        if(view.context is MainActivity){
            view.setOnClickListener {
                (view.context as MainActivity).showInMap(pUID)
            }
        }
    }
}

@BindingAdapter("android:showContactDialogByCode")
fun showContactDialogByCode(view: View, contactCode: String?){
    contactCode?.let { code ->
        if(view.context is MainActivity){
            view.setOnClickListener {
                (view.context as MainActivity).showContactDialog(CallUsDialogFragment.CODE, contactCode)
            }
        }
    }
}

@BindingAdapter("android:showContactDialogByUID")
fun showContactDialogByUID(view: View, placeUID: String?){
    placeUID?.let { uid ->
        if(view.context is MainActivity){
            view.setOnClickListener {
                (view.context as MainActivity).showContactDialog(CallUsDialogFragment.UID, placeUID)
            }
        }
    }
}

@BindingAdapter("android:bold")
fun bold(textView: TextView, boolean: Boolean = false){
    if(boolean) textView.setTypeface(null, Typeface.BOLD)
    else textView.setTypeface(null, Typeface.NORMAL)
}

@BindingAdapter("android:alpha")
fun alpha(view: View, alpha: Float){
    try{
        view.alpha = alpha
    }
    catch (e: Exception){}
}

@BindingAdapter("android:alpha")
fun alpha(view: ViewGroup, alpha: Float){
    try{
        view.alpha = alpha
    }
    catch (e: Exception){}
}

@BindingAdapter("android:enaled")
fun enabled(editText: EditText, enabled: Boolean){
    try{
        editText.isEnabled = enabled
    }
    catch (e: Exception){}
}

@BindingAdapter("android:showDetailDialog")
fun showRoomTypeDialog(textView: TextView, roomType: RoomType?){
    roomType?.let {
        if(textView.context is MainActivity) {
            (textView.context as MainActivity)._viewModel.roomTypeLiveData.value = roomType
            (textView.context as MainActivity).showRoomTypeDialog()
        }
    }
}

@BindingAdapter("android:error")
fun error(editText: EditText, error: String? = null){
    editText.error = error
}

@BindingAdapter("android:imeOptions")
fun imeOptions(editText: EditText, imeOption: Int? = null){
    imeOption?.let { im ->
        editText.imeOptions = im
    }
}

@BindingAdapter("android:loadAvatar")
fun loadAvatar(imageView: ImageView, userUID: String? = null){
    try{
        userUID?.let { uid ->
            imageView.context?.let { ctx ->
                if (ctx is MainActivity) {
                    val stringRef = "${Constants.PROFILE_PATH}$uid${Constants.PROFILE_EXT}"
                    LogHX.d("loadAvatar", stringRef)
                    val ref = ctx.firebaseStorage.reference.child(stringRef)
                    ref.downloadUrl.addOnSuccessListener {
                        LogHX.d("loadAvatar", it.path)
                        Glide.with(ctx)
                            .load(it)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imageView)
                    }
                }
            }
        }
    }
    catch (e: java.lang.Exception){}
}

@BindingAdapter("android:colorBase")
fun loadColorBase(neomorphFrameLayout: NeomorphFrameLayout, hotel: Hotel?){
    try{
        hotel?.let { h ->
            val isNightMode = Settings.isActiveDarkTheme(neomorphFrameLayout.context)
            val color = if(isNightMode) h.baseColor else h.baseColorDark
            neomorphFrameLayout.setNeumorphBackgroundColor(Utils.getColorRGB(color)!!)
        }
    }
    catch (e: Exception){
        neomorphFrameLayout.setNeumorphBackgroundColor(ContextCompat.getColor(neomorphFrameLayout.context, R.color.colorButtonPink))
    }
}
