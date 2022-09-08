// https://dalgonakit.tistory.com/137
package com.example.embeddedcontest.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.embeddedcontest.R


class MapActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var btn_run = findViewById<Button>(R.id.btn_run)
        var editText = findViewById<EditText>(R.id.editTextNumberDecimal)




        btn_run.setOnClickListener() {
            // 클릭시 동작할 코드
            val distance: Int? = editText.text.toInt()
            println(distance)
            Toast.makeText(this, distance, Toast.LENGTH_SHORT).show()

        }



    }


}