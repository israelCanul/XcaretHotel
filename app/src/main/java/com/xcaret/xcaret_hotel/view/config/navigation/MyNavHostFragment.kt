package com.xcaret.xcaret_hotel.view.config.navigation

import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment

class MyNavHostFragment: NavHostFragment() {

    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return KeepStateNavigator(requireContext(), childFragmentManager, id)
    }

    /*private lateinit var myFragmentNavigator: MyFragmentNavigator

    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        myFragmentNavigator =  MyFragmentNavigator(requireContext(), childFragmentManager, id)
        return myFragmentNavigator
    }

    fun addDestinationChangeListener(destinationChangeListener: MyFragmentNavigator.DestinationChangeListener){
        if(::myFragmentNavigator.isInitialized) myFragmentNavigator.addDestionationChangeListener(destinationChangeListener)
    }

    fun getMyFragmentNavigation() = myFragmentNavigator*/
}