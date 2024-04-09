package com.yrenard.applicationcovoiturage.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yrenard.applicationcovoiturage.model.data.DataManager
import com.yrenard.applicationcovoiturage.model.Recherche
import com.yrenard.applicationcovoiturage.model.TypeRecherche
import com.yrenard.applicationcovoiturage.model.Voyage
import com.yrenard.applicationcovoiturage.R
import com.yrenard.applicationcovoiturage.model.data.ARG_RECHERCHE
import com.yrenard.applicationcovoiturage.databinding.FragmentResultatBinding
import com.yrenard.applicationcovoiturage.model.adapter.VoyageRecyclerAdapter

class ResultatFragment : Fragment() {
    private var _binding: FragmentResultatBinding? = null
    private val binding get() = _binding!!
    private var recherche: Recherche? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this._binding = FragmentResultatBinding.inflate(inflater, container, false)
        this.initUI()
        return this._binding!!.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null;
    }

        private fun initUI(){
        if(this.arguments == null){
            this.binding.textView.text = this.getString(R.string.errErrHappenned)
        }

        this.recherche = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){//If api 33
            this.requireArguments().getSerializable(ARG_RECHERCHE, Recherche::class.java)!!
        } else{//if lower than 33
            this.requireArguments().getSerializable(ARG_RECHERCHE) as Recherche
        }

        this.binding.textView.text = if(this.recherche?.typeRecherche == TypeRecherche.FILTRE){
            String.format(this.getString(R.string.resultFragmentTextViewMessage),
                this.recherche?.villeDepart,
                this.recherche?.cegepArrivee)
        } else {
            "Tout les voyages"
        }
        this.initListeVoyage()


    }

    private fun initListeVoyage(){
        this.binding.voyageRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        var voyages: ArrayList<Voyage> = DataManager.listeVoyages

        this.binding.voyageRecyclerView.adapter = if (this.recherche?.typeRecherche == TypeRecherche.TOUS){
            VoyageRecyclerAdapter(this.requireContext(),
                voyages
            )
        } else {
            voyages = DataManager.rechercheVoyage(this.recherche!!)
            VoyageRecyclerAdapter(this.requireContext(),
                voyages)
        }

        (this.binding.voyageRecyclerView.adapter as VoyageRecyclerAdapter).onItemClick = {
            position -> this.onRecyclerViewItemClicked(position, voyages)
        }

    }

    private fun onRecyclerViewItemClicked(position: Int, voyages: ArrayList<Voyage>){
        Toast.makeText(this.requireContext(),
            String.format(this.getString(R.string.toastRecyclerViewItemSelected),
                voyages[position].idConducteur), Toast.LENGTH_SHORT).show()

        this.findNavController().navigate(ResultatFragmentDirections
            .actionResultatFragmentToDetailsFragment(argVoyage = voyages[position]))
    }


}