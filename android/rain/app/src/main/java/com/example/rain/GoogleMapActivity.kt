package com.example.rain

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var zoom: Float = 16f
    private val server_url: String = "http://10.0.2.2:3000/android"
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var infoWindow: View
    private lateinit var infoTitle: TextView
    private lateinit var info_btn_select: Button
    private var neighbors = JSONObject().put("count", 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)

        mLocationRequest =  LocationRequest.create().apply {
            interval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.infoWindow = layoutInflater.inflate(R.layout.create_infowindow, null) as View
        this.info_btn_select = infoWindow.findViewById(R.id.btn_select) as Button


        getLocation()

        thread(start = true) {
            while(true) {
                requestInfo(server_url, 37.40, -120.08, zoom)

                Log.d("Near", "Count: ${neighbors.getInt("count")}")

                Thread.sleep(10000)
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        Toast.makeText(this, "${this.location.latitude}", Toast.LENGTH_SHORT).show()

        // Mark current position
        val cur_pos = LatLng(this.location.latitude, this.location.longitude)
        this.map.addMarker(MarkerOptions()
            .position(cur_pos))
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_pos, this.zoom))

        for (i:Int in 0 until this.neighbors.getInt("count")) {
            var target: JSONObject = this.neighbors.getJSONObject("${i}")
            var t_loc = target.getDouble("loc")
            var t_lat = target.getDouble("lat")
            var t_floor = target.getInt("floor")
            var t_waterlevel = target.getInt("waterlevel")
            var t_status = target.getInt("status")
            var t_isAssigned = target.getBoolean("isAssigned")

            Log.d("Near", "Loc: ${t_loc}, Lat: ${t_lat}, Floor: ${t_floor}, Waterlevel: ${t_waterlevel}")

            this.map.addMarker(MarkerOptions()
                .position(LatLng(t_lat, t_loc))
            )
            val circleOptions = CircleOptions()
                .center(LatLng(t_lat, t_loc))
                .radius(30.0)
                .strokeWidth(40F)
                .strokeColor(0xffff0000.toInt())
                .fillColor(0xff000000.toInt())
            this.map.addCircle(circleOptions)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            return
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        //locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, mLocationCallback, Looper.myLooper())

        var mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }


    private fun requestInfo(url: String, lat: Double, loc: Double, zoom: Float) {

        var target_url = "${url}?lat=${lat}&loc=${loc}&zoom=${zoom}"

        Log.d("Connect", "try to connect...")

        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            target_url,
            null,
            Response.Listener<JSONObject> { response ->
                for (k in response.keys()) {
                    Log.d("Connect", "${k}: ${response.get(k).toString()}")
                    //result.put(k, response.get(k))
                }
                //neighbors = JSONObject(response.toString())
                neighbors = JSONObject(response.toString())
            },
            Response.ErrorListener { error -> Log.d("error", "error...$error") }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)

    }


}