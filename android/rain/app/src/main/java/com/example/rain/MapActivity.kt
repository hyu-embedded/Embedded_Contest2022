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
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import org.json.JSONArray
import org.json.JSONObject


// https://developers.google.com/maps/documentation/android-sdk/start#maps_android_mapsactivity-kotlin

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    private lateinit var mMap: GoogleMap
    lateinit var button: Button
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
        val edt_distance : EditText = findViewById(R.id.editText_distance)
        val btn_search : Button = findViewById(R.id.btn_search)

        var distance : String = edt_distance.text.toString()
        btn_search.setOnClickListener() {
            //var distance : String = edt_distance.text.toString()
            //Toast.makeText(this, distance, Toast.LENGTH_SHORT).show()
            //var url = "http://127.0.0.1:3000/android"
            var url = "http://172.16.161.167:3000/android"

            requestInfo(url, 0)


            if (checkPermissionForLocation(this)) {
                startLocationUpdates()
            }
        }
//        button = findViewById(R.id.btn_search)
//
//        button.setOnClickListener {
//            if (checkPermissionForLocation(this)) {
//                startLocationUpdates()
//            }
//        }

       
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
        moveMap(mLastLocation)
    }

    private fun moveMap(location: Location?){
        if (location != null) {
            mLastLocation = location
        }
        val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        val latLng_1 = LatLng(mLastLocation.latitude+0.0001, mLastLocation.longitude+0.0001)
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng).zoom(17f).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))
        mMap.addMarker(MarkerOptions()
            .position(latLng)
            .title("yeongbin"))
        mMap.addMarker(MarkerOptions()
            .draggable(true).icon(BitmapDescriptorFactory.defaultMarker
                (BitmapDescriptorFactory.HUE_BLUE))
            .position(latLng_1)
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