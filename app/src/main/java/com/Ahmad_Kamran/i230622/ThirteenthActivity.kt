package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ThirteenthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.thirteenth_activity)

        val editButton: Button = findViewById(R.id.editButton)

        editButton.setOnClickListener {
            val intent = Intent(this, FourteenthActivity::class.java)
            startActivity(intent)
        }

        val highlightsButton: Button = findViewById(R.id.highlightButton)

        highlightsButton.setOnClickListener {
            val intent = Intent(this, SixteenthActivity::class.java)
            startActivity(intent)
        }

        val friendsButton: Button = findViewById(R.id.friendsButton)

        friendsButton.setOnClickListener {
            val intent = Intent(this, FifteenthActivity::class.java)
            startActivity(intent)
        }

        val sportButton: Button = findViewById(R.id.sportsButton)

        sportButton.setOnClickListener {
            val intent = Intent(this, FifteenthActivity::class.java)
            startActivity(intent)
        }

        val designButton: Button = findViewById(R.id.DesignButton)

        designButton.setOnClickListener {
            val intent = Intent(this, FifteenthActivity::class.java)
            startActivity(intent)
        }

        val homeButton = findViewById<Button>(R.id.homebutton)
        homeButton.setOnClickListener {
            val intent = Intent(this, FifthActivity::class.java)
            startActivity(intent)
        }

        val searchButton: Button = findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            val intent = Intent(this, SixthActivity::class.java)
            startActivity(intent)
        }

        val likesButton: Button = findViewById(R.id.likesButton)

        likesButton.setOnClickListener {
            val intent = Intent(this, EleventhActivity::class.java)
            startActivity(intent)
        }

    }
}
