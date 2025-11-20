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
class FourthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fourth_activity)

        val SignUp = findViewById<TextView>(R.id.Sign_up)

        SignUp.setOnClickListener{
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        val LogIn = findViewById<TextView>(R.id.log_in)

        LogIn.setOnClickListener{
            val intent = Intent(this, FifthActivity::class.java)
            startActivity(intent)
        }

        val backButton : Button = findViewById(R.id.backButton)

        backButton.setOnClickListener{
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

    }


}