package com.example.environmental_analyzer.fragments

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.environmental_analyzer.Entity.Weather
import com.example.environmental_analyzer.MAIN
import com.example.environmental_analyzer.MainActivity
import com.example.environmental_analyzer.MainDb
import com.example.environmental_analyzer.MainViewModel
import com.example.environmental_analyzer.adapters.VpAdapter
import com.example.environmental_analyzer.adapters.WeatherModel
import com.example.environmental_analyzer.databinding.FragmentWeatherBinding
import com.example.environmental_analyzer.recycleFragments.DaysFragment
import com.example.environmental_analyzer.recycleFragments.HoursFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

const val API_KEY = "86e184ef77824ab987d90855242703"

class WeatherFragment : Fragment() {

    private val fList = listOf(
        DaysFragment.newInstance(),
        HoursFragment.newInstance()
    )

    private val tList = listOf(
        "Дни",
        "Часы"
    )

    private  lateinit var plauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentWeatherBinding
    private val model : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        MAIN = requireContext() as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        checkNETConnection()
        init()
        requestWeatherData("Penza")
        updateCurrentCard()

        binding.syncButton.setOnClickListener {
            val db = MainDb.getDb(MAIN)
            Thread {
                db.getDao().deleteAllWeather()
            }.start()
            requestWeatherData("Penza")
        }
    }

    private fun Proverca(mainObject: JSONObject, weatherItem : WeatherModel){
        val database = MainDb.getDb(MAIN)
        val rowCount = database.getDao().countTableRowsWeather()
        rowCount.observeForever { count ->
            if (count == 0) {
                lifecycleScope.launchWhenStarted {
                    setData(mainObject, weatherItem)
                }
                return@observeForever
            }
        }
    }

    private fun init() = with(binding){
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vpList.adapter = adapter
        TabLayoutMediator(tlPer, vpList){
            tab, position -> tab.text = tList[position]
        }.attach()
    }

    private fun updateCurrentCard() = with(binding){

        val database = MainDb.getDb(MAIN)
        database.getDao().getWeather().asLiveData().observe(MAIN) { list ->

            list.forEach { weather ->
                val maxMinTemp = "${weather.currentMaxTemp}°C/${weather.currentMinTemp}°C"
                tvDate.text = weather.date
                tvCurrentTemp.text = "${weather.currentTemp}°C"
                tvLocation.text = weather.city
                tvStatus.text = weather.condition
                Picasso.get().load("https:" + weather.imageUrl).into(imgWether)
                tvMinMaxTemp.text = maxMinTemp
            }
        }
    }

    private fun permissionListener(){
        plauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(){
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            plauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkNETConnection(){
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected ?: false
        if(isConnected){
            Toast.makeText(activity, "Данные обновленны", Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(activity, "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestWeatherData(city: String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "7" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val requst = StringRequest(
            Request.Method.GET,
            url,
            {
                result -> parseWeatherData(result)
            },
            {
                error -> Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(requst)
    }

    private fun parseWeatherData(result: String){
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        lifecycleScope.launchWhenStarted {
            Proverca(mainObject, list[0])
            parseCurrentData(mainObject, list[0])
        }
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem : WeatherModel) {
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition")
                .getString("text"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition")
                .getString("icon"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.currentMaxTemp,
            weatherItem.currentMinTemp,
            weatherItem.hours
        )
        model.liveDataCurrent.value = item
    }

    private suspend fun setData(mainObject: JSONObject, weatherItem : WeatherModel) = withContext(Dispatchers.IO) {
        try {
            val database = MainDb.getDb(MAIN)

            val item = Weather(
                null,
                mainObject.getJSONObject("location").getString("name"),
                mainObject.getJSONObject("current").getString("last_updated"),
                mainObject.getJSONObject("current")
                    .getJSONObject("condition")
                    .getString("text"),
                mainObject.getJSONObject("current")
                    .getJSONObject("condition")
                    .getString("icon"),
                mainObject.getJSONObject("current").getString("temp_c"),
                weatherItem.currentMaxTemp,
                weatherItem.currentMinTemp,
            )

            database.getDao().insertWeather(item)

                }
                catch (e: Exception) {
                Toast.makeText(
                    MAIN,
                    "${e}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private fun parseDays(mainObject: JSONObject) : List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val city = mainObject.getJSONObject("location").getString("name")
        for(i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                city,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }
    companion object {
        fun newInstance() = WeatherFragment()
    }
}