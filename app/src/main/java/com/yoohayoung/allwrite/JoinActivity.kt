package com.yoohayoung.allwrite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class JoinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val btn_join = findViewById<Button>(R.id.btn_join)
        btn_join.setOnClickListener {
            var startLoginActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(startLoginActivityIntent)
        }
    }
}