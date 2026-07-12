package com.oldpods.app.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.oldpods.app.R
import com.oldpods.app.common.AppNavigator
import com.oldpods.app.ui.notifications.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), NotificationsFragment.LoginCallback, AppNavigator {

    private lateinit var navController: androidx.navigation.NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)
    }

    override fun navigateToLogin() {
        navController.navigate(R.id.navigation_login)
    }

    override fun navigateToDetail(appId: String) {
        val bundle = Bundle().apply { putString("app_id", appId) }
        navController.navigate(R.id.navigation_detail, bundle)
    }

    override fun navigateToSearch(query: String) {
        val bundle = Bundle().apply { putString("search_query", query) }
        navController.navigate(R.id.navigation_search, bundle)
    }
}
