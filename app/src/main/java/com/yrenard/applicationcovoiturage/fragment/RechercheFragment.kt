package com.yrenard.applicationcovoiturage.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.yrenard.applicationcovoiturage.model.Recherche
import com.yrenard.applicationcovoiturage.model.TypeRecherche
import com.yrenard.applicationcovoiturage.R
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.bindDefaultValueFromSPFile
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.getSpinner
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.verifyFieldForTravelButton
import com.yrenard.applicationcovoiturage.databinding.FragmentRechercheBinding
import com.yrenard.applicationcovoiturage.model.data.*

class RechercheFragment : Fragment() {
    private var _binding: FragmentRechercheBinding? = null
    private val binding get() = _binding!!

    private lateinit var animShake: Animation


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this._binding = FragmentRechercheBinding.inflate(inflater, container, false)
        this.initUI()
        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null;
    }

    private fun initUI(){
        if(this.arguments != null){
            val email = this.requireArguments().getString(ARG_EMAIL, ARG_EMAIL_UNDEFINED)
            Toast.makeText(this.requireContext(),
                this.getString(R.string.welcomeMessage, email),
                Toast.LENGTH_LONG).show()
        }
        animShake = AnimationUtils.loadAnimation(
            this.requireContext(), R.anim.shake_button)

        this.initSpinnerCegep()

        this.bindDefaultValue()

        this.binding.buttonSearch.setOnClickListener{ this.onSearchButtonClick() }
        this.binding.buttonDisplayAll.setOnClickListener{ this.onDisplayAllButtonClick() }


    }

    private fun onSearchButtonClick(){
        if(this.verifyFieldForTravelButton(
            arrayOf(this.binding.editTextStartCity, this.binding.editTextNbrPass))){
            this.binding.buttonSearch.startAnimation(this.animShake)
            return
        }
        this.rechercher()
    }

    private fun onDisplayAllButtonClick(){
        Log.d(DEBUG_LOG_NAME, "Go to result fragment -> DisplayAll mode")
        this.afficherTous()
    }

    private fun initSpinnerCegep(){
        this.binding.spinnerCities.adapter = this.getSpinner<String>(DataManager.listeCegeps)
    }

    private fun rechercher(){
        this.sauvegarderRechercher()

        val recherche = Recherche(
            nbPassagers = Integer.parseInt(this.binding.editTextNbrPass.text.toString()),
            villeDepart = this.binding.editTextStartCity.text.toString(),
            cegepArrivee = this.binding.spinnerCities.selectedItem as String,
            dateJour = this.binding.datePickerDate.dayOfMonth,
            dateMois = this.binding.datePickerDate.month + 1,
            dateAnnee = this.binding.datePickerDate.year,
            nonFumeur = this.binding.radioButtonNoSmoke.isChecked,
            accepteBagage = this.binding.radioButtonBagage.isChecked,
            aAirClimatise = this.binding.radioButtonAC.isChecked
        )
        Log.d(DEBUG_LOG_NAME, "Go to result fragment -> Filter Mode")
        this.goToResult(recherche)
    }

    private fun afficherTous(){
        this.goToResult(Recherche(typeRecherche = TypeRecherche.TOUS))
    }

    private fun goToResult(recherche: Recherche){
        val action = RechercheFragmentDirections
            .actionRechercheFragmentToResultatFragment(
                argRechercheObject = recherche
            )
        this.findNavController().navigate(action)
    }

    private fun sauvegarderRechercher(){
        FichierManager.sauvegarderConfig(this.requireContext(),
            SHARED_PREFERENCES_FIELD_STARTCITY,
            this.binding.editTextStartCity.text.toString(),
            SharedPreferenceType.NORMAL
            )
    }

    private fun bindDefaultValue(){
        this.bindDefaultValueFromSPFile(this.binding.editTextStartCity, SHARED_PREFERENCES_FIELD_STARTCITY)
    }
}