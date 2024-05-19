package com.example.environmental_analyzer.fragments

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.environmental_analyzer.DialogManager
import com.example.environmental_analyzer.Entity.Allergy
import com.example.environmental_analyzer.MAIN
import com.example.environmental_analyzer.MainDb
import com.example.environmental_analyzer.Models.AllergyModel
import com.example.environmental_analyzer.R
import com.example.environmental_analyzer.adapters.AllergyAdapter
import com.example.environmental_analyzer.databinding.FragmentAllergyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class AllergyFragment : Fragment() {

    lateinit var binding: FragmentAllergyBinding
    private lateinit var adapter: AllergyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var bundle: Bundle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bundle = Bundle()

        Proverca()
        updateCurrentCard()

        binding.syncAllergyButt.setOnClickListener {
            val db = MainDb.getDb(MAIN)
            Thread {
                db.getDao().deleteAllergy()
            }.start()
            lifecycleScope.launchWhenStarted {
                setData("penza")
                updateCurrentCard()
            }
            Toast.makeText(
                MAIN,
                "Данные обновлены",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.recButtonAllergy.setOnClickListener{
            val rec = MAIN.resources.getString(R.string.AllergyRecom)
            DialogManager.recomendationDialog(requireContext(), rec)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Proverca(){
        val database = MainDb.getDb(MAIN)
        val rowCount = database.getDao().countTableRowsAllergy()
        rowCount.observeForever { count ->
            if (count == 0) {
                lifecycleScope.launchWhenStarted {
                    setData("penza")
                    updateCurrentCard()
                }
                return@observeForever
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun setData(city: String) = withContext(Dispatchers.IO) {
        try {
            val database = MainDb.getDb(MAIN)
            val currDate = LocalDateTime.now()
            val date = "${currDate.dayOfMonth}-${currDate.monthValue}-${currDate.year}"

            val month = currDate.monthValue

            var seson = ""
            var mainAlerg = ""

            when(month){
                1-> seson = "Январский"
                2-> seson = "Февральский"
                3-> seson = "Мартовский"
                4-> seson = "Апрельский"
                5-> seson = "Майский"
                6-> seson = "Июньский"
                7-> seson = "Июльский"
                8-> seson = "Августовский"
                9-> seson = "Сентяборьский"
                10-> seson = "Октяборьский"
                11-> seson = "Нояборьский"
                12-> seson = "Декаборьский"
            }

            when(month){
                1-> mainAlerg = "-"
                2-> mainAlerg = "-"
                3-> mainAlerg = "Лиственные кустарники"
                4-> mainAlerg = "Лиственные деревья"
                5-> mainAlerg = "Хвойные деревья"
                6-> mainAlerg = "Злаковые"
                7-> mainAlerg = "Злаковые"
                8-> mainAlerg = "Сорные травы"
                9-> mainAlerg = "Сорные травы"
                10-> mainAlerg = "-"
                11-> mainAlerg = "-"
                12-> mainAlerg = "-"
            }


            val allergy = Allergy(
                null,
                city,
                date,
                seson,
                mainAlerg,
                ""
            )

            database.getDao().insertAllergy(allergy)

        }
        catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateCurrentCard() = with(binding){

        val database = MainDb.getDb(MAIN)
        database.getDao().getAllergy().asLiveData().observe(MAIN) { list ->

            list.forEach { Alist ->

                tvAllergyCity.text = Alist.city
                tvDateAllergy.text = Alist.date
                tvSeson.text = Alist.seson
                tvMainAllergy.text = Alist.mainAllerg

                initRecycle()

            }
        }
    }

    private fun initRecycle() {
        val layoutManager = LinearLayoutManager(MAIN)
        recyclerView = binding.rcPlant
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        lifecycleScope.launchWhenStarted {
            genRecycleAllergy().observe(viewLifecycleOwner) {AllList ->
                adapter = AllergyAdapter(AllList)
                recyclerView.adapter = adapter
            }
        }
    }

    private suspend fun genRecycleAllergy(): LiveData<List<AllergyModel>> = withContext(
        Dispatchers.IO){
        val listAller = MutableLiveData<List<AllergyModel>>()
        //try {
            val AlList = ArrayList<AllergyModel>()

            val resources = MAIN.resources
            var arrayIndex = R.array.NoSeson

            when(binding.tvSeson.text){
                "Февральский"-> arrayIndex = R.array.Feb
                "Мартовский"-> arrayIndex = R.array.March
                "Апрельский"-> arrayIndex = R.array.Aprl
                "Майский"-> arrayIndex = R.array.May
                "Июньский"-> arrayIndex = R.array.Jun
                "Июльский"-> arrayIndex = R.array.Jul
                "Августовский"-> arrayIndex = R.array.Aug
                "Сентяборьский"-> arrayIndex = R.array.Sep
                "Октяборьский"-> arrayIndex = R.array.Okt
                "Нояборьский"-> arrayIndex = R.array.Nov
            }

            val allergyList = convertStringArrayToAllergyModelList(resources, arrayIndex)
            for (allergy in allergyList) {
                val AllM = AllergyModel(
                    allergy.plant_name
                )
                AlList.add(AllM)
            }

            MainScope().launch {
                listAller.value = AlList
            }
            return@withContext listAller
        //}
        /*catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
            return@withContext listAller
        }*/
    }

    fun convertStringArrayToAllergyModelList(resources: Resources, arrayStr: Int): ArrayList<AllergyModel> {
        val allergyList = ArrayList<AllergyModel>()
        val array = resources.getStringArray(arrayStr)
        for (plantName in array) {
            val allergyModel = AllergyModel(plantName)
            allergyList.add(allergyModel)
        }
        return allergyList
    }

    companion object {
        @JvmStatic
        fun newInstance() = AllergyFragment()
    }
}