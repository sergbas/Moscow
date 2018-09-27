package ru19july.bask.moscowtransport

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MyGoogleMap(mapFr: SupportMapFragment) : OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(p0: Marker?) = false

    lateinit var googleMap: GoogleMap
    private var mapFragment: SupportMapFragment

    init{
        mapFragment = mapFr
        mapFragment.getMapAsync(this)
    }

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true

        mMap.getUiSettings().setZoomControlsEnabled(true)
        mMap.setOnMarkerClickListener(this)

        //https://api.mosgorpass.ru/v7/stop?boundsFilter=55.77940526825614,37.61609095395988;55.77067642081403,37.624640337941855&perPage=500&page=0&disablePublicTransport=0

    }

    public fun placeMarkerOnMap(location: LatLng) {
        Log.d(javaClass.simpleName, "placeMarkerOnMap: " + location)
        val markerOptions = MarkerOptions().position(location)
        mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
    }
}
