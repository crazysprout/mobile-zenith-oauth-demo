package com.aztek.zenith.demo

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.aztek.zenith.ZenithApp

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvDetails = findViewById<TextView>(R.id.tv_profile_details)
        val btnClose = findViewById<ImageButton>(R.id.btn_close)
        val btnDeleteAccount = findViewById<Button>(R.id.btn_delete_account)

        // Retrieve data passed from MainActivity
        // Assuming we pass fields individually to avoid Parcelable issues if unknown
        val id = intent.getStringExtra("EXTRA_ID") ?: "-"
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "-"
        val email = intent.getStringExtra("EXTRA_EMAIL") ?: "-"
        val isVerified = intent.getBooleanExtra("EXTRA_VERIFIED", false)
        val isGuest = intent.getBooleanExtra("EXTRA_GUEST", false)

        val displayText =
                """
            ID: $id
            Username: $username
            Email: $email
            Verified: $isVerified
            Guest: $isGuest
        """.trimIndent()

        tvDetails.text = displayText

        btnClose.setOnClickListener { finish() }

        btnDeleteAccount.setOnClickListener {
            ZenithApp.deleteAccount(this) { exception ->
                if (exception != null) {
                    Toast.makeText(
                                    this,
                                    "Failed to delete account: ${exception.message}",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                } else {
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
