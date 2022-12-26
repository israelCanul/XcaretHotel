package com.xcaret.xcaret_hotel.view.config.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.view.config.*
import com.xcaret.xcaret_hotel.view.tabs.ui.MapFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.doAsync
import java.util.*


@Navigator.Name("fragment") // `fragment` is used in navigation xml
class MyFragmentNavigator(
    private val mContext: Context,
    private val mManager: FragmentManager, // Should pass childFragmentManager.
    private val mContainerId: Int
) : FragmentNavigator(mContext, mManager, mContainerId) {

    private var destinationChangeListener: DestinationChangeListener? = null
    private var currentTag: String? = null

    fun addDestionationChangeListener(destinationChangeListener: DestinationChangeListener){
        this.destinationChangeListener = destinationChangeListener
    }

    override fun navigate(destination: Destination, args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        return navigate(destination, args, navOptions, navigatorExtras, isBack = false)
    }

    fun navPopBackStack(): Boolean {
        if(!mBackStack.isEmpty()) mBackStack.pop()
        if(mBackStack.isEmpty()) return false
        val mBackEntry = mBackStack.peek()
        navigate(mBackEntry.destination, mBackEntry.args, mBackEntry.navOptions, mBackEntry.navigatorExtras, ISBACK)
        return ISBACK
    }

    override fun popBackStack(): Boolean {
        return !mBackStack.isEmpty()
    }

    @SuppressLint("RestrictedApi")
    private fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?, isBack: Boolean = false): NavDestination? {
        val tag = destination.label.toString()
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }

        LogHX.i("navigate", "current: $currentTag, destination: $tag")

        //launch map
        if(tagsRoot.contains(tag) && tag != FragmentTags.Map.value)
            initMapFragment()

        var fragment = mManager.findFragmentByTag(tag)
        val ft = mManager.beginTransaction()
        val currentFragment = mManager.findFragmentByTag(currentTag)

        if(tagsDetach.contains(tag)) {
            if(currentTag != FragmentTags.QuoteFragment.value && (tag != FragmentTags.Login.value || tag != FragmentTags.Profile.value)) {
                LogHX.d("detach", tag)
                fragment?.let { ft.detach(it).remove(it) }
                fragment = null
            }
        }

        currentFragment?.let { ft.hide(it) }

        //ft.setCustomAnimations(R.anim.nav_default_slide_in, R.anim.nav_default_fade_out)
        /*if(!tagsRoot.contains(tag) && tag != FragmentTags.Splash.value) {
            LogHX.i("AnimationFragment", "==============================")
            Log.e("anim", "si: ${R.anim.nav_default_slide_in}, fi: ${R.anim.nav_default_fade_in}, fo: ${R.anim.nav_default_fade_out}, so: ${R.anim.nav_default_slide_out}")
            val animEnter = if(isBack) R.anim.nav_default_fade_in else R.anim.nav_default_slide_in
            val animExit = if(isBack) R.anim.nav_default_slide_out else R.anim.nav_default_fade_out
            LogHX.i("default", "enter: ${animEnter}, animExit: ${animExit}")
            LogHX.i("AnimationFragment", "==============================")
            ft.setCustomAnimations(animEnter, animExit)
        }*/
        /*var enterAnim = if(!isBack) navOptions?.enterAnim ?: -1 else -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = if(!isBack) navOptions?.popEnterAnim ?: -1 else -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }*/

        fragment?.let { fr ->
            if(fr.isAdded){
                if(tag == FragmentTags.Map.value) {
                    //if(!isBack) (fragment as MapFragment).update()
                    //else (fragment as MapFragment).showHideBackButton()
                }
                ft.show(fr)
                LogHX.d("$tag", "show")
            }else{
                ft.add(mContainerId, fr, tag)
                ft.show(fr)
                LogHX.d("$tag", "add")
            }
        } ?: kotlin.run {
            fragment = mManager.fragmentFactory.instantiate(mContext.classLoader, className)
            fragment?.let {
                it.arguments = args
                ft.add(mContainerId, it, tag)
                ft.show(it)
                LogHX.d("$tag", "add-show")
            }
        }

        //ft.addToBackStack(null)
        if(tagsRoot.contains(tag)) destinationChangeListener?.onChangeDestination(destination.id)

        //mapa manejar su propia logica de mostrar el bottom navigation
        if(tag != FragmentTags.Map.value)
            fragment?.updateVisibilityBottomBar(tagsRoot.contains(tag))
        fragment?.configToolbar(tag)

        ft.setPrimaryNavigationFragment(fragment)
        currentTag = tag

        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key!!, value!!)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        val navBackEntry = NavBackEntry(destination, args, navOptions, navigatorExtras)

        //indica si debemos de eliminar la pila de elementos
        if(!isBack) {
            mBackStack.push(navBackEntry)
            originBackStack.add(navBackEntry)
            fixBackNavigation()
        }
        if(tag == FragmentTags.Home.value) cleanFragmentTransaction(FragmentTags.Home.value)
        fixNavigationSecurity(if(isBack) navBackEntry else null, args)
        return if(isBack) null else destination
    }

    private fun initMapFragment(){
        var fragmentMap = mManager.findFragmentByTag(FragmentTags.Map.value)
        if (fragmentMap == null) {
            fragmentMap = mManager.fragmentFactory.instantiate(
                mContext.classLoader,
                MapFragment::class.java.name
            )
            if (!fragmentMap.isAdded) {
                LogHX.d("initMapFragment", "ok")
                mManager.beginTransaction()
                    .add(mContainerId, fragmentMap, FragmentTags.Map.value)
                    .hide(fragmentMap)
                    .commit()
            }
        }
    }


    private fun fixNavigationSecurity(lastBack: NavBackEntry? = null, args: Bundle? = Bundle.EMPTY){
        if(originBackStack.isEmpty()) return
        val navBack = lastBack ?: originBackStack.peek()

        val cleanStack = args?.getBoolean(Constants.CLEAR_STACK, false) ?: false

        if(navBack.destination.label == FragmentTags.Splash.value || cleanStack){
            cleanFragmentTransaction(navBack.destination.label.toString())
            originBackStack.forEach {
                destinationChangeListener?.popBackStack(it.destination.id, true)
            }
            originBackStack.clear()
            mBackStack.clear()
        }
        else fixPopBackStack(navBack)
    }

    private fun fixPopBackStack(navBack: NavBackEntry){
        var origin = -1
        loop@ for(i in originBackStack.indices){
            LogHX.d("Search destination", "$i")
            if(originBackStack[i].destination.label == navBack.destination.label) {
                origin = i + 1
                break@loop
            }
        }

        LogHX.d("Destination found", "$origin")
        //si no se encontro o el index es el mismo que el tamaño de la pila, entonces no borramos nada.
        if(origin == -1 || origin >= (originBackStack.size)) return

        for(i in origin until originBackStack.size){
            val toPop = originBackStack[i]
            LogHX.d("Destination remove", "${toPop.destination.id}")
            destinationChangeListener?.popBackStack(toPop.destination.id, true)
        }

        val originBarStackSize = originBackStack.size
        for(i in origin until originBarStackSize){
            originBackStack.pop()
        }

    }

    private fun fixBackNavigation(){
        //fix cuando navegue hacia atras desde un tag root, me redirige al home
        if(mBackStack.isEmpty()) return
        val last = mBackStack.peek()
        val lastToLast =
            if(mBackStack.size > 1) mBackStack[mBackStack.size - 2]
            else null

        LogHX.d("lastToLast", "last: ${last.destination.label} toLast: ${lastToLast?.destination?.label}")

        val toRemove = when {
            tagsRoot.contains(last.destination.label.toString()) && tagsRoot.contains(lastToLast?.destination?.label ?: "") -> mBackStack.size - 1
            else -> 0
        }

        for(i in 0 until toRemove) mBackStack.pop()

        if(mBackStack.isEmpty() && toRemove > 0)
            mBackStack.push(last)
        else{
            val last2 = mBackStack.peek()
            if(toRemove > 0 && last.destination.label != last2.destination.label)
                mBackStack.push(last)
        }
    }

    private fun cleanFragmentTransaction(tagNotDestroy: String){
        for (frag in mManager.fragments) {
            if(tagNotDestroy != frag.tag && frag.tag != FragmentTags.Map.value)
                mManager.beginTransaction().detach(frag).remove(frag).commit()
        }
        System.gc() //force call garbage colletor
    }

    interface DestinationChangeListener{
        fun onChangeDestination(destination: Int)
        fun popBackStack(destinationId: Int, inclusive: Boolean = false)
    }

    class NavBackEntry(val destination: Destination, var args: Bundle?, val navOptions: NavOptions?, val navigatorExtras: Navigator.Extras?)

    companion object {
        const val ISBACK = true

        val tagsRoot = arrayOf (
            FragmentTags.Home.value,
            FragmentTags.Map.value,
            FragmentTags.AFI.value,
            FragmentTags.Menu.value
        )

        val tagsDetach = arrayOf (
            FragmentTags.ParkDetail.value,
            FragmentTags.RoomDetail.value,
            FragmentTags.RestaurantDetail.value,
            FragmentTags.PoolDetailFragment.value,
            FragmentTags.Profile.value,
            FragmentTags.Splash.value,
            FragmentTags.QuoteFragment.value,
            FragmentTags.MyReservations.value
        )

        val mBackStack: Stack<NavBackEntry> = Stack()
        val originBackStack: Stack<NavBackEntry> = Stack()
    }
}