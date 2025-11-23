package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FourthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fourth_activity)

        val signUp = findViewById<TextView>(R.id.Sign_up)
        val logIn = findViewById<TextView>(R.id.log_in)
        val backButton = findViewById<Button>(R.id.backButton)

        val usernameInput = findViewById<EditText>(R.id.Username_box)
        val passwordInput = findViewById<EditText>(R.id.Password_box)

        signUp.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        // 🔹 LOGIN API CALL
        logIn.setOnClickListener {
            val emailOrUsername = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (emailOrUsername.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Make Retrofit login call
            RetrofitClient.api.loginUser(emailOrUsername, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        val body = response.body()
                        if (body != null && body.success) {
                            Toast.makeText(this@FourthActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@FourthActivity, HomeFeedActivity::class.java)
                            intent.putExtra("username", body.user?.username)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@FourthActivity, body?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@FourthActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
