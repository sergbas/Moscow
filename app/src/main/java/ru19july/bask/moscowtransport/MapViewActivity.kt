package ru19july.bask.moscowtransport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.mapbox.mapboxsdk.Mapbox
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapViewActivity : AppCompatActivity() {

    private var mapview1: MapView? = null
    //private var mapView: com.mapbox.mapboxsdk.maps.MapView? = null

    private var map : IMapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        var layout = findViewById<View>(R.id.layout_mapview) as LinearLayout

        mapview1 = createYandexmap()
        //mapView = createMapboxMap()

        map = MapboxView(applicationContext, getString(R.string.app_access_token), this)

//        var mapview : View = map as com.mapbox.mapboxsdk.maps.MapView

        //layout.addView(mapview1)
        layout.addView(map?.getView())

        //moveYandexMap(mapview1)
        //moveMapbox(map as com.mapbox.mapboxsdk.maps.MapView)
        map?.moveTo(55.7507, 37.6177)
    }


    private fun createMapboxMap(): com.mapbox.mapboxsdk.maps.MapView {
        Mapbox.getInstance(applicationContext, getString(R.string.app_access_token))

        var mapview = com.mapbox.mapboxsdk.maps.MapView(this)

        mapview.setStyleUrl("mapbox://styles/mapbox/satellite-v9")


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

        return  mapview
    }

    override fun onStop() {
        super.onStop()
        mapview1?.onStop()
        map?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview1?.onStart()
        map?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onResume() {
        super.onResume()
        map?.onResume()

    }
    override fun onPause() {
        super.onPause()
        map?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map?.onSaveInstanceState(outState)

    }
    override fun onLowMemory() {
        super.onLowMemory()
        map?.onLowMemory()

    }
    override fun onDestroy() {
        super.onDestroy()
        map?.onDestroy()
    }
}
