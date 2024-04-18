package com.example.environmental_analyzer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.environmental_analyzer.databinding.ActivityMainBinding
import com.example.environmental_analyzer.fragments.WeatherFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, WeatherFragment.newInstance())
            .commit()

    }
}