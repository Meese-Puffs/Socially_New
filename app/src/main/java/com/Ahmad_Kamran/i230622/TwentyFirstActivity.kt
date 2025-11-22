package com.Ahmad_Kamran.i230622

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.Executors

// NOTE: BaseLoggedInActivity is assumed to be defined elsewhere and handles basic authentication
class TwentyFirstActivity : BaseLoggedInActivity() {

    // IMPORTANT: Make sure this URL points to the updated PHP file name if you changed it.
    private val SERVER_URL = "http://192.168.18.51/socially_api/get_user_profile.php"

    // UI elements for profile details
    private lateinit var profileName: TextView
    private lateinit var profileUsername: TextView // Full Name
    private lateinit var profileBio: TextView
    private lateinit var profileImage: ImageView
    private lateinit var statusMessage: TextView // For showing loading/error status (R.id.bio2)
    private lateinit var onlineStatus: TextView // To show Online/Offline status
    private lateinit var followerNumTextView: TextView // Follower count

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.twentyfirst_activity)

        // Initialize UI components
        profileName = findViewById(R.id.name)
        profileUsername = findViewById(R.id.bio1)
        profileBio = findViewById(R.id.bio3)
        profileImage = findViewById(R.id.Face)
        statusMessage = findViewById(R.id.bio2)
        onlineStatus = findViewById(R.id.onlineStatus)
        followerNumTextView = findViewById(R.id.followerNum)

        // 1. Get the USER_ID from the Intent
        val userId = intent.getIntExtra("USER_ID", -1)

        if (userId != -1) {
            fetchUserProfile(userId)
        } else {
            statusMessage.text = "Error: User ID not provided."
            Toast.makeText(this, "Application error: User ID missing.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserProfile(userId: Int) {
        // Reset UI indicators
        statusMessage.text = "Loading profile..."
        onlineStatus.text = "..."
        followerNumTextView.text = "..."

        Executors.newSingleThreadExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val urlString = "$SERVER_URL?id=$userId"
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                // Set reasonable timeouts
                connection.connectTimeout = 10000 // 10 seconds to connect
                connection.readTimeout = 10000 // 10 seconds to read data

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = StringBuilder()
                    // Read the entire server response stream
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                    }
                    val responseString = response.toString()

                    try {
                        val jsonResponse = JSONObject(responseString)

                        if (jsonResponse.getBoolean("success")) {
                            val userObj = jsonResponse.getJSONObject("user")

                            // Data extraction
                            val isOnline = userObj.optBoolean("isOnline", false)
                            val followersCount = userObj.optInt("followersCount", 0)

                            runOnUiThread {
                                statusMessage.text = "" // Clear status on success

                                // 3. Display the data
                                followerNumTextView.text = followersCount.toString()
                                profileName.text = userObj.optString("username", "N/A")
                                profileUsername.text = userObj.optString("fullName", "N/A")
                                profileBio.text = userObj.optString("bio", "No bio provided.")

                                // 4. Set the Online/Offline status text and color
                                if (isOnline) {
                                    onlineStatus.text = "Online"
                                    onlineStatus.setTextColor(Color.parseColor("#4CAF50"))
                                } else {
                                    onlineStatus.text = "Offline"
                                    onlineStatus.setTextColor(Color.parseColor("#9E9E9E"))
                                }

                                val imageUrl = userObj.optString("profileImage", "")
                                Log.d("ProfileLoad", "Profile loaded for ID: $userId")
                            }
                        } else {
                            // Server JSON indicated failure (e.g., user not found)
                            val message = jsonResponse.optString("message", "User not found.")
                            runOnUiThread {
                                statusMessage.text = "Error loading user: $message"
                                followerNumTextView.text = "0"
                                Log.e("ProfileLoad", "Server reported error: $message")
                            }
                        }
                    } catch (jsonE: JSONException) {
                        // Catches JSON parsing errors (server output is corrupted)
                        runOnUiThread {
                            statusMessage.text = "Error: Invalid data format from server."
                            Log.e("ProfileLoad", "JSON Parsing Error. Raw Response: $responseString", jsonE)
                            Toast.makeText(this@TwentyFirstActivity, "Data corruption: Server sent invalid JSON.", Toast.LENGTH_LONG).show()
                        }
                    }

                } else {
                    // HTTP response code was not 200 (e.g., 404, 500)
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No detailed error message."
                    runOnUiThread {
                        statusMessage.text = "Error loading user: HTTP $responseCode"
                        followerNumTextView.text = "0"
                        Log.e("ProfileLoad", "HTTP Error: $responseCode. Server Output: $errorResponse")
                    }
                }
            } catch (e: UnknownHostException) {
                // Occurs if the IP is incorrect or the host name cannot be resolved
                runOnUiThread {
                    statusMessage.text = "Network Error: Unknown Host. (Check IP)"
                    Log.e("ProfileLoad", "Unknown Host Error: ${e.message}")
                    Toast.makeText(this@TwentyFirstActivity, "Unknown Host Error. Check IP address: $SERVER_URL", Toast.LENGTH_LONG).show()
                }
            } catch (e: SocketTimeoutException) {
                // Occurs if the connection times out (server is unreachable or too slow)
                runOnUiThread {
                    statusMessage.text = "Network Error: Connection Timed Out."
                    Log.e("ProfileLoad", "Timeout Error: ${e.message}")
                    Toast.makeText(this@TwentyFirstActivity, "Connection Timed Out. Server may be offline.", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                // General I/O errors (e.g., no internet connection, network cable disconnected)
                runOnUiThread {
                    statusMessage.text = "Network Error: General Connection Failure."
                    Log.e("ProfileLoad", "IO Exception: ${e.message}")
                    Toast.makeText(this@TwentyFirstActivity, "Connection Failure. Check Wi-Fi/Internet.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Catch-all for truly unexpected system errors
                runOnUiThread {
                    statusMessage.text = "Error loading user: Unexpected System Error."
                    Log.e("ProfileLoad", "Unexpected System Error: ${e.message}", e)
                }
            } finally {
                connection?.disconnect()
            }
        }
    }
}