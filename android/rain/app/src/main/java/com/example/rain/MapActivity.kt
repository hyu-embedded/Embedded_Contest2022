package com.example.rain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.CircleOptions

import org.json.JSONArray
import org.json.JSONObject


// https://developers.google.com/maps/documentation/android-sdk/start#maps_android_mapsactivity-kotlin

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    private lateinit var mMap: GoogleMap
    private lateinit var infoWindow: ViewGroup
    private lateinit var info_waterlvel: TextView
    private lateinit var info_floor: TextView
    private lateinit var info_btn_select: Button
    private lateinit var info_btn_done: Button
    private lateinit var infoButtonListener: GoogleMap.OnInfoWindowClickListener

    lateinit var button: Button
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mLocationRequest =  LocationRequest.create().apply {

            interval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
        val mapLayout = findViewById<LinearLayout>(R.id.mapLayout)

        val layoutInflater: LayoutInflater = LayoutInflater.from(applicationContext)
        infoWindow = layoutInflater.inflate(R.layout.create_infowindow, mapLayout, false) as ViewGroup
        info_waterlvel = infoWindow.findViewById(R.id.waterlevelTxt) as TextView
        info_floor = infoWindow.findViewById(R.id.floorTxt) as TextView
        info_btn_select = infoWindow.findViewById(R.id.btn_select) as Button
        info_btn_done = infoWindow.findViewById(R.id.btn_done) as Button

        info_btn_select.setOnClickListener() {

        }

        info_btn_done.setOnClickListener() {

        }

//        val edt_distance : EditText = findViewById(R.id.editText_distance)
        val btn_searchLocation : Button = findViewById(R.id.btn_search)
        val btn_zoomLoaction : Button = findViewById(R.id.btn_location)
        btn_zoomLoaction.isEnabled = false
//        var distance : String = edt_distance.text.toString()
        btn_searchLocation.setOnClickListener() {
            //var distance : String = edt_distance.text.toString()
            //Toast.makeText(this, distance, Toast.LENGTH_SHORT).show()
            //var url = "http://127.0.0.1:3000/android"
//            var url = "http://10.0.2.2:3000/android"
            btn_zoomLoaction.isEnabled = true
//            requestInfo(url, 0)

            if (checkPermissionForLocation(this)) {
                startLocationUpdates()
//                button.isEnabled = false
            }
        }
//        button.performClick()




        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        btn_zoomLoaction.setOnClickListener(){
            if(mLastLocation != null) {
                myLocationUpdates(mLastLocation)
            }

        }
    }
    private fun myLocationUpdates(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng).zoom(16f).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
//        mMap.setOnMarkerClickListener { this }
    }

    private fun startLocationUpdates() {

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }


    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location?) {
        if (location != null) {
            mLastLocation = location
        }
        val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        //val latLng = LatLng(37.555940, 127.049470)

        mMap.addMarker(MarkerOptions()
            .position(latLng)
            .title("asdasdasd")
            .snippet("Population"))?.showInfoWindow()
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(30.0)
            .strokeWidth(40F)
            .strokeColor(0xffff0000.toInt()) //alpha, R, G, B = 0x xx xx xx xx
            .fillColor(0xff000000.toInt())
//            .strokeColor(255000)
        mMap.addCircle(circleOptions)
//        indicateLocation(location information)
    }
// TODO: locate other information
    private fun indicateLocation(location: Location?){
        if (location != null) {
            mLastLocation = location
        }
        val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        mMap.addMarker(MarkerOptions()
            .draggable(true).icon(BitmapDescriptorFactory.defaultMarker
                (BitmapDescriptorFactory.HUE_BLUE))
            .position(latLng)
            .title("yeongbin"))
    }

    // 위치 권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION
                )
                false
            }
        } else {
            true
        }
    }

    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()

            } else {
                Log.d("ttt", "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near HYU-IoT Lab.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    fun requestInfo(url: String, distance: Int) {

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONArray> { response ->
                for (i in 0 until response.length()) {
                    val jsonObject = response[i] as JSONObject
                    val id = jsonObject.getString("id")
                    val loc = jsonObject.getString("loc")
                    val lat = jsonObject.getString("lat")
                    val floor = jsonObject.getString("floor")
                    val waterlevel = jsonObject.getString("waterlevel")
                    val status = jsonObject.getString("status")

                    Log.d("http", "ID: $id\nloc: $loc, lat: $lat, floor: $floor\nwaterlevel: $waterlevel, status: $status")
                }
            },
            Response.ErrorListener { error -> Log.d("error", "error...$error") }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(jsonArrayRequest)
    }
}