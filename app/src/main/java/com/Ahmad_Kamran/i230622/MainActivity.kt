package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "http://192.168.18.51/socially_api/"
    private val TAG = "CONNECTION_TEST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val main = findViewById<LinearLayout>(R.id.main)

        // *** STEP 1: Test Connection on App Start ***
        testDatabaseConnection()

        // *** NEW STEP: SPLASH SCREEN DELAY (5 SECONDS) ***
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, FourthActivity::class.java)
            startActivity(intent)
            finish()   // so user cannot come back to splash
        }, 5000) // 5000 ms = 5 seconds
    }

    // Function to handle the network operation
    private fun testDatabaseConnection() {
        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(BASE_URL + "conn.php")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (response.isSuccessful && responseBody != null) {
                        Log.d(TAG, "API Response: $responseBody")
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "SUCCESS! API is connected to DB.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Log.e(TAG, "Server Error: ${response.code} - ${response.message}")
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "ERROR: Server responded with code ${response.code}. Check XAMPP.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network Error: Could not reach API.", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "NETWORK FAILED: Could not reach $BASE_URL. Is XAMPP running?",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
