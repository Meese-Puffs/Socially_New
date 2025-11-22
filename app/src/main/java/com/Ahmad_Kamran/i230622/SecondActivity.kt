package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.SocketTimeoutException
import java.util.concurrent.Executors

class SecondActivity : AppCompatActivity() {

    private val SERVER_URL = "http://192.168.18.51/socially_api/register_user.php"

    private lateinit var usernameBox: EditText
    private lateinit var nameBox: EditText
    private lateinit var lastNameBox: EditText
    private lateinit var dobBox: EditText
    private lateinit var emailBox: EditText
    private lateinit var passwordBox: EditText
    private lateinit var createAccountButton: TextView
    private lateinit var visibilityIcon: ImageView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components from XML
        usernameBox = findViewById(R.id.username_box)
        nameBox = findViewById(R.id.YourName_box)
        lastNameBox = findViewById(R.id.YourLastName_box)
        dobBox = findViewById(R.id.DateOfBirth_box)
        emailBox = findViewById(R.id.Email_box)
        passwordBox = findViewById(R.id.Password_box)
        createAccountButton = findViewById(R.id.create_account)
        visibilityIcon = findViewById(R.id.visibility)
        val backArrow = findViewById<ImageView>(R.id.arrow)

        // Set initial password visibility state (masked)
        passwordBox.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD


        createAccountButton.setOnClickListener {
            registerUser()
        }

        backArrow.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser() {
        // Retrieve input values without any checks
        val username = usernameBox.text.toString().trim()
        val firstName = nameBox.text.toString().trim()
        val lastName = lastNameBox.text.toString().trim()
        val dob = dobBox.text.toString().trim()
        val email = emailBox.text.toString().trim()
        val password = passwordBox.text.toString().trim()

        // NO CLIENT-SIDE VALIDATION CHECKS ARE PERFORMED HERE.

        // Disable button and show loading feedback
        createAccountButton.isEnabled = false
        createAccountButton.text = "Registering..."

        // 2. Prepare JSON Payload
        val jsonInput = JSONObject().apply {
            put("username", username)
            put("firstName", firstName)
            put("lastName", lastName)
            put("dateOfBirth", dob)
            put("email", email)
            put("password", password)
        }

        // 3. Execute Network Call on a Background Thread
        Executors.newSingleThreadExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(SERVER_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000 // 10 seconds to connect
                connection.readTimeout = 10000    // 10 seconds to read data

                // Write the JSON payload
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonInput.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response from server
                    val response = connection.inputStream.bufferedReader().use { it.readText() }

                    try {
                        val jsonResponse = JSONObject(response)
                        if (jsonResponse.getBoolean("success")) {
                            val responseMessage = jsonResponse.optString("message", "Registration successful!")
                            runOnUiThread {
                                Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                                val intent = Intent(this, ThirdActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val responseMessage = jsonResponse.optString("message", "Registration failed.")
                            runOnUiThread {
                                Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (jsonE: Exception) {
                        Log.e("Registration", "Error parsing server response: ${jsonE.message}. Response: $response", jsonE)
                        runOnUiThread {
                            Toast.makeText(this, "Received invalid data from server.", Toast.LENGTH_LONG).show()
                        }
                    }

                } else if (responseCode >= 400) {
                    // Handle specific client/server errors like 404, 500. Try to read error stream.
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided."
                    Log.e("Registration", "HTTP Error $responseCode. Server response: $errorResponse")
                    runOnUiThread {
                        Toast.makeText(this, "Server responded with error: HTTP $responseCode.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Registration failed with unexpected HTTP code: $responseCode", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("Registration", "Timeout Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this, "Connection timed out. Please check your network.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("Registration", "General Network/Registration Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this, "A general error occurred. Check server URL/connection.", Toast.LENGTH_LONG).show()
                }
            } finally {
                connection?.disconnect()
                // Re-enable button on failure/error
                runOnUiThread {
                    createAccountButton.isEnabled = true
                    createAccountButton.text = "Create an Account"
                }
            }
        }
    }

    // The validateInput function has been completely removed.
}