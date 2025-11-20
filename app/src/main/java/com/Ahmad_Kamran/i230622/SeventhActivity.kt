package com.Ahmad_Kamran.i230622

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class SeventhActivity : AppCompatActivity() {

    private var selectedTab = "Top" // Tracks the currently selected filter tab
    private lateinit var resultsRecyclerView: RecyclerView
    private var userAdapter: UserAdapter? = null
    private lateinit var searchEditText: EditText
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    // Change this to your XAMPP server IP address
    private val SERVER_URL = "http://192.168.18.51/socially_api/search_users.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure you have R.id.tabTop, R.id.tabAccounts, R.id.tabMedia in your layout
        setContentView(R.layout.seventh_activity)

        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with the click listener logic
        userAdapter = UserAdapter(emptyList()) { user ->
            onUserClicked(user)
        }
        resultsRecyclerView.adapter = userAdapter

        val homeButton = findViewById<Button>(R.id.homebutton)
        val searchButton = findViewById<Button>(R.id.searchbutton)
        val clearButton = findViewById<TextView>(R.id.Clear)
        val searchBarContainer = findViewById<FrameLayout>(R.id.searchBarContainer)

        // Create and add EditText to search bar
        searchEditText = EditText(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                leftMargin = (40 * resources.displayMetrics.density).toInt()
                rightMargin = (40 * resources.displayMetrics.density).toInt()
            }
            background = null
            hint = "Search"
            textSize = 16f
            // Assuming R.color.dark_grey and R.color.black are defined
            setHintTextColor(ContextCompat.getColor(this@SeventhActivity, R.color.dark_grey))
            setTextColor(ContextCompat.getColor(this@SeventhActivity, R.color.black))
            setPadding(0, 0, 0, 0)
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
        }
        searchBarContainer.addView(searchEditText)

        // --- Event Listeners ---


        // Show keyboard when EditText is clicked
        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        // Setup search functionality (debouncing)
        setupSearchFunctionality()

        // Navigation buttons
        homeButton.setOnClickListener {
            startActivity(Intent(this, FifthActivity::class.java))
            finish()
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SixthActivity::class.java))
            finish()
        }

        // Clear button functionality
        clearButton.setOnClickListener {
            // Simple visual feedback for the button press
            clearButton.alpha = 0.5f
            clearButton.postDelayed({
                clearButton.alpha = 1f
                searchEditText.text.clear()
                // Do not hide keyboard after clearing text unless necessary
            }, 100)
        }

        // Auto-focus search bar on activity start
        searchEditText.requestFocus()
        searchEditText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    private fun setupSearchFunctionality() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove previous pending search request (debouncing)
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                val query = s.toString().trim()
                searchRunnable = Runnable { searchUsers(query) }
                // Wait 500ms after the last character typed before executing search
                searchHandler.postDelayed(searchRunnable!!, 500)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchUsers(query: String) {
        if (query.isEmpty()) {
            runOnUiThread { displaySearchResults(emptyList()) }
            return
        }

        Executors.newSingleThreadExecutor().execute {
            try {
                val url = URL(SERVER_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.doInput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val jsonBody = JSONObject()
                jsonBody.put("query", query)
                // Sending the selected filter to the backend
                jsonBody.put("filter", selectedTab)

                OutputStreamWriter(connection.outputStream).use {
                    it.write(jsonBody.toString())
                    it.flush()
                }

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
                        val usersArray = jsonResponse.getJSONArray("users")
                        val users = mutableListOf<User>()
                        for (i in 0 until usersArray.length()) {
                            val userObj = usersArray.getJSONObject(i)
                            users.add(
                                User(
                                    id = userObj.getInt("id"),
                                    username = userObj.getString("username"),
                                    fullName = userObj.optString("fullName", ""),
                                    profileImage = userObj.optString("profileImage", ""),
                                    bio = userObj.optString("bio", ""),
                                    followersCount = userObj.optInt("followersCount", 0)
                                )
                            )
                        }
                        runOnUiThread { displaySearchResults(users) }
                    } else {
                        runOnUiThread {
                            Log.e("SearchUsers", "Search failed: ${jsonResponse.optString("message")}")
                            displaySearchResults(emptyList())
                        }
                    }
                } else {
                    runOnUiThread {
                        Log.e("SearchUsers", "HTTP Error: ${connection.responseCode}")
                        displaySearchResults(emptyList())
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("SearchUsers", "Error: ${e.message}")
                    displaySearchResults(emptyList())
                }
            }
        }
    }

    // Function added to handle user clicks and launch TwentyFirstActivity
    private fun onUserClicked(user: User) {
        val intent = Intent(this, TwentyFirstActivity::class.java)
        // Pass the clicked user's ID to the profile screen (TwentyFirstActivity)
        intent.putExtra("USER_ID", user.id)
        startActivity(intent)
    }

    private fun displaySearchResults(users: List<User>) {
        // Use the adapter's update method for efficient list refreshing
        userAdapter?.updateUsers(users)
        // You might want to show a 'No results' message here if users.isEmpty()
    }
}