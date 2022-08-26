// https://dalgonakit.tistory.com/137
package com.example.embedded_contest.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.embedded_contest.R

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 내용 작성하기
        setContentView(R.layout.activity_hello)

        var btnHello = findViewById<Button>(R.id.btnHello)
        btnHello.setOnClickListener() {
            // 클릭시 동작할 코드 작성 필요
            Toast.makeText(this, "Hello world", Toast.LENGTH_SHORT).show()
        }
    }
}