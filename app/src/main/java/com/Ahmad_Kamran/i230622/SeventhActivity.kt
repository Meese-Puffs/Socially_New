package com.Ahmad_Kamran.i230622

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * SeventhActivity handles user search functionality with debouncing.
 * It relies on external definitions for the 'User' data class and 'UserAdapter' class.
 */
class SeventhActivity : AppCompatActivity() {

    private lateinit var resultsRecyclerView: RecyclerView
    private var userAdapter: UserAdapter? = null
    private lateinit var searchEditText: EditText
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    // Change this to your XAMPP server IP address
    private val SERVER_URL = "http://192.168.18.51/socially_api/search_users.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.seventh_activity)

        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)

        // UserAdapter is used here, relying on its definition in a separate file.
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
            setHintTextColor(Color.GRAY)
            setTextColor(Color.BLACK)
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
            clearButton.alpha = 0.5f
            clearButton.postDelayed({
                clearButton.alpha = 1f
                searchEditText.text.clear()
                displaySearchResults(emptyList())
            }, 100)
        }

        // Auto-focus search bar on activity start
        searchEditText.requestFocus()
        searchEditText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    /**
     * Sets up TextWatcher for the search bar to implement debouncing.
     */
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

    /**
     * Executes the API call to search for users based on the query.
     */
    private fun searchUsers(query: String) {
        if (query.isEmpty()) {
            runOnUiThread { displaySearchResults(emptyList()) }
            return
        }

        Executors.newSingleThreadExecutor().execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(SERVER_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.doInput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val jsonBody = JSONObject()
                jsonBody.put("query", query)

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
                                // Constructing User object based on your external User class structure
                                User(
                                    id = userObj.getInt("id"),
                                    username = userObj.getString("username"),
                                    firstName = userObj.getString("firstName"),
                                    lastName = userObj.getString("lastName"),
                                    dateOfBirth = userObj.optString("dateOfBirth", ""),
                                    email = userObj.optString("email", ""),

                                    profileImage = userObj.optString("profileImage", null),
                                    bio = userObj.optString("bio", null),
                                    followersCount = userObj.optInt("followersCount", 0)
                                )
                            )
                        }
                        runOnUiThread { displaySearchResults(users) }
                    } else {
                        runOnUiThread {
                            Log.e("SearchUsers", "Search failed: ${jsonResponse.optString("message")}")
                            displaySearchResults(emptyList())
                            Toast.makeText(this@SeventhActivity, "Search failed: ${jsonResponse.optString("message", "No results.")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Log.e("SearchUsers", "HTTP Error: ${connection.responseCode}")
                        displaySearchResults(emptyList())
                        Toast.makeText(this@SeventhActivity, "Server Error: HTTP ${connection.responseCode}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Log.e("SearchUsers", "Error: ${e.message}")
                    displaySearchResults(emptyList())
                    Toast.makeText(this@SeventhActivity, "Network connection error.", Toast.LENGTH_LONG).show()
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    /**
     * Handles the click event when a user item is selected from the list.
     */
    private fun onUserClicked(user: User) {
        val intent = Intent(this, TwentyFirstActivity::class.java)
        // Pass the clicked user's ID to the profile screen (TwentyFirstActivity)
        intent.putExtra("USER_ID", user.id)
        startActivity(intent)
    }

    /**
     * Updates the RecyclerView with the new list of search results.
     */
    private fun displaySearchResults(users: List<User>) {
        userAdapter?.updateUsers(users)
    }
}