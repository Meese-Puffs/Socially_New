package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FifthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fifth_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fifth_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchButton: Button = findViewById(R.id.searchbutton)

        searchButton.setOnClickListener {
            val intent = Intent(this, SixthActivity::class.java)
            startActivity(intent)
        }

        val messageButton: Button = findViewById(R.id.messagebutton)

        messageButton.setOnClickListener {
            val intent = Intent(this, EighthActivity::class.java)
            startActivity(intent)
        }

        val likesButton: Button = findViewById(R.id.likesButton)

        likesButton.setOnClickListener {
            val intent = Intent(this, EleventhActivity::class.java)
            startActivity(intent)
        }

        val profileButton: Button = findViewById(R.id.profileButton)

        profileButton.setOnClickListener {
            val intent = Intent(this, ThirteenthActivity::class.java)
            startActivity(intent)
        }

        val story1Button: Button = findViewById(R.id.story1Button)

        story1Button.setOnClickListener {
            val intent = Intent(this, SeventeenthActivity::class.java)
            startActivity(intent)
        }

        val story2Button: Button = findViewById(R.id.story2Button)

        story2Button.setOnClickListener {
            val intent = Intent(this, SeventeenthActivity::class.java)
            startActivity(intent)
        }

        val story3Button: Button = findViewById(R.id.story3Button)

        story3Button.setOnClickListener {
            val intent = Intent(this, SeventeenthActivity::class.java)
            startActivity(intent)
        }

        val story4Button: Button = findViewById(R.id.story4Button)

        story4Button.setOnClickListener {
            val intent = Intent(this, SeventeenthActivity::class.java)
            startActivity(intent)
        }

        val cameraButton: Button = findViewById(R.id.camerabutton)

        cameraButton.setOnClickListener {
            val intent = Intent(this, TwentiethActivity::class.java)
            startActivity(intent)
        }

        val profButton: Button = findViewById(R.id.userProf)

        profButton.setOnClickListener {
            val intent = Intent(this, TwentyFirstActivity::class.java)
            startActivity(intent)
        }

        val reelButton: Button = findViewById(R.id.reelsButton)

        reelButton.setOnClickListener {
            val intent = Intent(this, SixteenthActivity::class.java)
            startActivity(intent)
        }

    }
}
