package com.Ahmad_Kamran.i230622

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.LinearLayout
import android.content.Intent
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Reference to your main layout
        val main = findViewById<LinearLayout>(R.id.main)

        // Click event to move to FourthActivity
        main.setOnClickListener {
            val intent = Intent(this, FourthActivity::class.java)
            startActivity(intent)
        }

        // ---------------------------
        // 📡 Fetch stories from backend
        // ---------------------------
        val api = ApiClient.instance.create(ApiInterface::class.java)

        api.getStories().enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("API_SUCCESS", "Response: ${response.body()}")
                    Toast.makeText(this@MainActivity, "Stories fetched!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Response error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
                Toast.makeText(this@MainActivity, "Network failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}