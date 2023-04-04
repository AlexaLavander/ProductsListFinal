package com.example.productslist.CosmosTheme

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.*
import com.example.productslist.Models.ListModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class Cosmos_lists(itemView: View, listModel: ListModel): RecyclerView.ViewHolder(itemView) {


    fun setDetails(name: String){
        val nameVH = itemView.findViewById<TextView>(R.id.name)
        val editable: Editable = SpannableStringBuilder(name)
        nameVH.text = editable
        itemView.findViewById<Button>(R.id.read).setOnClickListener {
            val intent = Intent(itemView.context, CosmosRead::class.java)
            intent.putExtra("title", name)
            itemView.context.startActivity(intent)
        }


        itemView.isClickable = true


        itemView.findViewById<MaterialCardView>(R.id.cardView_cosmos).setOnLongClickListener(){ view: View? ->
            val inflater = (itemView.context as Activity).getLayoutInflater()
            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(itemView.context)
            val dialoglayout = inflater.inflate(R.layout.cosmos_alert_delete_list, null)
            alertDialog.setView(dialoglayout)
            alertDialog.setCancelable(false)
            val show = alertDialog.show()
            val delete = dialoglayout.findViewById<Button>(R.id.delete).setOnClickListener {
                databaseReferenceLists.child(UID).child(name).removeValue()
                databaseReferenceListUsers.child(UID).child(name).removeValue()
                show.dismiss()

            }
            val leave = dialoglayout.findViewById<Button>(R.id.leave).setOnClickListener {
                show.dismiss()
            }
            true
        }



    }


}