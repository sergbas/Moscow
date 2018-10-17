package ru19july.bask.moscowtransport.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import ru19july.bask.moscowtransport.R
import ru19july.bask.moscowtransport.interfaces.IMapView
import ru19july.bask.moscowtransport.ui.views.MapboxView

class MapViewActivity : AppCompatActivity() {

    private var map : IMapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        var layout = findViewById<View>(R.id.layout_mapview) as LinearLayout

        var time = System.currentTimeMillis().toInt();
        if(time % 1 == 0)
            map = MapboxView(applicationContext, getString(R.string.app_access_token), this)
        //else
            //map = YandexmapView(this, "")

        layout.addView(map?.getView())

        map?.moveTo(55.7507, 37.6177)
    }

    override fun onStop() {
        super.onStop()
        map?.onStop()
    }

    override fun onStart() {
        super.onStart()
        map?.onStart()
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

