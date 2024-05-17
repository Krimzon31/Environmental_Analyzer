package com.example.environmental_analyzer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.environmental_analyzer.databinding.ActivityMainBinding
import com.example.environmental_analyzer.fragments.AirPollutionFragment
import com.example.environmental_analyzer.fragments.AllergyFragment
import com.example.environmental_analyzer.fragments.Geomagnetic_ConditionsFragment
import com.example.environmental_analyzer.fragments.UV_radiationFragment
import com.example.environmental_analyzer.fragments.WeatherFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(WeatherFragment())

        val  selectedItemId = R.id.weatherFragment
        binding.bottomNavView.setSelectedItemId(selectedItemId)

        binding.bottomNavView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.weatherFragment -> replaceFragment(WeatherFragment())
                R.id.uv_radiationFragment -> replaceFragment(UV_radiationFragment())
                R.id.airPollutionFragment -> replaceFragment(AirPollutionFragment())
                R.id.allergy -> replaceFragment(AllergyFragment())
                R.id.geomagnetic_conditions -> replaceFragment(Geomagnetic_ConditionsFragment())

                else ->{

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentMeneger = supportFragmentManager
        val fragmentTransaction = fragmentMeneger.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}