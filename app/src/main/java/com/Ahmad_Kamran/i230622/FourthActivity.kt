package com.Ahmad_Kamran.i230622

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors

class FourthActivity : AppCompatActivity() {

    // IMPORTANT: Define your endpoints
    private val LOGIN_URL = "http://192.168.18.51/socially_api/login_user.php"
    private val STATUS_UPDATE_URL = "http://192.168.18.51/socially_api/update_status.php" // NEW ENDPOINT
    private val PREFS_NAME = "SociallyPrefs"

    // UI Components for Login
    private lateinit var loginIdentifierBox: EditText
    private lateinit var loginPasswordBox: EditText
    private lateinit var loginButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fourth_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fourth_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Initialize UI components based on the XML IDs ---
        loginIdentifierBox = findViewById(R.id.Username_box)
        loginPasswordBox = findViewById(R.id.Password_box)
        loginButton = findViewById(R.id.log_in)

        val signUp = findViewById<TextView>(R.id.Sign_up)
        val backButton : Button = findViewById(R.id.backButton)

        loginPasswordBox.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // --- Event Listeners ---
        loginButton.setOnClickListener {
            loginUser()
        }

        signUp.setOnClickListener{
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener{
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Handles authentication and session management.
     */
    private fun loginUser() {
        val identifier = loginIdentifierBox.text.toString().trim()
        val password = loginPasswordBox.text.toString().trim()

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your username/email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        loginButton.isEnabled = false
        loginButton.text = "Logging In..."

        val jsonInput = JSONObject().apply {
            put("identifier", identifier)
            put("password", password)
        }

        Executors.newSingleThreadExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(LOGIN_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonInput.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()

                    try {
                        val jsonResponse = JSONObject(response)
                        if (jsonResponse.getBoolean("success")) {
                            val userId = jsonResponse.optInt("user_id", -1)
                            val message = jsonResponse.optString("message", "Login successful!")

                            if (userId != -1) {
                                // 1. Store the user ID in SharedPreferences
                                val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                prefs.edit().putInt("user_id", userId).apply()
                                Log.d("Login", "Stored User ID: $userId")

                                // 2. --- NEW: Update User Status to ONLINE ---
                                updateUserStatus(userId, true)

                                // 3. Navigate to the main activity (FifthActivity)
                                runOnUiThread {
                                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, FifthActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this, "Login successful, but missing user data.", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            val message = jsonResponse.optString("message", "Login failed. Please check credentials.")
                            runOnUiThread {
                                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (jsonE: Exception) {
                        Log.e("Login", "Error parsing server response: ${jsonE.message}. Response: $response", jsonE)
                        runOnUiThread {
                            Toast.makeText(this, "Received invalid data from server.", Toast.LENGTH_LONG).show()
                        }
                    }

                } else if (responseCode >= 400) {
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided."
                    Log.e("Login", "HTTP Error $responseCode. Server response: $errorResponse")
                    runOnUiThread {
                        Toast.makeText(this, "Server responded with error: HTTP $responseCode.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("Login", "Timeout Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this, "Connection timed out. Please check your network.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("Login", "General Network/Login Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this, "A general error occurred. Check server URL/connection.", Toast.LENGTH_LONG).show()
                }
            } finally {
                connection?.disconnect()
                runOnUiThread {
                    loginButton.isEnabled = true
                    loginButton.text = "Log in"
                }
            }
        }
    }

    /**
     * Sends a separate network request to update the user's online status in the database.
     */
    private fun updateUserStatus(userId: Int, isOnline: Boolean) {
        Executors.newSingleThreadExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(STATUS_UPDATE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonInput = JSONObject().apply {
                    put("user_id", userId)
                    put("isOnline", if (isOnline) 1 else 0) // 1 for online, 0 for offline
                }

                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonInput.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Status update successful (We don't need to read the full response here, just check the code)
                    Log.d("StatusUpdate", "User $userId status set to ONLINE.")
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided."
                    Log.e("StatusUpdate", "Failed to set status. HTTP Error $responseCode. Server response: $errorResponse")
                }
            } catch (e: Exception) {
                Log.e("StatusUpdate", "Error updating user status: ${e.message}", e)
            } finally {
                connection?.disconnect()
            }
        }
    }
}