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
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONObject

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var zoom: Float = 16f
    private val server_url: String = "http://10.0.2.2:3000/android"

    private lateinit var infoWindow: View
    private lateinit var infoTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.infoWindow = layoutInflater.inflate(R.layout.create_infowindow, null);

        getLocation()



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

        // Mark 

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    private fun requestInfo(url: String, location: Location, zoom: Float) {
        var result = JSONObject()

        val message = JSONObject()
        message.put("lat", location.latitude)
        message.put("loc", location.longitude)
        message.put("zoom", zoom)

        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            message,
            Response.Listener<JSONObject> { response ->
                for (k in response.keys()) {
                    result.put(k, response.get(k))
                }
            },
            Response.ErrorListener { error -> Log.d("error", "error...$error") }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)

        return result
    }


}