package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EighteenthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eighteenth_activity)

        val continueButton: Button = findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            val intent = Intent(this, NinteenthActivity::class.java)
            startActivity(intent)
        }

        val backButton: Button = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, FifthActivity::class.java)
            startActivity(intent)
        }

    }


}