package ru19july.bask.moscowtransport

import com.google.android.gms.maps.model.LatLng

interface IMap {
    fun placeMarkerOnMap(currentLatLng: LatLng)
}
