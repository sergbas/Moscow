package ru19july.bask.moscowtransport

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import org.jetbrains.anko.doAsync

class MyGoogleMap : IGoogleMap{

    override fun onMarkerClick(p0: Marker?) = false

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //val moscow = LatLng(55.45, 37.37)
        //mMap.addMarker(MarkerOptions().position(moscow).title("Marker in Moscow"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moscow, 12.0f))

        setUpMap()

        mMap.getUiSettings().setZoomControlsEnabled(true)
        mMap.setOnMarkerClickListener(this)

        //https://api.mosgorpass.ru/v7/stop?boundsFilter=55.77940526825614,37.61609095395988;55.77067642081403,37.624640337941855&perPage=500&page=0&disablePublicTransport=0

        doAsync {
            MapsActivity.Request(urlTelemetry).run()
            //uiThread { longToast("Telemetry") }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MapsActivity.REQUEST_PERMISSION)
        } else {
            Log.d(javaClass.simpleName, "WRITE")
            /// write()
        }
    }

}
