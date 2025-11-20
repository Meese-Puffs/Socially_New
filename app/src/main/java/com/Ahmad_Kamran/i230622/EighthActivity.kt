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

class EighthActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eighth_activity)

        val messageButton0: Button = findViewById(R.id.messageButton0)

        messageButton0.setOnClickListener {
            val intent = Intent(this, FifthActivity::class.java)
            startActivity(intent)
        }

        val messageButton1: Button = findViewById(R.id.messageButton1)

        messageButton1.setOnClickListener {
            val intent = Intent(this, NinthActivity::class.java)
            startActivity(intent)
        }

        val messageButton2: Button = findViewById(R.id.messageButton2)

        messageButton2.setOnClickListener {
            val intent = Intent(this, NinthActivity::class.java)
            startActivity(intent)
        }

        val messageButton3: Button = findViewById(R.id.messageButton3)

        messageButton3.setOnClickListener {
            val intent = Intent(this, NinthActivity::class.java)
            startActivity(intent)
        }

        val messageButton4: Button = findViewById(R.id.messageButton4)

        messageButton4.setOnClickListener {
            val intent = Intent(this, NinthActivity::class.java)
            startActivity(intent)
        }

        val messageButton5: Button = findViewById(R.id.messageButton5)

        messageButton5.setOnClickListener {
            val intent = Intent(this, NinthActivity::class.java)
            startActivity(intent)
        }

        val messageButton6: Button = findViewById(R.id.messageButton6)

        messageButton6.setOnClickListener {
            val intent = Intent(this, NinthActivity::class.java)
            startActivity(intent)
        }

        val cameraButton: Button = findViewById(R.id.CameraButton)

        cameraButton.setOnClickListener {
            val intent = Intent(this, TwentiethActivity::class.java)
            startActivity(intent)
        }

    }

}