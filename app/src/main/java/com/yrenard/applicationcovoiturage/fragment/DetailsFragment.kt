package com.yrenard.applicationcovoiturage.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.yrenard.applicationcovoiturage.R
import com.yrenard.applicationcovoiturage.model.data.ARG_VOYAGE
import com.yrenard.applicationcovoiturage.model.data.DataManager
import com.yrenard.applicationcovoiturage.model.data.DatabaseHandler
import com.yrenard.applicationcovoiturage.model.data.ExtendFragment.verifyFieldForTravelButton
import com.yrenard.applicationcovoiturage.databinding.FragmentDetailsBinding
import com.yrenard.applicationcovoiturage.model.Evaluation
import com.yrenard.applicationcovoiturage.model.Voyage

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var voyage: Voyage

    private lateinit var animShake: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this._binding = FragmentDetailsBinding.inflate(inflater, container, false)

        this.initUI()

        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null;
    }

    private fun initUI() {
        if (this.arguments != null) {
            this.voyage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {//If api 33
                this.requireArguments().getSerializable(ARG_VOYAGE, Voyage::class.java)!!
            } else {//if lower than 33
                this.requireArguments().getSerializable(ARG_VOYAGE) as Voyage
            }
        }

        animShake = AnimationUtils.loadAnimation(
            this.requireContext(), R.anim.shake_button)

        this.binding.listViewPassager.adapter =
            ArrayAdapter<String>(
                this.requireContext(),
                android.R.layout.simple_list_item_1,
                DataManager.getUsagerListFromVoyages(voyage)
            )

        this.binding.buttonJoinTravel.setOnClickListener { this.onButtonJoinClick() }
        this.binding.buttonLeaveTravel.setOnClickListener { this.onButtonLeaveClick() }
    }

    private fun onButtonJoinClick() {
        if (this.voyage.idConducteur == DataManager.idConnectedUser ||
            DataManager.idConnectedUser in this.voyage.listeIdPassagers) {
            this.binding.buttonJoinTravel.startAnimation(this.animShake)
            return
        }

        DatabaseHandler.joindreVoyage(
            null,
            this.requireContext(),
            this.voyage.id,
            DataManager.idConnectedUser
        )

        this.voyage.listeIdPassagers.add(DataManager.idConnectedUser)
    }

    private fun onButtonLeaveClick() {
        if (this.voyage.idConducteur == DataManager.idConnectedUser ||
            DataManager.idConnectedUser !in this.voyage.listeIdPassagers
        ) {
            this.binding.buttonLeaveTravel.startAnimation(this.animShake)
            return
        }

        if (this.voyage.id != -1 && this.binding.editTextNote.text.toString() != "") {
            if (this.verifyFieldForTravelButton(arrayOf(this.binding.editTextNote))) {
                DatabaseHandler.insertEvaluation(
                    this.requireContext(),
                    this.voyage.idConducteur,
                    Evaluation(
                        DataManager.idConnectedUser,
                        this.binding.editTextNote.text.toString().toInt()
                    )
                )
            }
        }

        DatabaseHandler.quitterVoyage(
            this.requireContext(),
            this.voyage.id,
            DataManager.idConnectedUser
        )

        this.voyage.listeIdPassagers.remove(DataManager.idConnectedUser)
    }
}