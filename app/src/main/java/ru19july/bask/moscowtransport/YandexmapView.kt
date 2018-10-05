package ru19july.bask.moscowtransport

import android.content.Context
import android.os.Bundle
import android.view.View
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class YandexmapView(context: Context, accessKey: String) : View(context), IMapView {
    private var mapview: MapView

    init {
        MapKitFactory.setApiKey("56f599ed-8d55-449e-b970-bb72f3acef84")
        MapKitFactory.initialize(context)

        mapview = MapView(context)
    }

    override fun onStart() {
        mapview?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {
        mapview?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroy() {

    }

    override fun onLowMemory() {

    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

    override fun moveTo(latitude: Double, longitude: Double) {
        mapview?.getMap()?.move(
                CameraPosition(Point(latitude, longitude), 12.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 3F),
                null)
    }

    override fun getView(): View? {
        return mapview
    }

}
