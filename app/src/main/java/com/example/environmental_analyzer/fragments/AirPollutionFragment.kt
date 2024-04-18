package com.example.environmental_analyzer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.environmental_analyzer.databinding.FragmentAirPollutionBinding

class AirPollutionFragment : Fragment() {

    lateinit var binding: FragmentAirPollutionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAirPollutionBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AirPollutionFragment()
    }
}