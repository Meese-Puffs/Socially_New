package com.Ahmad_Kamran.i230622

import android.content.Context
import android.content.Intent
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

    private var selectedTab = "Top"
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
        userAdapter = UserAdapter(emptyList())
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
            setHintTextColor(ContextCompat.getColor(this@SeventhActivity, R.color.dark_grey))
            setTextColor(ContextCompat.getColor(this@SeventhActivity, R.color.black))
            setPadding(0, 0, 0, 0)
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
        }
        searchBarContainer.addView(searchEditText)

        // Show keyboard when EditText is clicked
        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        // Setup search functionality
        setupSearchFunctionality()


        // Navigation buttons
        homeButton.setOnClickListener {
            startActivity(Intent(this, FifthActivity::class.java))
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SixthActivity::class.java))
        }

        // Clear button functionality
        clearButton.setOnClickListener {
            clearButton.alpha = 0.5f
            clearButton.postDelayed({
                clearButton.alpha = 1f
                searchEditText.text.clear()
                searchEditText.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                showDefaultResults()
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
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    showDefaultResults()
                    return
                }
                searchRunnable = Runnable { searchUsers(query) }
                searchHandler.postDelayed(searchRunnable!!, 500)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchUsers(query: String) {
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
                        }
                    }
                } else {
                    runOnUiThread { Log.e("SearchUsers", "HTTP Error: ${connection.responseCode}") }
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { Log.e("SearchUsers", "Error: ${e.message}") }
            }
        }
    }

    private fun displaySearchResults(users: List<User>) {
        userAdapter = UserAdapter(users)
        resultsRecyclerView.adapter = userAdapter
    }

    private fun showDefaultResults() {
        Log.d("SearchResults", "Showing default results")
    }

    private fun switchTab(
        tab: String,
        tabTop: TextView,
        tabAccounts: TextView,
        tabTags: TextView,
        tabPlaces: TextView,
        lineIndicator: View
    ) {
        selectedTab = tab

        val tabs = listOf(tabTop, tabAccounts, tabTags, tabPlaces)
        tabs.forEach {
            it.setTextColor(ContextCompat.getColor(this, R.color.dark_grey))
            it.setTypeface(null, android.graphics.Typeface.NORMAL)
        }

        when (tab) {
            "Top" -> {
                tabTop.setTextColor(ContextCompat.getColor(this, R.color.black))
                tabTop.setTypeface(null, android.graphics.Typeface.BOLD)
                animateLineIndicator(lineIndicator, 0f, 80)
            }
            "Accounts" -> {
                tabAccounts.setTextColor(ContextCompat.getColor(this, R.color.black))
                tabAccounts.setTypeface(null, android.graphics.Typeface.BOLD)
                animateLineIndicator(lineIndicator, 85f, 110)
                if (searchEditText.text.isNotEmpty()) searchUsers(searchEditText.text.toString())
            }
            "Tags" -> {
                tabTags.setTextColor(ContextCompat.getColor(this, R.color.black))
                tabTags.setTypeface(null, android.graphics.Typeface.BOLD)
                animateLineIndicator(lineIndicator, 195f, 60)
            }
            "Places" -> {
                tabPlaces.setTextColor(ContextCompat.getColor(this, R.color.black))
                tabPlaces.setTypeface(null, android.graphics.Typeface.BOLD)
                animateLineIndicator(lineIndicator, 270f, 75)
            }
        }
    }

    private fun animateLineIndicator(lineIndicator: View, startMargin: Float, width: Int) {
        val params = lineIndicator.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        lineIndicator.animate()
            .translationX(startMargin * resources.displayMetrics.density)
            .setDuration(200)
            .start()
        params.width = (width * resources.displayMetrics.density).toInt()
        lineIndicator.layoutParams = params
    }

    data class User(
        val id: Int,
        val username: String,
        val fullName: String,
        val profileImage: String,
        val bio: String,
        val followersCount: Int
    )
}
