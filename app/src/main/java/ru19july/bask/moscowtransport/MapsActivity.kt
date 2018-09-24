package ru19july.bask.moscowtransport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val moscow = LatLng(55.45, 37.37)
        mMap.addMarker(MarkerOptions().position(moscow).title("Marker in Moscow"))
        mMap.moveCamera(CameraUpdateFactory.zoomBy(12.0f))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(moscow))

        //https://api.mosgorpass.ru/v7/stop?boundsFilter=55.77940526825614,37.61609095395988;55.77067642081403,37.624640337941855&perPage=500&page=0&disablePublicTransport=0
        //https://api.mosgorpass.ru/v7/telemetry?bounds=55.773125631218136,37.60907378795798;55.758637899944006,37.623260320667214&exclude=

    }
}
