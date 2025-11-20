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

class NinthActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ninth_activity)

        val callButton : Button = findViewById(R.id.callButton)

        callButton.setOnClickListener{
            val intent = Intent(this, TenthActivity::class.java)
            startActivity(intent)
        }

        val backButton : Button = findViewById(R.id.backButton)

        backButton.setOnClickListener{
            val intent = Intent(this, EighthActivity::class.java)
            startActivity(intent)
        }

    }

}