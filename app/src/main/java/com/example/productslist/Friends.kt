package com.example.productslist

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.Holders.AcceptsRequestHolder
import com.example.productslist.Holders.FriendHolder
import com.example.productslist.Models.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textfield.TextInputEditText

class Friends : AppCompatActivity() {
    var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<User, FriendHolder>? = null
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAccept: RecyclerView
    var firebaseRecyclerAdapterAccept: FirebaseRecyclerAdapter<User, AcceptsRequestHolder>? = null

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManagerAccept: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        linearLayoutManagerAccept = LinearLayoutManager(this)
        linearLayoutManagerAccept.stackFromEnd = true
        linearLayoutManagerAccept.reverseLayout = true

        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewAccept = findViewById(R.id.recyclerView_accept)
        databaseReferenceUsers.child(UID).child("username").get().addOnSuccessListener {
            val options = FirebaseRecyclerOptions.Builder<User>().setQuery(
                databaseReferenceRequestsFriend.child(it.value.toString()), User::class.java
            ).build()

            showDataAccept(options)
            firebaseRecyclerAdapterAccept!!.startListening()

        }
        databaseReferenceUsers.child(UID).child("username").get().addOnSuccessListener {
            val options = FirebaseRecyclerOptions.Builder<User>().setQuery(
                databaseReferenceFriends.child(it.value.toString()), User::class.java
            ).build()
            showData(options)
            firebaseRecyclerAdapter!!.startListening()
        }

    }

    private fun showData(options: FirebaseRecyclerOptions<User>) {
        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<User, FriendHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.friend, parent, false)

                    return FriendHolder(itemView, User(), this@Friends)
                }

                override fun onBindViewHolder(
                    holder: FriendHolder,
                    position: Int,
                    model: User
                ) {
                    holder.setDetails(
                        model.id,
                        model.image,
                        model.theme,
                        model.username,
                        Friends()
                    )
                }

            }
        recyclerView.adapter = firebaseRecyclerAdapter
        recyclerView.layoutManager = linearLayoutManager
    }

    private fun showDataAccept(options: FirebaseRecyclerOptions<User>) {

        firebaseRecyclerAdapterAccept =
            object : FirebaseRecyclerAdapter<User, AcceptsRequestHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): AcceptsRequestHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.accept_friend, parent, false)
                    return AcceptsRequestHolder(itemView, User())
                }

                override fun onBindViewHolder(
                    holder: AcceptsRequestHolder,
                    position: Int,
                    model: User
                ) {
                    holder.setDetails(
                        model.image,
                        model.username,
                        model.theme,
                        model.id
                    )
                }

            }
        recyclerViewAccept.adapter = firebaseRecyclerAdapterAccept
        recyclerViewAccept.layoutManager = linearLayoutManagerAccept
    }
}