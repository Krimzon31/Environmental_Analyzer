package com.example.environmental_analyzer.fragments

import adapters.WeatherAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.environmental_analyzer.MainViewModel
import com.example.environmental_analyzer.databinding.FragmentDaysBinding


class DaysFragment : Fragment() {

    private lateinit var  binding: FragmentDaysBinding
    private lateinit var  adapter: WeatherAdapter
    private val model : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataList.observe(viewLifecycleOwner){
            adapter.submitList(it.subList(1, it.size))
        }
    }

    private fun initRcView() = with(binding){
        RcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter()
        RcView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}