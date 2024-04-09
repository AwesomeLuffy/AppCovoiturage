package com.yrenard.applicationcovoiturage.fragment

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yrenard.applicationcovoiturage.R
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.verifyFieldForTravelButton
import com.yrenard.applicationcovoiturage.databinding.FragmentProfileBinding
import com.yrenard.applicationcovoiturage.model.Usager
import com.yrenard.applicationcovoiturage.model.data.*

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var actualUser: Usager

    private lateinit var animShake: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentProfileBinding.inflate(inflater, container, false)
        this.initUI()
        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
    private fun initUI(){
        animShake = AnimationUtils.loadAnimation(
            this.requireContext(), R.anim.shake_button)

        this.binding.buttonLogOut.setOnClickListener{ this.OnLogOutButtonClicked() }
        this.binding.buttonApply.setOnClickListener{ this.onApplyButtonClicked() }

        this.actualUser= DataManager.getUsager(DataManager.idConnectedUser)

        this.binding.plainTextCourriel.setText(this.actualUser.courriel)
        this.binding.plainTextName.setText(this.actualUser.prenom)
        this.binding.plainTextSurname.setText(this.actualUser.nom)
        this.binding.plainTextPhoneNumber.setText(this.actualUser.numeroTelephone)
        this.binding.plainTextPassWord.setText(this.actualUser.motdepasse)
        this.binding.plainTextConfirmPassword.setText(this.actualUser.motdepasse)
    }

    private fun OnLogOutButtonClicked(){
        this.deconnecter()
    }

    private fun deconnecter(){
        FichierManager.sauvegarderConfig(this.requireContext(),
            ENC_SHARED_PREFERENCES_FIELD_MAIL,
            GLOB_SHARE_PREFERENCE_DEFAULT_STRING,
            SharedPreferenceType.ENCRYPTED
        )
        FichierManager.sauvegarderConfig(this.requireContext(),
            ENC_SHARED_PREFERENCES_FIELD_PASSWORD,
            GLOB_SHARE_PREFERENCE_DEFAULT_STRING,
            SharedPreferenceType.ENCRYPTED
        )

        DataManager.idConnectedUser = -1

        this.findNavController()
            .navigate(ProfileFragmentDirections.actionProfileFragmentToConnectionFragment())
    }

    private fun onApplyButtonClicked(){
        if(this.verifyFieldForTravelButton(arrayOf(
                this.binding.plainTextName,
                this.binding.plainTextSurname,
                this.binding.plainTextCourriel,
                this.binding.plainTextPhoneNumber,
                this.binding.plainTextPassWord,
                this.binding.plainTextConfirmPassword
            ))){
            return
        }

        if(this.binding.plainTextPassWord.text.toString() !=
            this.binding.plainTextConfirmPassword.text.toString()){
            this.binding.plainTextConfirmPassword.error = this.getString(R.string.errFieldPasswordNotMatch)
            return
        }

        val usager: Usager = Usager(
            id = DataManager.idConnectedUser,
            nom = this.binding.plainTextSurname.text.toString(),
            prenom = this.binding.plainTextName.text.toString(),
            courriel = this.binding.plainTextCourriel.text.toString(),
            motdepasse = this.binding.plainTextPassWord.text.toString(),
            numeroTelephone = this.binding.plainTextPhoneNumber.text.toString(),
            listeEvaluations = DataManager.getUsager(DataManager.idConnectedUser).listeEvaluations
        )

        if(this.actualUser == usager){
            return
        }

        DatabaseHandler.creeOrUpdateUsager(this.requireContext(), usager, TypeInsertOrUpdate.UPDATE)

        Toast.makeText(this.requireContext(),
            this.getString(R.string.stringChangedApplied),
            Toast.LENGTH_LONG).show()
    }
}