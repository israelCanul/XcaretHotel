package com.xcaret.xcaret_hotel.view.config

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.xcaret.xcaret_hotel.HotelXcaretApp
import com.xcaret.xcaret_hotel.R
import org.jetbrains.anko.windowManager
import java.lang.Math.sqrt
import java.net.NetworkInterface
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.experimental.and
import kotlin.math.pow
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter

import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION

import android.content.pm.ResolveInfo
import java.net.Inet6Address
import java.net.InetAddress


object Utils {

    const val DRAWABLE = "drawable"
    const val MIN_SCREEN_BG = 5.5

    val macAddr: String
        @SuppressLint("DefaultLocale")
        get() {
            try {
                val all = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                    val macBytes = nif.hardwareAddress ?: return ""

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        var hex = Integer.toHexString((b and 0xFF.toByte()).toInt()).toUpperCase()
                        hex = if (hex.length == 1)
                            "0$hex"
                        else
                            hex.substring(hex.length -2,  hex.length)
                        res1.append("$hex:")
                    }

                    if (res1.isNotEmpty()) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ignored: Exception) {
            }

            return "02:00:00:00:00:00"
        }


    val ipAddress: String
        get() {
            var sAddr = "127.0.0.1"
            try{
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                interfaces.forEach  loop1@{ intf ->
                    val addrs = Collections.list(intf.inetAddresses)
                    addrs.forEach loop2@{ addr ->
                        if(!addr.isLoopbackAddress){
                            sAddr = addr.hostAddress
                            val isIPv4 = sAddr.indexOf(":") < 0
                            sAddr = if(isIPv4)sAddr else ""
                        }
                    }
                    if (sAddr.isNotEmpty())return@loop1
                }
            }
            catch (e: Exception){
                sAddr = ""
            }
            return sAddr
        }

    val ipAddressLocal:String
    get(){
        var ip = "127.0.0.1"
        val wm = HotelXcaretApp.mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipObtained = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        if (!ipObtained.isNullOrEmpty()){
            ip = if (!ipObtained.equals("0.0.0.0")){
                ipObtained
            }else{
                getLocalIpV6()?: "127.0.0.1"
            }
        }
        return ip
    }
    fun getLocalIpV6(): String? {
        try {
            val en = NetworkInterface
                .getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf
                    .inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address) {
                        return inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: Exception) {

        }
        return null
    }

    fun getDrawableId(context: Context?, name: String?): Int?{
        var id: Int? = null
        try {
            context?.let {
                id = context.resources.getIdentifier(name, DRAWABLE, context.packageName)
            }
        }catch (e: Exception){
            id = 0
        }
        return id
    }

    fun getDefaultImage(context: Context?, hotelCode: String?): Int{
        var default: Int? = getDrawableId(context, "${hotelCode?.toLowerCase()}_default")
        if(default == null || default <= 0) default = R.drawable.hxm_default
        return default
    }

    fun getDrawableByResoulution(context: Context?, name: String): Int?{
        return try {
            var correctDrawable = name
            if (getScreenSizeInches(context as Activity) > MIN_SCREEN_BG)
                correctDrawable += "2"
            val id = getDrawableId(context, correctDrawable)
            if(id != 0) id
            else getDrawableId(context, name)
        }catch (e: Exception){
            null
        }
    }

    fun isSizeInches(context: Context?): Boolean{
        return getScreenSizeInches(context) > MIN_SCREEN_BG
    }

    fun getScreenSizeInches(context: Context?): Double{
        var result = 0.0
        val metrics = getScreenSize(context)
        var mWidthPixels = metrics.widthPixels
        var mHeightPixels = metrics.heightPixels
        try {
            val realSize = Point()
            context?.windowManager?.defaultDisplay?.getRealSize(realSize)
            //Display::class.java.getMethod("getRealSize", Point::class.java).invoke((activity), realSize)
            mWidthPixels = realSize.x
            mHeightPixels = realSize.y
            val x = (mWidthPixels / metrics.xdpi).toDouble().pow(2.0)
            val y = (mHeightPixels / metrics.ydpi).toDouble().pow(2.0)

            result = sqrt(x + y)
        } catch (ignored: Exception) { ignored.printStackTrace() }

        return result
    }

    fun getScreenSize(context: Context?): DisplayMetrics{
        val displayMetrics = DisplayMetrics()
        context?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun getStatusBarHeigth(cntx: Context): Int{
        val resourceId = cntx.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if(resourceId > 0){ cntx.resources.getDimensionPixelSize(resourceId) } else { 0 }
    }

    fun getHeighActionbar(context: Context):Int {
        val tv = TypedValue()
        var actionBarHeight = 0
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight =
                TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
        }
        return actionBarHeight
    }

    fun getNavigationBarHeight(cntx: Context): Int{
        val hasMenuKey = ViewConfiguration.get(cntx).hasPermanentMenuKey()
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

        val resourceId = if(!hasMenuKey && !hasBackKey) cntx.resources.getIdentifier("navigation_bar_height", "dimen", "android") else 0
        return if(resourceId > 0){ cntx.resources.getDimensionPixelSize(resourceId) } else { 0 }
    }

    fun fixMarginBottom(view: View){
        try{
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            val navBarHeight = getNavigationBarHeight(view.context)
            LogHX.e("navBarHeight", navBarHeight.toString())
            params.bottomMargin = params.bottomMargin + navBarHeight
            view.layoutParams = params
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun fixHeightBottomView(view: View){
        try{
            try {
                val params = view.layoutParams
                val height = getNavigationBarHeight(view.context)
                LogHX.e("navBarHeight", height.toString())
                if(height > 0) params.height = height
                view.layoutParams = params
            }catch (e: Exception) { e.printStackTrace() }
        }
        catch (e: Exception){}
    }

    fun fixHeightTopView(view: View){
        try {
            val params = view.layoutParams
            params.height = params.height + getStatusBarHeigth(view.context)
            view.layoutParams = params
        }catch (e: Exception) { e.printStackTrace() }
    }

    fun fixMarginTopView(view: View){
        try{
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = params.topMargin + getStatusBarHeigth(view.context)
            view.layoutParams = params
        }
        catch (e: Exception){}
    }

    fun heighPercentage(view: ViewGroup, percentage: Float){
        try{
            val size = getScreenSize(view.context)
            val height = size.heightPixels * (percentage)
            val params = view.layoutParams
            params.height = height.toInt()
            view.layoutParams = params
        }
        catch (e: Exception){}
    }

    fun splitName(name: String): Array<String>{
        val arrayName = name.trim().split(" ")
        var firstName = ""
        var lastName = ""
        when {
            arrayName.size >= 4 -> {
                firstName = "${arrayName[0]} ${arrayName[1]}"
                for(i in 2 until arrayName.size){
                    lastName = "${arrayName[i]} "
                }
            }
            arrayName.size == 3 -> {
                firstName = arrayName[0]
                lastName = "${arrayName[1]} ${arrayName[2]}"
            }
            arrayName.size == 2 -> {
                firstName = arrayName[0]
                lastName = arrayName[1]
            }
            else -> {
                firstName = arrayName[0]
                lastName = arrayName[0]
            }
        }
        return arrayOf(firstName.trim(), lastName.trim())
    }

    fun goToNavigation(latitude: Double, longitude: Double, context: Context?){
        try {
            context?.let {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                it.startActivity(mapIntent)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getColorByHouse(houseCode: String?): Int{
        return when{
            Constants.CASAS_AGUA == houseCode -> R.drawable.gradient_water_house
            Constants.CASA_ESPIRAL == houseCode -> R.drawable.gradient_xpiral_house
            Constants.CASA_FUEGO == houseCode -> R.drawable.gradient_fire_house
            Constants.CASA_TIERRA == houseCode -> R.drawable.gradient_land_house
            Constants.CASA_VIENTO == houseCode -> R.drawable.gradient_wind_house
            Constants.CASA_MUSICA == houseCode -> R.drawable.hxa_gradient_house
            Constants.CASA_DISENO == houseCode -> R.drawable.hxa_gradient_house
            Constants.CASA_PATRON == houseCode -> R.drawable.hxa_gradient_house
            Constants.CASA_PAZ == houseCode -> R.drawable.hxa_gradient_house
            Constants.CASA_ARTISTAS == houseCode -> R.drawable.hxa_gradient_house
            Constants.CASA_PIRAMID == houseCode -> R.drawable.hxa_gradient_house
            houseCode?.contains("hxa", ignoreCase = true) == true -> R.drawable.hxa_gradient_house
            else -> R.drawable.gradient_alert
        }
    }

    fun getColorRGB(rgb: String?): Int?{
        try {
            val splitStr = rgb?.split(",")
            val colorValues = mutableListOf<Int>()
            splitStr?.forEachIndexed { index, s ->
              colorValues.add(index, s.trim().toInt())
            }
            return Color.rgb(
                colorValues[0],
                colorValues[1],
                colorValues[2]
            )
        } catch (ex: Exception) {
            return null
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(context: Context){
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun encrypt(toEncript: String, publicKey: String): String {
        var encoded = ""
        try {
            val publicBytes: ByteArray = Base64.decode(publicKey.trim(), Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val pubKey: PublicKey = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING") //or try with "RSA"

            cipher.init(Cipher.ENCRYPT_MODE, pubKey)
            val encrypted = cipher.doFinal(toEncript.toByteArray(Charset.defaultCharset()))
            encoded = Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return encoded.trim()
    }

    fun partOfThePrice(amount: Double?, currency: String?, decimal: Boolean): String{
        var result = ""
        amount?.let { a ->
            currency?.let { c ->
                result = a.formatCurrency()
                var symbol = "".getSymbolCurrency()
                if(symbol.equals(c, true)) symbol = "$"
                if(!decimal) {
                    if (result.length > 3)
                        result = "$symbol ${result.substring(0, result.length - 3)}"
                }else {
                    if (result.length > 3)
                        result = "${result.substring(result.length - 3, result.length)} ${c.toUpperCase()}"
                }
            }
        }
        return result
    }

    fun getCountry():String{
        val tm =
            HotelXcaretApp.mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val mn = tm.simCountryIso
        val mm = tm.networkCountryIso
        return  tm.networkCountryIso
    }

    fun formatStringToCardFormat(rawCardNumber :String):String{
        val nString = rawCardNumber.chunked(4).joinToString(" ")
        val len = nString.length
        return nString
    }

    fun isValidCardNumber(cardNumber :String ):Boolean{
        if(cardNumber.length in 14..18){
            return true
        }
        return false
    }

    fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun convertDpToPixels(dps :Float):Int{
        val scale = HotelXcaretApp.mContext.getResources().getDisplayMetrics().density;
        val pixels = (dps * scale + 0.5f).toInt()

        val pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, HotelXcaretApp.mContext.resources.displayMetrics)
        val pix = pixel.toInt()
        return pixels
    }

    fun getDeviceName(): String? {
        val manufacturer: String = Build.MANUFACTURER
        val model: String = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true
        val phrase = java.lang.StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }

    /**
     * Returns a list of packages that support Custom Tabs.
     */
    fun getCustomTabsPackages(context: Context): ArrayList<ResolveInfo>? {
        val pm = context.packageManager
        // Get default VIEW intent handler.
        val activityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs: ArrayList<ResolveInfo> = ArrayList()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info)
            }
        }
        return packagesSupportingCustomTabs
    }

    fun analizeServerCode(code:Int):Int{
        return when(code){
            0,Constants.BAD_REQUEST,Constants.GATEWAY_TIMEOUT,
            Constants.SERVICE_UNAVAILABLE,Constants.NOT_FOUND,
            Constants.REQUEST_TIMEOUT-> Constants.SERVICE_OFFLINE
            Constants.INTERNAL_SERVER_ERROR -> Constants.CARD_NOT_FOUND

            else -> Constants.SERVICE_OFFLINE
        }
    }


}