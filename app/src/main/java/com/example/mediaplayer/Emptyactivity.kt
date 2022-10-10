package com.example.mediaplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Emptyactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, splashActivity::class.java)
        startActivity(intent)
        finish()
    }
}