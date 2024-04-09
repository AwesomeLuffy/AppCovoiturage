package com.yrenard.applicationcovoiturage.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.yrenard.applicationcovoiturage.model.data.DatabaseHandler
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.verifyFieldForTravelButton
import com.yrenard.applicationcovoiturage.databinding.FragmentInscriptionBinding
import com.yrenard.applicationcovoiturage.model.Usager
import com.yrenard.applicationcovoiturage.model.data.DataManager
import com.yrenard.applicationcovoiturage.R as RR

class InscriptionFragment : Fragment() {

    private var _binding: FragmentInscriptionBinding? = null
    private val binding get() = _binding!!

    private lateinit var animShake: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this._binding = FragmentInscriptionBinding.inflate(inflater, container, false)
        this.initUI()
        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null;
    }

    private fun initUI(){
        animShake = AnimationUtils.loadAnimation(
                this.requireContext(), RR.anim.shake_button)
        this.binding.buttonSignIn.setOnClickListener{ this.onSignInButtonClick() }
    }

    private fun onSignInButtonClick(){
        if(this.verifyFieldForTravelButton(arrayOf(
            this.binding.plainTextName,
            this.binding.plainTextSurname,
            this.binding.plainTextCourriel,
            this.binding.plainTextPhoneNumber,
            this.binding.plainTextPassWord,
            this.binding.plainTextConfirmPassword
        ))){
            this.binding.buttonSignIn.startAnimation(animShake)
            return
        }

        if(DataManager.checkIfUserExists(this.binding.plainTextCourriel.text.toString())){
            this.binding.plainTextCourriel.error = this.getString(RR.string.errEmailAlreadyExists)
            return
        }

        if(this.binding.plainTextPassWord.text.toString() !=
            this.binding.plainTextConfirmPassword.text.toString()){
            this.binding.plainTextConfirmPassword.error = this.getString(com.yrenard.applicationcovoiturage.R.string.errFieldPasswordNotMatch)
            return
        }

        val usager: Usager = Usager(
            id = -1,
            nom = this.binding.plainTextName.text.toString(),
            prenom = this.binding.plainTextSurname.text.toString(),
            courriel = this.binding.plainTextCourriel.text.toString(),
            motdepasse = this.binding.plainTextPassWord.text.toString(),
            numeroTelephone = this.binding.plainTextPhoneNumber.text.toString(),
            listeEvaluations = ArrayList()
        )

        DatabaseHandler.creeOrUpdateUsager(this.requireContext(), usager)

        Toast.makeText(this.requireContext(),
        this.getString(RR.string.stringUserWillBeCreated),
        Toast.LENGTH_LONG).show()

        this.findNavController().navigate(
            InscriptionFragmentDirections.actionInscriptionFragmentToConnectionFragment())
    }
}