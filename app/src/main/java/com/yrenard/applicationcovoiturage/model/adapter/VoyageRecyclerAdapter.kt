package com.yrenard.applicationcovoiturage.model.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yrenard.applicationcovoiturage.model.data.DataManager
import com.yrenard.applicationcovoiturage.model.Usager
import com.yrenard.applicationcovoiturage.model.Voyage
import com.yrenard.applicationcovoiturage.R
import java.time.format.DateTimeFormatter

enum class LAYOUTTYPE {LINEAR, GRID, STAGGERGRID}

class VoyageRecyclerAdapter (private val context: Context,
                             private val voyages: ArrayList<Voyage>,
                             private val layoutType: LAYOUTTYPE = LAYOUTTYPE.LINEAR) :
RecyclerView.Adapter<VoyageRecyclerAdapter.ViewHolder>(){

    private val layoutInflater = LayoutInflater.from(this.context)

    var onItemClick: ((Int) -> Unit)? = null //Empty function tu declare what to do when item clicked

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val editTextViewAddress = itemView.findViewById<TextView>(R.id.textViewAddress)
        val editTextViewConducteur = itemView.findViewById<TextView>(R.id.textViewConducteur)
        val editTextViewDate = itemView.findViewById<TextView>(R.id.textViewVoyageDate)
        val editTextViewPlace = itemView.findViewById<TextView>(R.id.textViewRemainingPlace)

        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(getLayoutType(parent))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conducteur: Usager = DataManager.getUsager(this.voyages[position].idConducteur)
        holder.editTextViewAddress.text = this.voyages[position].adresseDepart
        holder.editTextViewConducteur.text = (conducteur.prenom + " " + conducteur.nom)
        holder.editTextViewDate.text = this.voyages[position].date.format(DateTimeFormatter.ofPattern("yy-MMM-dd"))
        holder.editTextViewPlace.text = String.format(this.context.getString(R.string.placeRemaining),
            this.voyages[position].nbPlaceRestante())
    }

    private fun getLayoutType(parent: ViewGroup): View{
        //DEFAULT LINEAR
        if(layoutType == LAYOUTTYPE.GRID){
            return this.layoutInflater.inflate(R.layout.voyage_list_item, parent, false)
        }
        return this.layoutInflater.inflate(R.layout.voyage_list_item, parent, false)
    }

    override fun getItemCount(): Int {
        return this.voyages.size
    }
}