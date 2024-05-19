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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.environmental_analyzer.DialogManager
import com.example.environmental_analyzer.Entity.AirPollution
import com.example.environmental_analyzer.MAIN
import com.example.environmental_analyzer.MainDb
import com.example.environmental_analyzer.Models.AirPollutionModel
import com.example.environmental_analyzer.R
import com.example.environmental_analyzer.adapters.AirPollutionAdapter
import com.example.environmental_analyzer.databinding.FragmentAirPollutionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.time.LocalDateTime

class AirPollutionFragment : Fragment() {

    lateinit var binding: FragmentAirPollutionBinding
    private lateinit var adapter: AirPollutionAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAirPollutionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNETConnection()
        Proverca()
        updateCurrentCard()
        initRecycle("penza")

        binding.syncButtonAir.setOnClickListener {
            val db = MainDb.getDb(MAIN)
            Thread {
                db.getDao().deleteAirPollution()
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

        binding.recAirButton.setOnClickListener {
            var rec = ""

            when(binding.tvAirPoll.text){
                "Хорошо" -> rec = MAIN.resources.getString(R.string.AirLevel1)
                "Средне" -> rec = MAIN.resources.getString(R.string.AirLevel2)
                "Вредно для уязвимых групп"-> rec = MAIN.resources.getString(R.string.AirLevel3)
                "Вредно"-> rec = MAIN.resources.getString(R.string.AirLevel4)
                "Очень вредно" -> rec = MAIN.resources.getString(R.string.AirLevel5)
                "Опасно" -> rec = MAIN.resources.getString(R.string.AirLevel6)
            }

            DialogManager.recomendationDialog(requireContext(), rec)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Proverca(){
        val database = MainDb.getDb(MAIN)
        val rowCount = database.getDao().countTableRowsAirPollution()
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
        try {
            val database = MainDb.getDb(MAIN)
            val url = "https://www.iqair.com/ru/russia/${city}"
            val client = OkHttpClient()
            val currDate = LocalDateTime.now()
            val date = "${currDate.dayOfMonth}-${currDate.monthValue}-${currDate.year}"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val document = Jsoup.parse(response.body?.string())

                val container = document.select("div[class=aqi-overview]")

                    val aqi = container.select("div[class=aqi-value-wrapper]")
                        .select("p[class=aqi-value__value]")
                        .text()

                    val pollutionLevel = container.select("div[class=aqi-value-wrapper]")
                        .select("p[class=aqi-status]")
                        .select("span[class=aqi-status__text]")
                        .text()

                    val mainPolluter = container.select("div[class=aqi-overview-detail]")
                        .select("table[class=aqi-overview-detail__other-pollution-table]")
                        .select("tbody")
                        .text()

                    val imagePollution = container.select("div[class=aqi-overview__summary aqi-yellow]")
                        .select("img[class=aqi__icon]")
                        .attr("src")

                        val airPol = AirPollution(
                            null,
                            city,
                            date,
                            aqi,
                            pollutionLevel,
                            "Главный загрязнитель: ${mainPolluter}",
                            imagePollution)

                    database.getDao().insertAirPollution(airPol)
            }
        }
        catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
        }
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
        database.getDao().getAirPollution().asLiveData().observe(MAIN) { list ->

            list.forEach { AirPoll ->
                tvAirDate.text = AirPoll.date
                tvAQI.text = AirPoll.aqi
                tvAirPoll.text = AirPoll.pollutionLevel
                tvMainPoll.text = AirPoll.mainPolluter

                when(AirPoll.pollutionLevel){
                    "Хорошо" -> pollutionImg.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVGreenIndicate))
                    "Средне" -> pollutionImg.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVYellowIndicate))
                    "Вредно для уязвимых групп"-> pollutionImg.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVOrangeIndicate))
                    "Вредно"-> pollutionImg.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVRedIndicate))
                    "Очень вредно" -> pollutionImg.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVPerpleIndicate))
                    "Опасно" -> pollutionImg.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.danger))
                }
            }
        }
    }

    private fun initRecycle(city: String) = with(binding) {
        val layoutManager = LinearLayoutManager(MAIN)
        recyclerView = binding.rcAirPoll
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        lifecycleScope.launchWhenStarted {
            genRecycleAir(city).observe(viewLifecycleOwner) {Alist ->
                adapter = AirPollutionAdapter(Alist)
                recyclerView.adapter = adapter
            }
        }
    }

    private suspend fun genRecycleAir(city: String): LiveData<List<AirPollutionModel>> = withContext(Dispatchers.IO){
        val listAir = MutableLiveData<List<AirPollutionModel>>()
        try {
            val url = "https://www.iqair.com/ru/russia/${city}"
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val document = Jsoup.parse(response.body?.string())

                val containers = document.select("div[class=table-wrapper]").select("tbody [_ngcontent-sc307=\"\"]")

                val AirList = ArrayList<AirPollutionModel>()
                for (container in containers) {

                    val date = container.select("tr")
                        .select("td")
                        .first()
                        ?.text()

                    val aqi = container.select("tr")
                        .select("div")
                        .select("p")
                        .select("span")
                        .select("b")
                        .text()

                    val pollutionLevel =container.select("tr")
                        .select("div")
                        .select("p")
                        .select("strong")
                        .text()


                    val imagePollution = container.select("tr")
                            .select("div")
                            .select("img")
                            .attr("src")
                    if(aqi != "") {
                        val airPol = AirPollutionModel(
                            date!!,
                            aqi,
                            pollutionLevel,
                            imagePollution
                        )

                        AirList.add(airPol)
                    }
                }
                MainScope().launch {
                    listAir.value = AirList
                }
            }
        return@withContext listAir
        }
        catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
            return@withContext listAir
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AirPollutionFragment()
    }
}