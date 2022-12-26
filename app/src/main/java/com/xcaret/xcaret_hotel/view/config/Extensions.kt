package com.xcaret.xcaret_hotel.view.config

import android.animation.Animator
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.marginTop
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity
import org.jetbrains.anko.padding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*
import kotlin.Exception
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.google.firebase.storage.StorageReference
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.labelview.view.LabelView
import kotlinx.android.synthetic.main.nav_header_slide.*
import kotlinx.android.synthetic.main.payment_fragment.*
import java.text.Normalizer


fun Fragment.updateVisibilityBottomBar(show: Boolean) {
    this.activity?.let { act ->
        if(act is MainActivity){
            act.updateVisibilityBottomNav(show)
        }
    }
}

fun Fragment.configToolbar(tag: String){
    this.activity?.let { act ->
        if(act is MainActivity){
            val toolbarSettings = ToolbarUtils.toolbars[tag]
            toolbarSettings?.let { ts ->
                act.updateVisibiltyToolbar(ts.showToolbar, bgResource = ts.bgResource, title = ts.title, showBackButton = ts.showBackBtn)
            } ?: kotlin.run { act.updateVisibiltyToolbar(false) }
        }
    }
}

fun DialogFragment.showSecure(fragmentManager: FragmentManager, tag: String){
    try {
        val fr = fragmentManager.findFragmentByTag(tag)
        if (fr!= null || fr?.isAdded == true){
            fragmentManager.beginTransaction().remove(fr).commit()
        }
        show(fragmentManager, tag)
    }catch (i: IllegalStateException){
        i.printStackTrace()
    }catch (e: Exception){
        e.printStackTrace()
    }
}

fun DialogFragment.dismissSecure(){
    try {
        dismiss()
    }catch (i: IllegalStateException){
        i.printStackTrace()
    }catch (e: Exception){
        e.printStackTrace()
    }
}
fun Fragment.showSnackBar(message:String?,requiresHeightAdjustment:Boolean = false):Snackbar?{
    try{
        val snack: Snackbar = Snackbar.make(requireView(), message!!, Snackbar.LENGTH_LONG)
        val view = snack.view
        val layout = snack.view as SnackbarLayout
        view.background = resources.getDrawable(R.drawable.snack_bg)
        val btn = view.findViewById<TextView>(R.id.snackbar_action)
        btn.visibility = View.GONE
        val text = view.findViewById<TextView>(R.id.snackbar_text)
        text.setTextColor(
            resources.getColor(R.color.colorButtonText)
        )
        text.textAlignment = View.TEXT_ALIGNMENT_CENTER
        //text.gravity = Gravity.CENTER
        text.setTypeface(null, Typeface.BOLD)
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)

        if(view.layoutParams is FrameLayout.LayoutParams) {
            val params = view.layoutParams as FrameLayout.LayoutParams
            if (requiresHeightAdjustment){
                params.setMargins(0, (Utils.getStatusBarHeigth(HotelXcaretApp.mContext)), 0, 0)

            }
            params.height = 200
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.bringToFront()
        }
        else if (view.layoutParams is CoordinatorLayout.LayoutParams){
            val params = view.layoutParams as CoordinatorLayout.LayoutParams

            if(requiresHeightAdjustment){
                params.setMargins(0, (Utils.getStatusBarHeigth(HotelXcaretApp.mContext)), 0, 0);
            }
            params.height = 200
            //text.setPadding(0,80,0,0)
            //text.textAlignment = View.TEXT_ALIGNMENT_CENTER
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.bringToFront()
        }
        //snack.show()
        return snack
    }catch (exc:Exception){
        Log.e("ShowSnackBarExtension",exc.toString())
        return  null
    }
}



fun TextView.setDrawableColor(@ColorRes color: Int) {
    compoundDrawables.filterNotNull().forEach {
        it.colorFilter = PorterDuffColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
    }
}

fun RecyclerView.validateAndScrollToPosition(position: Int){
    try {
        val layoutManager = this.layoutManager
        layoutManager?.let { lm ->
            val firstVisibilityPos =
                (lm as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            val lastVisibilityPos = lm.findLastCompletelyVisibleItemPosition()
            if (position < firstVisibilityPos || position > lastVisibilityPos) {
                post { smoothScrollToPosition(position) }
            }
        }
    }catch (e: Exception){}
}

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}


fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

/*fun Double.formatCurrency(iso: String): String{
    return try{
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(iso)
        var value = format.format(this).toUpperCase()
        format.currency?.let {
            value = value
                .replace(it.symbol.toUpperCase(), "")
                .replace(it.symbol.toUpperCase(), "")
        }
        return value
    }
    catch (e: Exception){
        val format = DecimalFormat("###,###,###.##")
        format.format(this)
    }
}*/
fun Double.formatCurrency(): String{
    return try{
        val sepDecimal = Language.getCurrencyDecimal(HotelXcaretApp.mContext)
        val sepMiles = Language.getCurrencyMiles(HotelXcaretApp.mContext)
        val formatSymbos = DecimalFormatSymbols()
        formatSymbos.decimalSeparator = sepDecimal[0]
        formatSymbos.groupingSeparator = sepMiles[0]
        val format = DecimalFormat("###,###,###.00", formatSymbos)
        val f = format.format(this)
        f
    }
    catch (e: Exception){
        this.toString()
    }
}

fun Float.formatCurrency(): String{
    return try{
        val sepDecimal = Language.getCurrencyDecimal(HotelXcaretApp.mContext)
        val sepMiles = Language.getCurrencyMiles(HotelXcaretApp.mContext)
        val formatSymbos = DecimalFormatSymbols()
        formatSymbos.decimalSeparator = sepDecimal[0]
        formatSymbos.groupingSeparator = sepMiles[0]
        val format = DecimalFormat("###,###,###.00", formatSymbos)
        val f = format.format(this)
        f
    }
    catch (e: Exception){
        this.toString()
    }
}

/*fun String.getSymbolCurrency(iso: String): String{
    return try{
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(iso)
        return format.currency?.symbol ?: "$"
    }
    catch (e: Exception){
        "$"
    }
}*/

fun String.normalize():String{

    val array: CharArray = this.toCharArray()
    for (index in array.indices) {
        val pos: Int = Constants.ORIGINAL.indexOf(array[index])
        if (pos > -1) {
            array[index] = Constants.REPLACEMENT.get(pos)
        }
    }
    return String(array)

}

fun String.getSymbolCurrency(): String{
    return Language.getCurrencySymbol(HotelXcaretApp.mContext)
}

fun String.encrypt(): String{
    return if(isNullOrEmpty()){
        ""
    }else{
        Utils.encrypt(this, Settings.getParam(Constants.PUBLIC_KEY, HotelXcaretApp.mContext))
    }
}

fun String.toBase64(): String{
    return try{
        val data = this.toByteArray(charset("UTF-8"))
        Base64.encodeToString(data, Base64.NO_WRAP)
    }catch (e: Exception){
        ""
    }
}

fun Double.format(): Double{
    val format = DecimalFormat("###,###,###.##")
    return format.format(this).toDouble()
}

fun View.upIn(end: () -> Unit){
    visibility = View.VISIBLE
    val animate = TranslateAnimation(
        0f,
        0f,
        height.toFloat(),
        0f
    )
    LogHX.e("heigth", height.toString())
    animate.duration = 350
    animate.fillAfter = true
    animate.setAnimationListener(object: Animation.AnimationListener{
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) { end() }
    })
    startAnimation(animate)
}

fun View.downIn(){
    visibility = View.VISIBLE
    alpha = 0.0f
    animate()
        .translationY(0f)
        .alpha(1.0f)
        .setListener(null)
}

fun View.downOut(){
    animate()
        .translationY(0f)
        .alpha(0.0f)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animator) = Unit
            override fun onAnimationStart(animation: Animator) = Unit
            override fun onAnimationCancel(animation: Animator) = Unit
        }).start()
}

fun View.upOut(){
    animate()
        .translationY(-height.toFloat())
        .alpha(0.0f)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animator) = Unit
            override fun onAnimationStart(animation: Animator) = Unit
            override fun onAnimationCancel(animation: Animator) = Unit
        }).start()
}

fun View.updateHeight(newHeight: Int){
    try{
        val params = layoutParams
        params.height = newHeight
        layoutParams = params
    }catch (e: Exception){}
}

fun RecyclerView.centerItem(position: Int){
    try{
        val smoothController:RecyclerView.SmoothScroller = CenterSmoothController(this.context)
        smoothController.targetPosition = position
        (this.layoutManager as LinearLayoutManager).startSmoothScroll(smoothController)

    }catch (e: Exception){
        Log.e("Extensions", e.toString())
    }
}
fun ImageView.loadImage(reference:StorageReference){
    try {
        reference.downloadUrl.addOnSuccessListener { image ->
            Glide.with(context)
                .load(image)
                .placeholder(R.drawable.hxm_default)
                .error(R.drawable.hxm_default)
                .timeout(1000)
                .into(this)
        }
    }catch (ex:java.lang.Exception){
        Glide.with(context)
            .load(R.drawable.hxm_default)
            .placeholder(R.drawable.hxm_default)
            .into(this)
    }


//        Glide.with(context)
//        .load(url ?: "")
//        .into(this)
}
