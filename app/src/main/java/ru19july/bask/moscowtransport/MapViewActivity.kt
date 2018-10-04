package ru19july.bask.moscowtransport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapViewActivity : AppCompatActivity() {

    private var mapview1: MapView? = null
    private var mapView: com.mapbox.mapboxsdk.maps.MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        var layout = findViewById<View>(R.id.layout_mapview) as LinearLayout

        mapview1 = createYandexmap()
        mapView = createMapboxMap()

        //layout.addView(mapview1)
        layout.addView(mapView)

        //moveYandexMap(mapview1)
        moveMapbox(mapView!!)
    }

    private fun moveMapbox(mapView: com.mapbox.mapboxsdk.maps.MapView) {
        mapView?.getMapAsync({

            Log.d(javaClass.simpleName, "----- MAP -----")
            it.setStyle(Style.SATELLITE)

            val destinationCoord = LatLng(55.7507, 37.6177)
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    destinationCoord!!, 13.0));

        })
    }

    private fun createMapboxMap(): com.mapbox.mapboxsdk.maps.MapView {
        Mapbox.getInstance(applicationContext, getString(R.string.app_access_token))

        var mapview = com.mapbox.mapboxsdk.maps.MapView(this)

        mapview.setStyleUrl("mapbox://styles/mapbox/satellite-v9")
        mapview.setLayoutParams(LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        return mapview
    }

    private fun moveYandexMap(mapview: MapView?) {
        mapview?.getMap()?.move(
                CameraPosition(Point(55.751574, 37.573856), 12.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 3F),
                null)
    }

    private fun createYandexmap(): MapView? {
        MapKitFactory.setApiKey("56f599ed-8d55-449e-b970-bb72f3acef84")
        MapKitFactory.initialize(this)

        var mapview = MapView(this)
        mapview.setLayoutParams(LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return  mapview
    }

    override fun onStop() {
        super.onStop()
        mapview1?.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview1?.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()

    }
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)

    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()

    }
    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
}
