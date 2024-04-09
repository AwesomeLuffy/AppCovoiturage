package com.yrenard.applicationcovoiturage.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import com.yrenard.applicationcovoiturage.MainActivity
import com.yrenard.applicationcovoiturage.databinding.FragmentDeveloppementBinding

class DeveloppementFragment : Fragment() {
    private var _binding: FragmentDeveloppementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(this.requireActivity().actionBar != null){
            this.requireActivity().actionBar!!.setDisplayHomeAsUpEnabled(false)
        }
        this._binding = FragmentDeveloppementBinding.inflate(inflater, container, false)
        this.initUI()
        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null;
    }

    private fun initUI(){}
}