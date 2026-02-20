package com.aztek.zenith.demo.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.aztek.zenith.ZenithApp
import com.aztek.zenith.demo.R
import com.aztek.zenith.demo.utils.LoadingHelper

class ProfileActivity : ComponentActivity() {
    private var isDeletingAccount = false
    private lateinit var btnDeleteAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvDetails = findViewById<TextView>(R.id.tv_profile_details)
        val btnClose = findViewById<ImageButton>(R.id.btn_close)
        btnDeleteAccount = findViewById<Button>(R.id.btn_delete_account)

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
            if (isDeletingAccount) return@setOnClickListener
            isDeletingAccount = true
            btnDeleteAccount.isEnabled = false
            LoadingHelper.showLoading(this, "Deleting Account...")

            var isHandled = false
            val handler = android.os.Handler(android.os.Looper.getMainLooper())
            val timeoutRunnable = Runnable {
                if (!isHandled) {
                    isHandled = true
                    isDeletingAccount = false
                    LoadingHelper.hideLoading(this@ProfileActivity)
                    btnDeleteAccount.isEnabled = true
                    Toast.makeText(
                        this@ProfileActivity,
                        "Request timed out, please try again",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            handler.postDelayed(timeoutRunnable, 15000)

            ZenithApp.deleteAccount(this) { exception ->
                if (isHandled) return@deleteAccount
                isHandled = true
                handler.removeCallbacks(timeoutRunnable)

                runOnUiThread {
                    isDeletingAccount = false
                    LoadingHelper.hideLoading(this@ProfileActivity)
                    btnDeleteAccount.isEnabled = true
                    if (exception != null) {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Failed to delete account: ${exception.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Account deleted", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If the user closed the CustomTab/WebView and returned here, reset UI
        if (isDeletingAccount) {
            isDeletingAccount = false
            LoadingHelper.hideLoading(this)
            btnDeleteAccount.isEnabled = true
        }
    }
}
