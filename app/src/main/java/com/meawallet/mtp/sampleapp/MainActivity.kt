package com.meawallet.mtp.sampleapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meawallet.mtp.sampleapp.databinding.ActivityMainBinding
import com.meawallet.mtp.sampleapp.di.appContainer
import com.meawallet.mtp.sampleapp.helpers.PushServiceInstanceManager


import com.meawallet.mtp.sampleapp.listeners.PushServiceInstanceIdGetListener
import com.meawallet.mtp.sampleapp.platform.RegistrationRetrier


class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName

    }

    private lateinit var binding: ActivityMainBinding

    private val tokenPlatform by lazy { appContainer.tokenPlatform }
    private val initializationHelper by lazy { appContainer.initializationHelper }

    private val navController by  lazy {  findNavController(R.id.nav_host_fragment_activity_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val addCardFab: FloatingActionButton = binding.addCard
        Navigation.setViewNavController(addCardFab, navController)
        addCardFab.setOnClickListener {
            navController.navigate(R.id.navigation_add_card)
        }

        registerWallet()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.open_payment_activity -> {
                navController.navigate(R.id.navigation_payment_activity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun registerWallet() {
        Log.d(TAG, "registerWallet()")

        if (tokenPlatform.isRegistered()) {
            Log.d(TAG, "Wallet already registered.")
            return
        }

        PushServiceInstanceManager.getIdToken(this, object : PushServiceInstanceIdGetListener {
            override fun onSuccess(token: String) {
                Log.d(TAG, "Push Service Instance ID token: $token")

                proceedWithRegister(token)
            }
            override fun onFailure(exception: Exception) {
                Log.d(TAG, "Failed to get Push Service Token", exception)

            }
        })
    }

    private fun proceedWithRegister(token: String) {
        val registrationRetrier = RegistrationRetrier(tokenPlatform)

        registrationRetrier.register(
            application,
            token,
            "en",
            3,
            {
                Log.d(TAG,
                    "Mea Token Platform library successfully registered. $token"
                )
            },
            { error ->
                Log.e(TAG,
                    "Mea Token Platform library registration failed: ${error.code} " + error.message
                )
            },
            {
                initializationHelper.postInitializeSetup()
            }
        )
    }
}