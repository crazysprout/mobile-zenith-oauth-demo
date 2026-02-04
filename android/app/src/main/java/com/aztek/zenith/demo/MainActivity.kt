package com.aztek.zenith.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.aztek.zenith.*
import com.aztek.zenith.data.*

class MainActivity : ComponentActivity() {

    private lateinit var layoutSignIn: LinearLayout
    private lateinit var layoutProfile: LinearLayout
    private lateinit var layoutContinue: LinearLayout

    // Profile Views
    private lateinit var tvUserId: TextView
    private lateinit var tvUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ZenithApp.setup(this, "YOUR_API_KEY") { error ->
            if (error == null) {
                runOnUiThread { checkPreviousSignIn() }
            } else {
                Log.e("MainActivity", "Zenith Setup Failed", error)
            }
        }

        // Bind Views
        layoutSignIn = findViewById(R.id.layout_sign_in)
        layoutProfile = findViewById(R.id.layout_profile)
        layoutContinue = findViewById(R.id.layout_continue)

        tvUserId = findViewById(R.id.tv_user_id)
        tvUserName = findViewById(R.id.tv_user_name)

        val btnOAuthSignIn = findViewById<Button>(R.id.btn_oauth_sign_in)
        val btnGuestSignIn = findViewById<Button>(R.id.btn_guest_sign_in)
        val btnSignOut = findViewById<Button>(R.id.btn_sign_out)
        val btnContinue = findViewById<Button>(R.id.btn_continue)
        val btnGetProfile = findViewById<Button>(R.id.btn_get_profile)
        val btnIap = findViewById<Button>(R.id.btn_iap)

        // Set Listeners
        btnOAuthSignIn.setOnClickListener { handleSignIn(ZenithSignInType.OAUTH) }
        btnGuestSignIn.setOnClickListener { handleSignIn(ZenithSignInType.GUEST) }
        btnSignOut.setOnClickListener { handleSignOut() }
        btnContinue.setOnClickListener { handleContinue() }
        btnGetProfile.setOnClickListener { handleGetProfile() }
        btnIap.setOnClickListener { startActivity(Intent(this, IapActivity::class.java)) }

        // Initial State Check
        checkPreviousSignIn()

        // Handle redirect intent if app was opened via URL
        ZenithApp.handleOpenUrl(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Handle redirect intent when the activity is already running
        intent?.let { ZenithApp.handleOpenUrl(it) }
    }

    private fun checkPreviousSignIn() {
        if (ZenithApp.lastSignInType() != null) {
            showContinueState()
        } else {
            showSignInState()
        }
    }

    private fun handleSignIn(type: ZenithSignInType) {
        ZenithApp.signIn(
                type,
                this,
                success = { user -> runOnUiThread { showProfileState(user) } },
                cancel = { Log.d("MainActivity", "Sign In Cancelled") },
                failure = { error -> Log.e("MainActivity", "Sign In Failed", error) }
        )
    }

    private fun handleContinue() {
        ZenithApp.continueSignIn(
                success = { user -> runOnUiThread { showProfileState(user) } },
                failure = { error ->
                    Log.e("MainActivity", "Continue Sign In Failed", error)
                    handleSignOut()
                }
        )
    }

    private fun handleGetProfile() {
        ZenithApp.getProfile(
                success = { user ->
                    runOnUiThread {
                        val intent =
                                Intent(this@MainActivity, ProfileActivity::class.java).apply {
                                    putExtra("EXTRA_ID", user.id)
                                    putExtra("EXTRA_USERNAME", user.username)
                                    putExtra("EXTRA_EMAIL", user.email)
                                    putExtra("EXTRA_VERIFIED", user.isVerifiedProfile)
                                    putExtra("EXTRA_GUEST", user.isGuest)
                                }
                        startActivity(intent)
                    }
                },
                failure = { error -> Log.e("MainActivity", "Get Profile Failed", error) }
        )
    }

    private fun handleSignOut() {
        ZenithApp.signOut(
                success = { runOnUiThread { showSignInState() } },
                failure = {
                    runOnUiThread { showSignInState() } // Fallback
                }
        )
    }

    private fun showSignInState() {
        layoutSignIn.visibility = View.VISIBLE
        layoutProfile.visibility = View.GONE
        layoutContinue.visibility = View.GONE
    }

    private fun showProfileState(user: ZenithUserInfo? = null) {
        layoutSignIn.visibility = View.GONE
        layoutProfile.visibility = View.VISIBLE
        layoutContinue.visibility = View.GONE

        (user ?: ZenithApp.userInfo)?.let {
            tvUserId.text = getString(R.string.profile_id, it.id)
            tvUserName.text =
                    getString(
                            R.string.profile_username,
                            it.username ?: getString(R.string.profile_not_available)
                    )
        }
    }

    private fun showContinueState() {
        layoutSignIn.visibility = View.GONE
        layoutProfile.visibility = View.GONE
        layoutContinue.visibility = View.VISIBLE
    }
}
