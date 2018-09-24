package ru19july.bask.moscowtransport

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    var url = "https://api.mosgorpass.ru/v7/telemetry?bounds=55.773125631218136,37.60907378795798;55.758637899944006,37.623260320667214&exclude="

    companion object {
        const val REQUEST_PERMISSION = 1
    }

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

        doAsync {
            Request(url).run()
            uiThread { longToast("Request performed") }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            Log.d(javaClass.simpleName, "WRITE")
            /// write()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //write()
            }
        }
    }

    class Request(val url: String){
        fun run(){
            val jsonStr = URL(url).readText()
            Log.d(javaClass.simpleName, jsonStr)
        }
    }
}
