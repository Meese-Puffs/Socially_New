package com.Ahmad_Kamran.i230622

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class TwentyFirstActivity : AppCompatActivity() {

    // IMPORTANT: Make sure this URL points to the updated PHP file name if you changed it.
    private val SERVER_URL = "http://192.168.18.51/socially_api/get_user_profile.php"

    private lateinit var profileName: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profileBio: TextView
    private lateinit var profileImage: ImageView
    private lateinit var statusMessage: TextView // To show "Loading" or "Error"
    private lateinit var onlineStatus: TextView // NEW: To show Online/Offline status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // NOTE: You must have a layout file named 'twenty_first_activity.xml'
        setContentView(R.layout.twentyfirst_activity)

        // Initialize UI components (replace with your actual IDs)
        profileName = findViewById(R.id.name)
        profileUsername = findViewById(R.id.bio1)
        profileBio = findViewById(R.id.bio3)
        profileImage = findViewById(R.id.Face)
        statusMessage = findViewById(R.id.bio2) // Add a TextView for status
        onlineStatus = findViewById(R.id.onlineStatus) // NEW: Initialize the status TextView

        // 1. Get the USER_ID from the Intent
        val userId = intent.getIntExtra("USER_ID", -1)

        if (userId != -1) {
            // ID received successfully, start loading
            fetchUserProfile(userId)
        } else {
            // This happens if SeventhActivity failed to pass the ID
            statusMessage.text = "Error: User ID not provided."
            Log.e("ProfileLoad", "User ID not found in Intent extras.")
        }
    }

    private fun fetchUserProfile(userId: Int) {
        statusMessage.text = "Loading profile..."
        onlineStatus.text = "" // Clear status while loading

        Executors.newSingleThreadExecutor().execute {
            try {
                val urlString = "$SERVER_URL?id=$userId"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = StringBuilder()
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                    }
                    val jsonResponse = JSONObject(response.toString())

                    if (jsonResponse.getBoolean("success")) {
                        val userObj = jsonResponse.getJSONObject("user")

                        // NEW: Extract the online status boolean
                        val isOnline = userObj.optBoolean("isOnline", false)

                        // Use runOnUiThread to update the UI on the main thread
                        runOnUiThread {
                            // Clear status message on success
                            statusMessage.text = ""

                            // 3. Display the data
                            profileName.text = userObj.optString("fullName", "N/A")
                            profileUsername.text = "@${userObj.optString("username", "N/A")}"
                            profileBio.text = userObj.optString("bio", "No bio provided.")

                            // 4. Set the Online/Offline status text and color
                            if (isOnline) {
                                onlineStatus.text = "Online"
                                // Use a suitable green color for online
                                onlineStatus.setTextColor(Color.parseColor("#4CAF50"))
                            } else {
                                onlineStatus.text = "Offline"
                                // Use a gray or red color for offline
                                onlineStatus.setTextColor(Color.parseColor("#9E9E9E"))
                            }


                            val imageUrl = userObj.optString("profileImage", "")
                            // TODO: Use a library like Glide or Picasso here to load imageUrl into profileImage
                            Log.d("ProfileLoad", "Profile loaded for ID: $userId")
                        }
                    } else {
                        // Server response indicated failure (e.g., user not found)
                        val message = jsonResponse.optString("message", "User not found.")
                        runOnUiThread {
                            statusMessage.text = "Error loading user: $message"
                            Log.e("ProfileLoad", "Server reported error: $message")
                        }
                    }
                } else {
                    // HTTP connection error
                    runOnUiThread {
                        statusMessage.text = "Error loading user: HTTP ${connection.responseCode}"
                        Log.e("ProfileLoad", "HTTP Error: ${connection.responseCode}")
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    statusMessage.text = "Error loading user: Network failure."
                    Log.e("ProfileLoad", "Network Error: ${e.message}")
                }
            }
        }
    }
}