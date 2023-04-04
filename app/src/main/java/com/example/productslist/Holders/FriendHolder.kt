package com.example.productslist.Holders

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.*
import com.example.productslist.Models.User
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

open class FriendHolder(itemView: View, user: User, friends: Friends) :
    RecyclerView.ViewHolder(itemView) {

    fun setDetails(id: String, image: String, theme: String, username: String, friends: Activity) {
        val profileImageUser = itemView.findViewById<CircleImageView>(R.id.image)

        itemView.findViewById<TextView>(R.id.username).text = username
        Log.i("mage", username)
        if (image != "") {
            Picasso.get().load(image).placeholder(R.drawable.baseline_account_circle_24_green)
                .into(profileImageUser)
        }


    }

}


open class AcceptsRequestHolder(itemView: View, user: User) : RecyclerView.ViewHolder(itemView) {

    fun setDetails(profileImage: String, username: String, theme: String, id: String) {


        databaseReferenceUsers.child(id).child("username").get().addOnSuccessListener {
            itemView.findViewById<TextView>(R.id.username).text = it.value.toString()
        }
        itemView.findViewById<Button>(R.id.accept).setOnClickListener {

            databaseReferenceUsers.child(UID).child("username").get()
                .addOnSuccessListener { task1 ->

                    databaseReferenceRequestsFriend.child((task1.value.toString())).child(id)
                        .removeValue()

                    databaseReferenceUsers.child(id).child("username").get()
                        .addOnSuccessListener { task2 ->

                            databaseReferenceFriends.child((task2.value.toString()))
                                .child(task1.value.toString()).child(UID).setValue("friend")
                            databaseReferenceUsers.child(UID).child("image").get()
                                .addOnSuccessListener {
                                    databaseReferenceFriends.child((task2.value.toString()))
                                        .child(task1.value.toString()).child("image")
                                        .setValue(it.value.toString())

                                }


                            databaseReferenceUsers.child(UID).child("username").get()
                                .addOnSuccessListener {
                                    databaseReferenceFriends.child((task2.value.toString()))
                                        .child(task1.value.toString()).child("username")
                                        .setValue(it.value.toString())
                                }




                            databaseReferenceFriends.child(task1.value.toString())
                                .child(task2.value.toString()).child(id).setValue("friend")

                            databaseReferenceUsers.child(id).child("image").get()
                                .addOnSuccessListener {
                                    databaseReferenceFriends.child(task1.value.toString())
                                        .child((task2.value.toString())).child("image")
                                        .setValue(it.value.toString())

                                }


                            databaseReferenceUsers.child(id).child("username").get()
                                .addOnSuccessListener {
                                    databaseReferenceFriends.child(task1.value.toString())
                                        .child((task2.value.toString())).child("username")
                                        .setValue(it.value.toString())
                                }
                        }

                }

        }
        itemView.findViewById<Button>(R.id.deny).setOnClickListener {
            databaseReferenceUsers.child(UID).child("username").get()
                .addOnSuccessListener { task1 ->
                    databaseReferenceRequestsFriend.child((task1.value.toString())).child(id)
                        .removeValue()
                }
        }
    }


}