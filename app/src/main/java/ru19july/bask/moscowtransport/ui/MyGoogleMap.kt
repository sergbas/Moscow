package ru19july.bask.moscowtransport.ui

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ru19july.bask.moscowtransport.interfaces.IMap

class MyGoogleMap : IMap, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(p0: Marker?) = false

    lateinit var googleMap: GoogleMap
    lateinit var mapView: MapView
    lateinit var mapFragment: SupportMapFragment

    constructor(mapFr: SupportMapFragment){
        mapFragment = mapFr
        mapFragment.getMapAsync(this)
    }

    constructor(mapV: MapView){
        Log.d(javaClass.simpleName, "mapView:" + mapV)
        mapView = mapV
        mapView.getMapAsync(this)
    }

    private var mMap: GoogleMap? = null

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d(javaClass.simpleName, "onMapReady, googleMap: " + googleMap)

        mMap?.isMyLocationEnabled = true

        mMap?.getUiSettings()?.setZoomControlsEnabled(true)
        mMap?.setOnMarkerClickListener(this)

        //https://api.mosgorpass.ru/v7/stop?boundsFilter=55.77940526825614,37.61609095395988;55.77067642081403,37.624640337941855&perPage=500&page=0&disablePublicTransport=0

    }

    override public fun placeMarkerOnMap(location: LatLng) {
        Log.d(javaClass.simpleName, "placeMarkerOnMap: " + location)
        val markerOptions = MarkerOptions().position(location)
        mMap?.addMarker(markerOptions)
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
    }
}
