package za.edu.opsc6311.bookstack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("login_details", Context.MODE_PRIVATE)

        // Find views
        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        // Load saved login details, if any
        val savedUsername = sharedPreferences.getString("username", "")
        val savedPassword = sharedPreferences.getString("password", "")
        editTextUsername.setText(savedUsername)
        editTextPassword.setText(savedPassword)

        // Login button click listener
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Check if the provided username and password match the stored credentials
            if (isLoginValid(username, password)) {
                // Show a toast message indicating successful login
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                // Show a toast message indicating invalid credentials
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Register button click listener
        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Check if the username and password fields are not empty
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Check if the username is available
                if (isUsernameAvailable(username)) {
                    // Show a toast message indicating successful registration
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                    // Save registration details
                    saveRegistrationDetails(username, password)

                } else {
                    // Show a toast message indicating the username is not available
                    Toast.makeText(this, "Username is not available", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show a toast message indicating username or password is empty
                Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRegistrationDetails(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }

    private fun isLoginValid(username: String, password: String): Boolean {
        // Retrieve the stored username and password from SharedPreferences
        val storedUsername = sharedPreferences.getString("username", "")
        val storedPassword = sharedPreferences.getString("password", "")

        // Check if the provided username and password match the stored credentials
        return username == storedUsername && password == storedPassword
    }

    private fun isUsernameAvailable(username: String): Boolean {
        // Retrieve the set of registered usernames from SharedPreferences
        val registeredUsernames = sharedPreferences.getStringSet("registered_usernames", mutableSetOf()) ?: mutableSetOf()

        // Check if the username exists in the set
        return !registeredUsernames.contains(username)
    }
}
