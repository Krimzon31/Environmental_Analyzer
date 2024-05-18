package com.example.environmental_analyzer.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.environmental_analyzer.Entity.Geomagnetic
import com.example.environmental_analyzer.MAIN
import com.example.environmental_analyzer.MainDb
import com.example.environmental_analyzer.R
import com.example.environmental_analyzer.databinding.FragmentGeomagneticConditionsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Geomagnetic_ConditionsFragment : Fragment() {

    lateinit var binding : FragmentGeomagneticConditionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentGeomagneticConditionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNETConnection()
        Proverca()
        updateCurrentCard()

        binding.syncGeomagneticButton.setOnClickListener {
            val db = MainDb.getDb(MAIN)
            Thread {
                db.getDao().deleteGeomagnetic()
            }.start()
            lifecycleScope.launchWhenStarted {
                setData("penza")
            }
            updateCurrentCard()
            Toast.makeText(
                MAIN,
                "Данные обновлены",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Proverca(){
        val database = MainDb.getDb(MAIN)
        val rowCount = database.getDao().countTableRowsGeomagnetic()
        rowCount.observeForever { count ->
            if (count == 0) {
                lifecycleScope.launchWhenStarted {
                    setData("penza")
                }
                return@observeForever
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun setData(city: String) = withContext(Dispatchers.IO) {
        //try {
            val database = MainDb.getDb(MAIN)
            val url = "https://world-weather.ru/pogoda/russia/${city}/biometeorology/"
            val client = OkHttpClient()
            val currDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a"))
            val date = "${currDate}"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val document = Jsoup.parse(response.body?.string())

                val container = document.select("div[id=content]")

                val meteoIndex = container.select("div[class=biometric]")
                    .select("div[class=bio col-1]")
                    .select("li[class=bio-li-2]")
                    .first()
                    ?.text()

                val geomagnetic = container.select("div[class=biometric]")
                    .select("div[class=bio col-2]")
                    .select("li[class=bio-li-2]")
                    .first()
                    ?.text()

                val sunGeomagnetic = container.select("div[class=biometric]")
                    .select("div[class=bio col-3]")
                    .select("li[class=bio-li-2]")
                    .first()
                    ?.text()

                val cont = container.select("div[id=content-right]")
                    .select("div[id=weather-now-description]")
                    .select("dd")

                    val davl = cont[1].text()

                    val vlag = cont[2].text()

                    val veter = cont[3].text()

                    val porVeter = cont[4].text()

                    val obl = cont[5].text()

                    val imageGeo = ""

                    val geomagnet = Geomagnetic(
                        null,
                        city,
                        date,
                        meteoIndex!!,
                        geomagnetic!!,
                        sunGeomagnetic!!,
                        davl,
                        vlag,
                        veter,
                        porVeter,
                        obl,
                        imageGeo
                    )

                    database.getDao().insertGeomagnetic(geomagnet)
            }
        //}
        /*catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
        }*/
    }

    private fun checkNETConnection(){
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected ?: false
        if(isConnected){
            Toast.makeText(activity, "Приложение подключено к интернету", Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(activity, "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCurrentCard() = with(binding){

        val database = MainDb.getDb(MAIN)
        database.getDao().getGeomagnetic().asLiveData().observe(MAIN) { list ->

            list.forEach { Glist ->

                tvGeoCity.text = Glist.city
                tvGeomagneticDate.text = Glist.date
                tvMeteoIndex.text = Glist.meteoIndex
                tvGeomagnetic.text = Glist.geomagnetic
                tvSunGeomagnetic.text = Glist.sunGeomagnetic

                tvDavl.text = Glist.davl
                tvVlag.text = Glist.vlag
                tvVeter.text = Glist.veter
                tvPorVeter.text = Glist.porVeter
                tvObl.text = Glist.obl

                when(Glist.meteoIndex){
                    "1" -> imgMeteoIndex.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVGreenIndicate))
                    "2" -> imgMeteoIndex.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVYellowIndicate))
                    "3"-> imgMeteoIndex.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVOrangeIndicate))
                    "4"-> imgMeteoIndex.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVRedIndicate))
                    "5" -> imgMeteoIndex.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVPerpleIndicate))
                }

                when(Glist.sunGeomagnetic){
                    "1" -> imgSun.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVGreenIndicate))
                    "2" -> imgSun.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVYellowIndicate))
                    "3"-> imgSun.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVOrangeIndicate))
                    "4"-> imgSun.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVRedIndicate))
                    "5" -> imgSun.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVPerpleIndicate))
                }

                when(Glist.geomagnetic){
                    "1" -> imgGeomagnetic.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVGreenIndicate))
                    "2" -> imgGeomagnetic.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVYellowIndicate))
                    "3"-> imgGeomagnetic.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVOrangeIndicate))
                    "4"-> imgGeomagnetic.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVRedIndicate))
                    "5" -> imgGeomagnetic.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVPerpleIndicate))
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Geomagnetic_ConditionsFragment()
    }
}