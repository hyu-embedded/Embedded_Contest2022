package com.example.rain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.example.rain.GoogleMapActivity
import com.example.rain.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    var url = "http://10.0.2.2:3000/android/login"
//    requestInfo(url, 0)
//    /login?id=yunsang&password=1234

    fun onMatch(view: View) {
        val textId = findViewById<EditText>(R.id.editTextId)
        val textPass = findViewById<EditText>(R.id.editTextTextPassword)
        val jsonObject = JSONObject()
        jsonObject.put("id", textId.text.toString())
        jsonObject.put("password", textPass.text.toString())
        login(url, textId.text.toString(),textPass.text.toString() )

    }
    fun login(url: String, id: String, password: String) {
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET,
            url + "?id=" + id + "&password=" + password,
            null,
            Response.Listener<JSONObject> { response ->
                if(response.get("status") == "ok") {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show()
                    val nextIntent = Intent(this, GoogleMapActivity::class.java)
                    nextIntent.putExtra("id", id)
                    startActivity(nextIntent)
                }
                                          },
            Response.ErrorListener { Log.d("Error", "Login Service") }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonRequest)

    }
}