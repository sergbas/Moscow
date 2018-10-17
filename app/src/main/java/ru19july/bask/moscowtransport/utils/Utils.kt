package ru19july.bask.moscowtransport.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng


object Utils {

    fun calculationByDistance(startP: LatLng, endP: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(startP.latitude, startP.longitude, endP.latitude, endP.longitude, results)

        return results[0].toDouble()
    }

    fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)

            poly.add(p)
        }

        return poly
    }

    fun makeURL(sourcelat: Double, sourcelog: Double, destlat: Double, destlog: Double): String {
        val urlString = StringBuilder()
        urlString.append("https://maps.googleapis.com/maps/api/directions/json")
        urlString.append("?origin=")// from
        urlString.append(java.lang.Double.toString(sourcelat))
        urlString.append(",")
        urlString
                .append(java.lang.Double.toString(sourcelog))
        urlString.append("&destination=")// to
        urlString
                .append(java.lang.Double.toString(destlat))
        urlString.append(",")
        urlString.append(java.lang.Double.toString(destlog))
        urlString.append("&sensor=false&mode=walking&alternatives=true")
        //urlString.append("&key=AIzaSyA_QY5Hyxg2hif-W-IDpupTUw0yrxjAeC4")
        //urlString.append("&key=AIzaSyBtyeSPfTMfHA-IzHU_qdoCI8aFj_vlnDs")
        urlString.append("&key=AIzaSyDExP61EUO8OWdW3vpE0xJaCJjyOo50E-A")

        return urlString.toString()
    }

    fun findNearestSegment(edges: List<Segment>, loc: LatLng): Int {
        var indx = 0
        var dmin = java.lang.Double.MAX_VALUE
        for (i in edges.indices) {
            val e = edges[i]

            val closestPoint = getClosestPointOnLine(e.start, e.finish, loc)
            val dx = loc.latitude - closestPoint.latitude
            val dy = loc.longitude - closestPoint.longitude
            val d = dx * dx + dy * dy

            if (d < dmin) {
                dmin = d
                indx = i
            }
        }

        val s = edges[indx]

        val dist = FloatArray(1)
        Location.distanceBetween(loc.latitude, loc.longitude, s.finish.latitude, s.finish.longitude, dist)

        return indx
    }

    private fun getClosestPointOnLine(start: LatLng, end: LatLng, p: LatLng): LatLng {
        val dx = end.latitude - start.latitude
        val dy = end.longitude - start.longitude
        val length = dx * dx + dy * dy
        if (length == 0.0) {
            return start
        }
        val v = LatLng(dx, dy)
        val ps = LatLng(p.latitude - start.latitude, p.longitude - start.longitude)
        val psv = ps.latitude * v.latitude + ps.longitude * v.longitude
        val param = psv / length
        return if (param < 0.0) start else if (param > 1.0) end else LatLng(start.latitude + param * v.latitude, start.longitude + param * v.longitude)
    }

    fun getDistanceToFinishFrom(edges: List<Segment>, from: Int): Double {
        var x = 0.0
        for (i in from + 1 until edges.size) {
            x += edges[i].LengthInMeters
        }
        return x
    }
}