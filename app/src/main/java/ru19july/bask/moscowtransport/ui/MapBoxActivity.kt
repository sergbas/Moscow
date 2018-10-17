package ru19july.bask.moscowtransport.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import ru19july.bask.moscowtransport.R

class MapBoxActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var destinationMarker: Marker? = null
    private val originCoord: LatLng? = null
    private var destinationCoord: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mapbox Access token
        Mapbox.getInstance(applicationContext, getString(R.string.app_access_token))
        setContentView(R.layout.activity_map_box)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync({

            Log.d(javaClass.simpleName, "----- MAP -----")
            it.setStyle(Style.SATELLITE)

            destinationCoord = LatLng(55.7507, 37.6177)
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    destinationCoord!!, 13.0));

            // Customize map with markers, polylines, etc.

            destinationMarker = it.addMarker(MarkerOptions()
                    .position(destinationCoord)
            )
        })
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

    }
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)

    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()

    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
