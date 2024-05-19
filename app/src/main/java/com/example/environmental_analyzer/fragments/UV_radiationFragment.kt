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
import com.example.environmental_analyzer.Entity.UVRadiation
import com.example.environmental_analyzer.MAIN
import com.example.environmental_analyzer.MainDb
import com.example.environmental_analyzer.Models.UVRadiationModel
import com.example.environmental_analyzer.R
import com.example.environmental_analyzer.adapters.UVRadiationAdapder
import com.example.environmental_analyzer.databinding.FragmentUVRadiationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.time.LocalDateTime

class UV_radiationFragment : Fragment() {

    lateinit var binding: FragmentUVRadiationBinding
    private lateinit var adapter: UVRadiationAdapder
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentUVRadiationBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNETConnection()
        Proverca()
        updateCurrentCard()
        initRecycle("penza")

        binding.syncUVbutton.setOnClickListener {
            val db = MainDb.getDb(MAIN)
            Thread {
                db.getDao().deleteUVRad()
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

        binding.recUVbutton.setOnClickListener {
            var rec = ""

            when(binding.tvUVlevel.text){
                "низкий" -> rec = MAIN.resources.getString(R.string.UVLevel1)
                "умеренный" -> rec = MAIN.resources.getString(R.string.UVLevel2)
                "высокий"-> rec = MAIN.resources.getString(R.string.UVLevel3)
                "очень высокий"-> rec = MAIN.resources.getString(R.string.UVLevel4)
                "экстремальный" -> rec = MAIN.resources.getString(R.string.UVLevel5)
            }

            DialogManager.recomendationDialog(requireContext(), rec)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Proverca(){
        val database = MainDb.getDb(MAIN)
        val rowCount = database.getDao().countTableRowsUVRad()
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
            val url = "https://www.pogodairadar.com/uf-indeks/${city}"
            val client = OkHttpClient()
            val currDate = LocalDateTime.now()
            val date = "${currDate.dayOfMonth}-${currDate.monthValue}-${currDate.year}"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val document = Jsoup.parse(response.body?.string())

                val container = document.select("div[class=column left]")

                val uvIndex = container.select("div[class=header]")
                    .select("div[class=number]")
                    .first()
                    ?.text()

                val uvLevel = container.select("div[class=header]")
                    .select("div[class=text]")
                    .first()
                    ?.text()

                val imageUV = ""

                val UVrad = UVRadiation(
                    null,
                    city,
                    date,
                    uvIndex!!,
                    uvLevel!!,
                    imageUV)

                database.getDao().insertUVRad(UVrad)
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
        database.getDao().getUVRad().asLiveData().observe(MAIN) { list ->

            list.forEach { UVlist ->
                tvDateUV.text = UVlist.date
                tvUVlevel.text = UVlist.UVLevel
                tvUVindex.text = UVlist.UVIndex
                tvCityUV.text = UVlist.city

                when(UVlist.UVLevel){
                    "низкий" -> UVcolorIndicate.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVGreenIndicate))
                    "умеренный" -> UVcolorIndicate.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVYellowIndicate))
                    "высокий"-> UVcolorIndicate.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVOrangeIndicate))
                    "очень высокий"-> UVcolorIndicate.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVRedIndicate))
                    "экстремальный" -> UVcolorIndicate.setBackgroundColor(ContextCompat.getColor(MAIN, R.color.UVPerpleIndicate))
                }
            }
        }
    }

    private fun initRecycle(city: String) {
        val layoutManager = LinearLayoutManager(MAIN)
        recyclerView = binding.rcUV
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        lifecycleScope.launchWhenStarted {
            genRecycleAir(city).observe(viewLifecycleOwner) {UVlist ->
                adapter = UVRadiationAdapder(UVlist)
                recyclerView.adapter = adapter
            }
        }
    }

    private suspend fun genRecycleAir(city: String): LiveData<List<UVRadiationModel>> = withContext(
        Dispatchers.IO){
        val listUV = MutableLiveData<List<UVRadiationModel>>()
        try {
        val url = "https://www.pogodairadar.com/uf-indeks/${city}"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val document = Jsoup.parse(response.body?.string())

            val containers = document.select("div[class=header]")

            val UVList = ArrayList<UVRadiationModel>()
            for (container in containers) {

                val date = container
                    .select("div[class=left]")
                    .select("time")
                    .text()

                val UVIndex = container.select("div[class=number]")
                    .text()

                val UVlevel =container.select("div[class=text]")
                    .text()

                if(UVIndex != "") {
                    val UVRad = UVRadiationModel(
                        date,
                        UVIndex,
                        UVlevel
                    )

                    UVList.add(UVRad)
                }
            }
            MainScope().launch {
                listUV.value = UVList
            }
        }
        return@withContext listUV
        }
        catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
            return@withContext listUV
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UV_radiationFragment()
    }
}