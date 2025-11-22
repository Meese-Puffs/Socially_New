package com.Ahmad_Kamran.i230622

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val dbHelper = DatabaseHelper(appContext)
    private val networkUtils = NetworkUtils()

    // IMPORTANT: Use the correct base URL for your API endpoints
    private val API_BASE_URL = "http://192.168.18.51/socially_api/"

    override fun doWork(): Result {
        Log.i("SyncWorker", "Starting offline synchronization attempt.")

        // 1. Check connectivity first
        if (!networkUtils.isOnline(applicationContext)) {
            Log.d("SyncWorker", "No network connection. Retrying later.")
            // WorkManager will automatically retry this job based on its constraints
            return Result.retry()
        }

        val actions = dbHelper.getUnsyncedActions()
        if (actions.isEmpty()) {
            Log.i("SyncWorker", "Queue is empty. Sync complete.")
            return Result.success()
        }

        Log.d("SyncWorker", "Found ${actions.size} actions to sync.")
        var allSuccessful = true

        // 2. Iterate through and process each queued action
        actions.forEach { action ->
            val success = processQueuedAction(action)
            if (success) {
                // 3. Mark successful actions in SQLite
                dbHelper.markActionAsSynced(action.id)
                dbHelper.deleteSyncedAction(action.id) // Optionally delete immediately
            } else {
                // If one action fails (e.g., server error), stop processing the rest and retry the whole batch later
                allSuccessful = false
                Log.e("SyncWorker", "Sync failed for action ID ${action.id}. Will retry.")
                // Break out of the loop if you want to stop processing on first failure
                return@forEach // Continue to the next action, but Result.retry() will be returned at the end
            }
        }

        // 4. Return result based on overall success
        return if (allSuccessful) {
            Log.i("SyncWorker", "All queued actions synced successfully.")
            Result.success()
        } else {
            Log.w("SyncWorker", "Some actions failed to sync. Scheduling retry.")
            Result.retry() // Indicate failure, WorkManager will schedule a retry
        }
    }

    /**
     * Handles the HTTP communication for a single queued action.
     * Maps the action type to the correct API endpoint.
     */
    private fun processQueuedAction(action: QueuedAction): Boolean {
        // Determine the correct API path for the action
        val endpoint = when (action.type) {
            QueuedAction.TYPE_SEND_MESSAGE -> "send_message.php"
            QueuedAction.TYPE_CREATE_POST -> "create_post.php"
            QueuedAction.TYPE_LIKE_POST -> "like_post.php"
            else -> {
                Log.e("SyncWorker", "Unknown action type: ${action.type}. Deleting.")
                return true // Treat as successful/handled to prevent infinite retry loop
            }
        }

        var connection: HttpURLConnection? = null
        try {
            val url = URL(API_BASE_URL + endpoint)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            // 1. Send the JSON payload
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(action.payloadJson)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                // 2. Read server response (optional but good for error checking)
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)

                if (jsonResponse.optBoolean("success", false)) {
                    Log.d("SyncWorker", "Action ID ${action.id} synced successfully.")
                    return true
                } else {
                    // Server reported success=false (e.g., recipient not found)
                    Log.e("SyncWorker", "Server denied action ID ${action.id}: ${jsonResponse.optString("message", "Unknown error")}")
                    // If server actively denies, we consider it a permanent failure and don't retry.
                    return true
                }
            } else {
                // HTTP error (e.g., 404, 500) - needs retry
                Log.e("SyncWorker", "HTTP Error $responseCode for action ID ${action.id}")
                return false
            }
        } catch (e: UnknownHostException) {
            Log.e("SyncWorker", "Network unavailable (UnknownHost). Retrying.", e)
            return false
        } catch (e: Exception) {
            // General IO, JSON, or Timeout exception - needs retry
            Log.e("SyncWorker", "Error processing action ID ${action.id}: ${e.message}. Retrying.", e)
            return false
        } finally {
            connection?.disconnect()
        }
    }
}