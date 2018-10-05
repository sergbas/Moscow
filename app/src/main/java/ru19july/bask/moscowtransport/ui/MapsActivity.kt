package ru19july.bask.moscowtransport.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import ru19july.bask.moscowtransport.MyGoogleMap
import ru19july.bask.moscowtransport.R
import ru19july.bask.moscowtransport.interfaces.IMap
import ru19july.bask.moscowtransport.utils.Utils
import java.io.IOException
import java.net.URL
import java.util.*


//https://www.raywenderlich.com/230-introduction-to-google-maps-api-for-android-with-kotlin
//https://www.bignerdranch.com/blog/embedding-custom-views-with-mapview-v2/

class MapsActivity : AppCompatActivity() {

    private lateinit var myMap: IMap//MyGoogleMap
    private lateinit var mapView: MapView
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

    data class PathResult(val routes: List<Routes>)
    data class Routes(val lon: Float, val lat: Float)

    companion object {
        const val REQUEST_PERMISSION = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
        private const val REQUEST_CHECK_SETTINGS = 3
    }

    val mapType = 0//1 - MapView, 0 - MapFragment

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy : StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(mapType == 0)
            setContentView(R.layout.activity_maps)
        else {
            setContentView(R.layout.activity_maps_view)

            mapView = findViewById(R.id.mapview);
            //mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(fun(googleMap: GoogleMap) {
                gmap = googleMap
                gmap!!.setMinZoomPreference(12F)
                val ny = LatLng(40.7143528, -74.0059731)
                gmap!!.moveCamera(CameraUpdateFactory.newLatLng(ny))
            });
        }
        checkPermissions()

        doAsync {
            Request(urlTelemetry).run()
            //uiThread { longToast("Telemetry") }
        }

        getWeather(LatLng(55.55, 37.77))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location

                val currentLatLng = LatLng(location.latitude, location.longitude)
                newPath(currentLatLng)
                Log.d(javaClass.simpleName, "location-0:" + currentLatLng)
                myMap!!.placeMarkerOnMap(currentLatLng)
            }
        }


        if(mapType == 0) {
            val mapFragment = supportFragmentManager .findFragmentById(R.id.map) as SupportMapFragment
            myMap = MyGoogleMap(mapFragment)
        }
        else {
            mapView = findViewById(R.id.mapview)
            //myMap = MyGoogleMap(mapView)
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation

                if(false)
                    newPath(LatLng(lastLocation.latitude, lastLocation.longitude))

                myMap!!.placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                Log.d(javaClass.simpleName, "location-1:" + lastLocation)
            }
        }
        createLocationRequest()
    }

    private fun newPath(location: LatLng) {
        val r = Random()
        val pathUrl = Utils.makeURL(
                location.latitude,
                location.longitude,
                location.latitude + r.nextDouble() * 0.01 - 0.005,
                location.longitude + r.nextDouble() * 0.01 - 0.005)

        Log.d(javaClass.simpleName, "newPath: " + pathUrl)

        try {
            val jsonStr = URL(pathUrl).readText()
            Log.d(javaClass.simpleName, "PATH: " + jsonStr)

            val path: PathResult = Gson().fromJson(jsonStr, PathResult::class.java)
        }
        catch (e: Exception){

        }
        /*
        https://maps.googleapis.com/maps/api/directions/json?origin=55.7217987,37.6386096&destination=55.7209966399032,37.63444882356039&sensor=false&mode=walking&alternatives=true&key=AIzaSyDExP61EUO8OWdW3vpE0xJaCJjyOo50E-A
        * {
   "geocoded_waypoints" : [
      {
         "geocoder_status" : "OK",
         "place_id" : "ChIJB3Vf4aRKtUYRV709NWFQFKE",
         "types" : [ "establishment", "finance", "point_of_interest" ]
      },
      {
         "geocoder_status" : "OK",
         "place_id" : "ChIJZWtPFz1LtUYR0zonNZ53tDo",
         "types" : [
            "establishment",
            "light_rail_station",
            "point_of_interest",
            "transit_station"
         ]
      }
   ],
   "routes" : [
      {
         "bounds" : {
            "northeast" : {
               "lat" : 55.7226516,
               "lng" : 37.638289
            },
            "southwest" : {
               "lat" : 55.7208869,
               "lng" : 37.6352452
            }
         },
         "copyrights" : "Картографические данные © 2018 Google",
         "legs" : [
            {
               "distance" : {
                  "text" : "0,4 км",
                  "value" : 365
               },
               "duration" : {
                  "text" : "4 мин.",
                  "value" : 269
               },
               "end_address" : "Дубининская ул., 57, Москва, Россия, 115054",
               "end_location" : {
                  "lat" : 55.7208869,
                  "lng" : 37.6352452
               },
               "start_address" : "Дубининская ул., 53 стр. 5, Москва, Россия, 115054",
               "start_location" : {
                  "lat" : 55.7220632,
                  "lng" : 37.638289
               },
               "steps" : [
                  {
                     "distance" : {
                        "text" : "0,2 км",
                        "value" : 158
                     },
                     "duration" : {
                        "text" : "2 мин.",
                        "value" : 117
                     },
                     "end_location" : {
                        "lat" : 55.7226516,
                        "lng" : 37.6360382
                     },
                     "html_instructions" : "Следуйте на \u003cb\u003eзапад\u003c/b\u003e в сторону \u003cb\u003eул. Дубининская\u003c/b\u003e\u003cdiv style=\"font-size:0.9em\"\u003eДорога с ограниченным доступом\u003c/div\u003e",
                     "polyline" : {
                        "points" : "{ebsIifvdFUxB?BABABA@[X?@A?WtC?B?@ABA@Qb@?@A@?@Ix@AV"
                     },
                     "start_location" : {
                        "lat" : 55.7220632,
                        "lng" : 37.638289
                     },
                     "travel_mode" : "WALKING"
                  },
                  {
                     "distance" : {
                        "text" : "0,2 км",
                        "value" : 207
                     },
                     "duration" : {
                        "text" : "3 мин.",
                        "value" : 152
                     },
                     "end_location" : {
                        "lat" : 55.7208869,
                        "lng" : 37.6352452
                     },
                     "html_instructions" : "Поверните \u003cb\u003eналево\u003c/b\u003e на \u003cb\u003eул. Дубининская\u003c/b\u003e\u003cdiv style=\"font-size:0.9em\"\u003eПункт назначения будет справа\u003c/div\u003e",
                     "maneuver" : "turn-left",
                     "polyline" : {
                        "points" : "qibsIgxudFr@TRDLD\\NLDv@ZvAf@PFz@X"
                     },
                     "start_location" : {
                        "lat" : 55.7226516,
                        "lng" : 37.6360382
                     },
                     "travel_mode" : "WALKING"
                  }
               ],
               "traffic_speed_entry" : [],
               "via_waypoint" : []
            }
         ],
         "overview_polyline" : {
            "points" : "{ebsIifvdFYdC]\\YzCUj@MtArBp@nDpAz@X"
         },
         "summary" : "ул. Дубининская",
         "warnings" : [
            "Пешие маршруты находятся в режиме бета-тестирования. – Внимание! На предлагаемом маршруте могут отсутствовать тротуары или пешеходные дорожки."
         ],
         "waypoint_order" : []
      }
   ],
   "status" : "OK"
}
        * */

    }

    private var gmap: GoogleMap? = null


    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
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

    private fun getWeather(currentLatLng: LatLng) {
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
