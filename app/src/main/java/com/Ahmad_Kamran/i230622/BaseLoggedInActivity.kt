package com.Ahmad_Kamran.i230622

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * Base activity for all screens that require an active user session.
 * This activity handles setting the user's status to ONLINE when the activity is foregrounded (onResume)
 * and OFFLINE when the activity is backgrounded or closed (onStop).
 */
open class BaseLoggedInActivity : AppCompatActivity() {

    // Endpoints and Constants
    private val STATUS_UPDATE_URL = "http://192.168.18.51/socially_api/update_status.php"
    private val PREFS_NAME = "SociallyPrefs"
    protected var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the stored user ID once
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = prefs.getInt("user_id", -1)

        if (currentUserId == -1) {
            Log.w("BaseActivity", "No user ID found in session. Child activity must handle redirection.")
        }
    }

    /**
     * Called when the activity is about to become visible (after onCreate or when returning from background).
     * This is the perfect time to set the user's status to ONLINE.
     */
    override fun onResume() {
        super.onResume()
        if (currentUserId != -1) {
            // User is valid, set them to ONLINE status (isOnline = true)
            updateUserStatus(currentUserId, true)
        }
    }

    /**
     * Called when the activity is no longer visible to the user (e.g., app is minimized or closed).
     * This is the perfect time to set the user's status to OFFLINE.
     */
    override fun onStop() {
        super.onStop()
        if (currentUserId != -1) {
            // User is valid and session exists, set them to OFFLINE status (isOnline = false)
            updateUserStatus(currentUserId, false)
        }
    }

    /**
     * Sends a separate network request to update the user's online status in the database.
     */
    protected fun updateUserStatus(userId: Int, isOnline: Boolean) {
        // Run the update on a background thread
        Executors.newSingleThreadExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(STATUS_UPDATE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val jsonInput = JSONObject().apply {
                    put("user_id", userId)
                    put("isOnline", if (isOnline) 1 else 0) // 1 for online, 0 for offline
                }

                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonInput.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode
                val statusText = if (isOnline) "ONLINE" else "OFFLINE"

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("StatusUpdate", "User $userId status set to $statusText successfully.")
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided."
                    Log.e("StatusUpdate", "Failed to set status to $statusText. HTTP Error $responseCode. Response: $errorResponse")
                }
            } catch (e: Exception) {
                Log.e("StatusUpdate", "Error updating user status: ${e.message}", e)
            } finally {
                connection?.disconnect()
            }
        }
    }
}