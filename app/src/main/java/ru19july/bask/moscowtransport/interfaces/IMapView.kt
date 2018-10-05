package ru19july.bask.moscowtransport.interfaces

import android.os.Bundle
import android.view.View

interface IMapView {
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
    fun onLowMemory()
    fun onSaveInstanceState(outState: Bundle)
    fun moveTo(latitude : Double, longitude: Double, zoom: Double)
    fun getView(): View?
}
