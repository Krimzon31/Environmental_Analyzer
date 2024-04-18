package com.example.environmental_analyzer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.environmental_analyzer.databinding.FragmentUVRadiationBinding

class UV_radiationFragment : Fragment() {

    lateinit var binding: FragmentUVRadiationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUVRadiationBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = UV_radiationFragment()
    }
}