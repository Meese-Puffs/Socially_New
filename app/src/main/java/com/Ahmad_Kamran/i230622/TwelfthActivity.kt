package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TwelfthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.twelfth_activity)

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

        val followingButton: Button = findViewById(R.id.followingButton)

        followingButton.setOnClickListener {
            val intent = Intent(this, EleventhActivity::class.java)
            startActivity(intent)
        }

    }

}