package ru19july.bask.moscowtransport.ui.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import ru19july.bask.moscowtransport.interfaces.IMapView

class MapboxView(applicationContext: Context, app_access_token: String, context: Context?) : View(context), IMapView {
    var mapview : MapView? = null

    override fun getView(): View? {
        return mapview
    }

    init {
        Mapbox.getInstance(applicationContext, app_access_token)
        mapview = MapView(applicationContext)
        mapview?.setStyleUrl(
        "mapbox://styles/mapbox/basic-v9"
//        "mapbox://styles/mapbox/satellite-v9"
        )

        mapview?.setLayoutParams(LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    override fun onStart() {
        mapview?.onStart()
    }

    override fun onResume() {
        mapview?.onResume()
    }

    override fun onPause() {
        mapview?.onPause()
    }

    override fun onStop() {
        mapview?.onStop()
    }

    override fun onDestroy() {
        mapview?.onDestroy()
    }

    override fun onLowMemory() {
        mapview?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapview?.onSaveInstanceState(outState)
    }

    override fun moveTo(latitude: Double, longitude: Double) {
        mapview?.getMapAsync({

            Log.d(javaClass.simpleName, "----- MAP -----")
            it.setStyle(Style.MAPBOX_STREETS)

            val destinationCoord = LatLng(latitude, longitude)
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    destinationCoord!!, 13.0));

        })
    }
}