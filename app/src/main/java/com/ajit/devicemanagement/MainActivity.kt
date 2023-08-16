package com.ajit.devicemanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                    true
                }
                R.id.addNewDeviceFragment -> {
                    navController.navigate(R.id.addNewDeviceFragment)
                    true
                }
                R.id.deviceListingFragment -> {
                    navController.navigate(R.id.deviceListingFragment)
                    true
                }
                R.id.userProfileFragment -> {
                    navController.navigate(R.id.userProfileFragment)
                    true
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if ( destination.id == R.id.deviceListingFragment ||  destination.id == R.id.userProfileFragment  || destination.id == R.id.dashboardFragment || destination.id == R.id.userProfileFragment) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }


    }



}