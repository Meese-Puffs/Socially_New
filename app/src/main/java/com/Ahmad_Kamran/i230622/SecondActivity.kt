package com.Ahmad_Kamran.i230622

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back = findViewById<ImageView>(R.id.arrow)
        val createAccountButton = findViewById<TextView>(R.id.create_account)

        val usernameInput = findViewById<EditText>(R.id.username_box)
        val firstNameInput = findViewById<EditText>(R.id.YourName_box)
        val lastNameInput = findViewById<EditText>(R.id.YourLastName_box)
        val dobInput = findViewById<EditText>(R.id.DateOfBirth_box)
        val emailInput = findViewById<EditText>(R.id.Email_box)
        val passwordInput = findViewById<EditText>(R.id.Password_box)

        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        createAccountButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.api.registerUser(username, email, password)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        val body = response.body()
                        if (body != null && body.success) {
                            Toast.makeText(this@SecondActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SecondActivity, FourthActivity::class.java))
                        } else {
                            Toast.makeText(this@SecondActivity, body?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@SecondActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
