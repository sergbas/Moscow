package ru19july.bask.moscowtransport.mapview

import android.content.Context
import android.os.Bundle
import android.view.View
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import ru19july.bask.moscowtransport.interfaces.IMapView

class YandexmapView(context: Context, accessKey: String) : View(context), IMapView {
    private var mapview: MapView

    init {
        MapKitFactory.setApiKey(accessKey)
        MapKitFactory.initialize(context)

        mapview = MapView(context)
    }

    override fun onStart() {
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroy() {

    }

    override fun onLowMemory() {

    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

    override fun moveTo(latitude: Double, longitude: Double, zoom: Double) {
        mapview.getMap()?.move(
                CameraPosition(Point(latitude, longitude), zoom.toFloat(), 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 3F),
                null)
    }

    override fun getView(): View? {
        return mapview
    }

}
