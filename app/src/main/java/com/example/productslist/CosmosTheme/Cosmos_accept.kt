package com.example.productslist.CosmosTheme

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.*
import com.example.productslist.Models.AcceptModel

class Cosmos_accept(itemView: View, val acceptModel: AcceptModel, val cosmosTheme: CosmosTheme) : RecyclerView.ViewHolder(itemView) {

    fun setDetails(id:String, name: String, buttonAccept: String, buttonDeny: String) {
        itemView.findViewById<TextView>(R.id.name).text = name
        itemView.findViewById<Button>(R.id.accept).setOnClickListener {
            databaseReferenceLists.child(UID).child(name).get()
                .addOnSuccessListener {
                    if (it.value.toString() != "null") {
                        cosmosTheme.alertHaveAccept(name, id)

                    } else {
                        databaseReferenceRequests.child(UID).child(name).removeValue()
                        databaseReferenceLists.child(id).child(name).get().addOnSuccessListener {
                            Log.i("id", id)
                            databaseReferenceLists.child(UID).child(name).setValue(it.value)
                        }
                        databaseReferenceListUsers.child(UID).child(
                            name
                        ).child("list").setValue(name)


                    }

                }

        }
        itemView.findViewById<Button>(R.id.deny).setOnClickListener {
            databaseReferenceRequests.child(UID).child(name).removeValue()

        }
    }
}