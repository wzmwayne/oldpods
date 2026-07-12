package com.oldpods.app.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.oldpods.app.R
import com.oldpods.app.main.MainActivity
import com.oldpods.app.common.PrefsManager
import com.oldpods.app.repository.AuthRepository
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            tryAutoLogin()
        }, 2000)
    }

    private fun tryAutoLogin() {
        if (PrefsManager.isLoggedIn(this)) {
            val email = PrefsManager.getUserEmail(this)
            val uuid = PrefsManager.getUuid(this)
            if (email.isNotEmpty() && uuid.isNotEmpty()) {
                scope.launch {
                    try {
                        val repo = AuthRepository()
                        repo.silentLogin(email, uuid)
                    } catch (e: Exception) {
                        // Silent login failed, proceed anyway
                    }
                    navigateToMain()
                }
                return
            }
        }
        navigateToMain()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
