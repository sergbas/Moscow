package ru19july.bask.moscowtransport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView




class YandexMapActivity : AppCompatActivity() {

    private var mapview: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("56f599ed-8d55-449e-b970-bb72f3acef84")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_yandex_map)


        mapview = (findViewById<View>(R.id.mapview) as MapView)
        mapview?.getMap()?.move(
                CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0F),
                null)
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
