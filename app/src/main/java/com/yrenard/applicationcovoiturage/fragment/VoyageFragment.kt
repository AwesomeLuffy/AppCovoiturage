package com.yrenard.applicationcovoiturage.fragment

import android.os.Build.VERSION_CODES.N
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yrenard.applicationcovoiturage.R as RR
import com.yrenard.applicationcovoiturage.model.data.DataManager
import com.yrenard.applicationcovoiturage.model.data.DatabaseHandler
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.bindDefaultValueFromSPFile
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.getSpinner
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.verifyFieldForTravelButton
import com.yrenard.applicationcovoiturage.model.data.SHARED_PREFERENCES_FIELD_STARTCITY
import com.yrenard.applicationcovoiturage.databinding.FragmentVoyageBinding
import com.yrenard.applicationcovoiturage.model.Voyage
import java.time.LocalDate

class VoyageFragment : Fragment() {
    private var _binding: FragmentVoyageBinding? = null
    private val binding get() = _binding!!

    private lateinit var animShake: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this._binding = FragmentVoyageBinding.inflate(inflater, container, false)
        this.initUI()
        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }

    private fun initUI(){
        animShake = AnimationUtils.loadAnimation(
            this.requireContext(), RR.anim.shake_button)

        this.initSpinner()
        this.bindDefaultValue()
        this.binding.buttonAdd.setOnClickListener{ this.onAddTravelButtonClick() }
    }

    private fun initSpinner(){
        this.binding.spinnerCities.adapter = this.getSpinner<String>(DataManager.listeCegeps)

    }

    private fun onAddTravelButtonClick(){
        if(this.verifyFieldForTravelButton(
            arrayOf(this.binding.editTextNbrPass,
                this.binding.editTextStartCity,
                this.binding.editTextStartAddress,
                this.binding.editTextArrivalAddress,
                this.binding.editTextPrice))){
            this.binding.buttonAdd.startAnimation(this.animShake)
            return
        }


        val voyage = Voyage(
            id=-1,
            villeDepart = this.binding.editTextStartCity.text.toString(),
            cegepArrivee = this.binding.spinnerCities.selectedItem as String,
            date = LocalDate.of(this.binding.datePickerDate.year,
                this.binding.datePickerDate.month + 1,
                this.binding.datePickerDate.dayOfMonth),
            adresseDepart = this.binding.editTextStartAddress.text.toString(),
            adresseArrivee = this.binding.editTextArrivalAddress.text.toString(),
            idConducteur = -1,
            prix = this.binding.editTextPrice.text.toString().toFloat(),
            nbMaxPassager = Integer.parseInt(this.binding.editTextNbrPass.text.toString()),
            listeIdPassagers = ArrayList(),
            nonFumeur = this.binding.radioButtonNoSmoke.isChecked,
            accepteBagage = this.binding.radioButtonBagage.isChecked,
            aAirClimatise = this.binding.radioButtonAC.isChecked
        )

        DatabaseHandler.creeVoyage(this.requireContext(), voyage)

        Toast.makeText(
            this.requireContext()
            , this.getString(RR.string.stringTravelWillBeCreated),
            Toast.LENGTH_LONG).show()


    }

    private fun bindDefaultValue(){
        this.bindDefaultValueFromSPFile(this.binding.editTextStartCity, SHARED_PREFERENCES_FIELD_STARTCITY)
    }

}