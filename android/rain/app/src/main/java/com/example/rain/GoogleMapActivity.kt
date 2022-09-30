package com.example.rain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject
import kotlin.concurrent.thread


class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var zoom: Float = 16f
    private val server_url: String = "http://192.168.0.107:3000/android"
    //private val server_url: String = "http://10.0.2.2:3000/android"
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var infoWindow: View
    private lateinit var infoTitle: TextView
    private lateinit var info_btn_select: Button
    private lateinit var info_btn_done: Button
    private var neighbors = JSONObject().put("count", 0)
    private lateinit var card_view: CardView
    private lateinit var btn_zoomin: Button
    private var isStart: Boolean = true
    private var markers: Map<Int, Marker> = mutableMapOf();


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

        this.btn_zoomin = findViewById(R.id.btn_zoom) as Button
        this.btn_zoomin.isEnabled = false
        this.btn_zoomin.setOnClickListener(){
            if(location != null) {
                indicate_current_pos(location)
            }
        }

        this.card_view = findViewById(R.id.card_view) as CardView
        card_view.visibility = View.INVISIBLE

        getLocation()

        thread(start = true) {
            while(true) {
                request_neighbor_info(server_url+"/search", 37.40, -120.08, zoom)

                Log.d("Near", "Count: ${neighbors.getInt("count")}")

                Thread.sleep(1000)
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        map!!.setOnMarkerClickListener(object: GoogleMap.OnMarkerClickListener{
            override fun onMarkerClick(p1: Marker): Boolean {
                card_view.visibility = View.VISIBLE
                var text_waterlevel = findViewById<TextView>(R.id.waterlevelTxt)
                var text_floor = findViewById<TextView>(R.id.floorTxt)
                var btn_choose = findViewById<Button>(R.id.btn_choose)
                var btn_done = findViewById<Button>(R.id.btn_done)
                btn_done.isEnabled = false

                var arr = p1.tag.toString().split("/")
                var t_id = arr[0]
                text_waterlevel.text = arr[1] + "mm"
                text_floor.text = arr[2]
                var t_status = arr[3].toInt()

                if (t_status > 0) {
                    btn_choose.isEnabled = true
                    btn_done.isEnabled = false
                }


                btn_choose.setOnClickListener() {
                    p1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    btn_choose.isEnabled = false
                    btn_done.isEnabled = true
                    update_neighbor(t_id);
                }

                btn_done.setOnClickListener() {
                    btn_done.isEnabled = false
                    p1.isVisible = false
                    //p1.remove()
                    p1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    notify_select_client(t_id);
                }

                return false
            }
        })

        map!!.setOnMapClickListener(object: GoogleMap.OnMapClickListener{
            override fun onMapClick(p0: LatLng) {
                card_view.visibility = View.INVISIBLE

                var btn_choose = findViewById<Button>(R.id.btn_choose)
                var btn_done = findViewById<Button>(R.id.btn_done)

                btn_choose.isEnabled = false
                btn_done.isEnabled = false
            }
        })

    }


    override fun onLocationChanged(location: Location) {
        this.location = location
        if (this.location != null) {
            this.btn_zoomin.isEnabled = true
//            if (this.isStart) {
//                this.btn_zoomin.performClick()
//                this.isStart=false
//            }
        }
        //Toast.makeText(this, "${this.location.latitude}", Toast.LENGTH_SHORT).show()

        // Mark current position
        val cur_pos = LatLng(this.location.latitude, this.location.longitude)
        val circleOptions = CircleOptions()
            .center(cur_pos)
            .radius(20.0)
            .strokeWidth(4F)
            .strokeColor(0xff0000.toInt())
            .fillColor(0xffff0000.toInt())
        this.map.addCircle(circleOptions)

//        this.map.addMarker(MarkerOptions()
//            .position(cur_pos))
        //this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_pos, this.zoom))

        for (i:Int in 0 until this.neighbors.getInt("count")) {
            var target: JSONObject = this.neighbors.getJSONObject("${i}")
            var t_id = target.getInt("id")
            var t_loc = target.getDouble("loc")
            var t_lat = target.getDouble("lat")
            var t_floor = target.getInt("floor")
            var t_waterlevel = target.getInt("waterlevel")
            var t_status = target.getInt("status")
            var t_isAssigned = target.getBoolean("isAssigned")

            Log.d("Near", "Loc: ${t_loc}, Lat: ${t_lat}, Floor: ${t_floor}, Waterlevel: ${t_waterlevel}")

            var defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)

            when(t_status) {
                -1 -> defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                1 -> defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                2 -> defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                3 -> defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }

            if (t_isAssigned) {
                defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            }

            if (t_status < 0) continue

            var marker : Marker? = this.map.addMarker(MarkerOptions()
                .position(LatLng(t_lat, t_loc))
                .icon(defaultMarker)
            )
            marker!!.tag = "${t_id}/${t_waterlevel}/${t_floor}/${t_status}"

//            val circleOptions = CircleOptions()
//                .center(LatLng(t_lat, t_loc))
//                .radius(15.0)
//                .strokeWidth(4F)
//                .strokeColor(0xff000000.toInt())
//                .fillColor(0xff000000.toInt())
//            this.map.addCircle(circleOptions)


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

    private fun indicate_current_pos(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng).zoom(16f).build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    private fun update_neighbor(id: String) {
        postInfo(server_url + "/update", id);
    }

    private fun notify_select_client(id: String) {
        postInfo(server_url + "/done", id);
    }

    private fun postInfo(url: String, id: String) {
        var target_url = "${url}?id=${id}"

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


    private fun request_neighbor_info(url: String, lat: Double, loc: Double, zoom: Float) {

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