package com.yrenard.applicationcovoiturage.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yrenard.applicationcovoiturage.R
import com.yrenard.applicationcovoiturage.databinding.FragmentConnectionBinding
import com.yrenard.applicationcovoiturage.model.data.*
import kotlin.random.Random

class ConnectionFragment : Fragment() {
    private var _binding: FragmentConnectionBinding? = null
    private val binding get() = _binding!!
    private var numberOfClick: Int = 0

    private lateinit var animShake: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this._binding = FragmentConnectionBinding.inflate(inflater, container, false)
        if (!this.autoConnect()) {
            this.initUI()
        }
        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .visibility = View.VISIBLE
        this._binding = null;
    }

    private fun initUI() {

        animShake = AnimationUtils.loadAnimation(
            this.requireContext(), R.anim.shake_button)

        this.requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .visibility = View.GONE

        this.binding.imageViewCarLogo.setOnClickListener {
            this.onImageViewClick()
        }
        this.binding.buttonLogIn.setOnClickListener { this.connecter() }
        this.binding.buttonSignIn.setOnClickListener { this.signIn() }


    }

    private fun onImageViewClick() {
        this.numberOfClick++
        Log.d("DEBUGMODE", "ttt" + this.numberOfClick.toString())

        if (this.numberOfClick > 2) {
            if (this.numberOfClick == 3) {
                Toast.makeText(this.requireContext(), "Secret mode unlocked !", Toast.LENGTH_LONG)
                    .show()
            }
            this.modifierLogoMenu()
        }
    }

    private fun modifierLogoMenu() {
        val resID = resources.getIdentifier(
            "logo_" + Random.nextInt(1, 25).toString(),
            "drawable",
            this.requireContext().packageName
        )
        this.binding.imageViewCarLogo.setImageDrawable(
            ContextCompat.getDrawable(this.requireContext(), resID)
        )

    }

    private fun connecter() {
        if (this.binding.plainTextCourriel.text.toString() != "" &&
            this.binding.plainTextPassWord.text.toString() != ""
        ) {

            val result = DataManager.checkUserMailAndPass(
                this.binding.plainTextCourriel.text.toString(),
                this.binding.plainTextPassWord.text.toString()
            )

            if (result.first) {//If mail exists
                Log.d("CoVoitDebug", "Email exists")
                if (result.second) {//If password correct
                    Log.d("CoVoitDebug", "Password good, start action")

                    FichierManager.sauvegarderConfig(
                        this.requireContext(),
                        ENC_SHARED_PREFERENCES_FIELD_MAIL,
                        this.binding.plainTextCourriel.text.toString(),
                        SharedPreferenceType.ENCRYPTED
                    )

                    FichierManager.sauvegarderConfig(
                        this.requireContext(),
                        ENC_SHARED_PREFERENCES_FIELD_PASSWORD,
                        this.binding.plainTextPassWord.text.toString(),
                        SharedPreferenceType.ENCRYPTED
                    )

                    val action =
                        ConnectionFragmentDirections.actionConnectionFragmentToRechercheFragment(
                            argEmail = this.binding.plainTextCourriel.text.toString()
                        )
                    this.findNavController().navigate(action)
                } else {
                    this.binding.plainTextPassWord.error = this.getString(R.string.errPasswordError)
                }
            } else {
                this.binding.plainTextCourriel.error = this.getString(R.string.errEmailError)
            }
        }
        else{
            this.binding.buttonLogIn.startAnimation(this.animShake)
        }
    }

    private fun signIn() {
        this.findNavController().navigate(
            ConnectionFragmentDirections.actionConnectionFragmentToInscriptionFragment()
        )
    }

    private fun autoConnect(): Boolean {
        val mail: String = FichierManager.lireConfig(
            this.requireContext(),
            ENC_SHARED_PREFERENCES_FIELD_MAIL,
            SharedPreferenceType.ENCRYPTED
        ) ?: return false

        if (mail != "") {
            val pass = FichierManager.lireConfig(
                this.requireContext(),
                ENC_SHARED_PREFERENCES_FIELD_PASSWORD,
                SharedPreferenceType.ENCRYPTED
            ) ?: return false

            val result = DataManager.checkUserMailAndPass(mail, pass)

            if (result.first) {
                if (result.second) {
                    Log.d("CoVoitDebug", "Mail : " + mail + " MDP : " + pass)
                    val action =
                        ConnectionFragmentDirections.actionConnectionFragmentToRechercheFragment(
                            argEmail = mail
                        )
                    this.findNavController().navigate(action)
                    return true
                }
            }
        }
        return false
    }

}