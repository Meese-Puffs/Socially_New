package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ImageView
import android.text.InputType
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SeventhActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.seventh_activity)

            val homeButton = findViewById<Button>(R.id.homebutton)
            homeButton.setOnClickListener {
                val intent = Intent(this, FifthActivity::class.java)
                startActivity(intent)
            }

            val searchButton: Button = findViewById(R.id.searchbutton)

            searchButton.setOnClickListener {
                val intent = Intent(this, SixthActivity::class.java)
                startActivity(intent)
            }

        }

    }