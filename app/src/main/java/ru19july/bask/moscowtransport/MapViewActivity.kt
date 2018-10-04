package ru19july.bask.moscowtransport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapViewActivity : AppCompatActivity() {

    private var mapview: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        var layout = findViewById<View>(R.id.layout_mapview) as LinearLayout

        mapview = createYandexmap()

        layout.addView(mapview)

        moveYandexMap(mapview)
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

        mapview = MapView(this)
        mapview?.setLayoutParams(LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return  mapview
    }

    override fun onStop() {
        super.onStop()
        mapview?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview?.onStart()
        MapKitFactory.getInstance().onStart()
    }
}
