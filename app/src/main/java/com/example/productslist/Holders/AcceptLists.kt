package com.example.productslist.Holders

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.*
import com.example.productslist.Models.AcceptModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.material.textfield.TextInputEditText

open class AcceptLists(itemView: View, val acceptModel: AcceptModel, val mainActivity: MainActivity): RecyclerView.ViewHolder(itemView){

    fun setDetails(id:String, name: String, buttonAccept: String, buttonDeny: String) {
        itemView.findViewById<TextView>(R.id.name).text = name
        itemView.findViewById<Button>(R.id.accept).setOnClickListener {
            databaseReferenceLists.child(UID).child(name).get()
                .addOnSuccessListener {
                    if (it.value != null) {
                        mainActivity.alertHaveAccept(name, id)

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