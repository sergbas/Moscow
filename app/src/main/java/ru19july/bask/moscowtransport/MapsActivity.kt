package ru19july.bask.moscowtransport

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.net.URL

//https://www.raywenderlich.com/230-introduction-to-google-maps-api-for-android-with-kotlin

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    var urlTelemetry = "https://api.mosgorpass.ru/v7/telemetry?bounds=55.773125631218136,37.60907378795798;55.758637899944006,37.623260320667214&exclude="
    var urlWeather = "http://api.openweathermap.org/data/2.5/forecast/daily?APPID=15646a06818f61f7b8d7823ca833e1ce&lat=%f&lon=%f&mode=json&units=metric&cnt=7"

    data class ForecastResult(val city: City, val list: List<Forecast>)

    data class City(val id: Long, val name: String, val coord: Coordinates, val country: String, val population: Int)
    data class Coordinates(val lon: Float, val lat: Float)
    data class Forecast(val dt: Long, val temp: Temperature, val pressure: Float, val humidity: Int, val weather: List<Weather>, val speed: Float, val deg: Int, val clouds: Int, val rain: Float)
    data class Temperature(val day: Float, val min: Float, val max: Float, val night: Float, val eve: Float, val morn: Float)
    data class Weather(val id: Long, val main: String, val description: String, val icon: String)

    companion object {
        const val REQUEST_PERMISSION = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
        private const val REQUEST_CHECK_SETTINGS = 3
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                Log.d(javaClass.simpleName, "location-1:" + lastLocation)

            }
        }
        createLocationRequest()
    }

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
            Request(urlTelemetry).run()
            //uiThread { longToast("Telemetry") }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            Log.d(javaClass.simpleName, "WRITE")
            /// write()
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapsActivity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                Log.d(javaClass.simpleName, "location-0:" + currentLatLng)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))

                doAsync {
                    val url = java.lang.String.format(urlWeather, currentLatLng.latitude, currentLatLng.longitude)
                    val jsonStr = URL(url).readText()

                    Log.d(javaClass.simpleName, "Weather: " + jsonStr)

                    val forecast : ForecastResult = Gson().fromJson(jsonStr, ForecastResult::class.java)
                    Log.d(javaClass.simpleName, "City: " + forecast.city.name + ", population:" + forecast.city.population)
                    Log.d(javaClass.simpleName, "Day temp: " + forecast.list[0].temp.day)
                    Log.d(javaClass.simpleName, "Weather: " + forecast.list[0].weather[0].main + " - " + forecast.list[0].weather[0].description)

                    uiThread { longToast("Weather: " + forecast.list[0].temp.day + "; " + forecast.list[0].weather[0].main + " - " + forecast.list[0].weather[0].description) }

                    Log.d(javaClass.simpleName, "WEATHER:" + forecast)
                }
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        mMap.addMarker(markerOptions)
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
            Log.d(javaClass.simpleName, "Request: " + url)
            val jsonStr = URL(url).readText()
            Log.d(javaClass.simpleName, jsonStr)
        }
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 3
    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }
}
