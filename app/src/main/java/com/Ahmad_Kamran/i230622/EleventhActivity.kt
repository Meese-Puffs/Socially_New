package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EleventhActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eleventh_activity)

        val searchButton: Button = findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            val intent = Intent(this, SixthActivity::class.java)
            startActivity(intent)
        }

        val homeButton: Button = findViewById(R.id.homebutton)

        homeButton.setOnClickListener {
            val intent = Intent(this, FifthActivity::class.java)
            startActivity(intent)
        }

        val YouButton: Button = findViewById(R.id.YouButton)

        YouButton.setOnClickListener {
            val intent = Intent(this, TwelfthActivity::class.java)
            startActivity(intent)
        }

    }

}