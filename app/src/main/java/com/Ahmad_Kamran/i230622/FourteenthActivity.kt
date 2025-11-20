package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FourteenthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fourteenth_activity)

        val cancelButton : Button = findViewById(R.id.cancelButton)

        cancelButton.setOnClickListener{
            val intent = Intent(this, ThirteenthActivity::class.java)
            startActivity(intent)
        }

        val doneButton : Button = findViewById(R.id.doneButton)

        doneButton.setOnClickListener{
            val intent = Intent(this, ThirteenthActivity::class.java)
            startActivity(intent)
        }


    }


}