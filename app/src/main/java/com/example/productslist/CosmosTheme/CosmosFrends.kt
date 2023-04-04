package com.example.productslist.CosmosTheme

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.*
import com.example.productslist.Holders.AcceptsRequestHolder
import com.example.productslist.Holders.FriendHolder
import com.example.productslist.Models.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class CosmosFrends : AppCompatActivity() {
    var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<User, FriendHolder>? = null
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAccept: RecyclerView
    var firebaseRecyclerAdapterAccept: FirebaseRecyclerAdapter<User, AcceptsRequestHolder>? = null

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManagerAccept: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cosmos_friends)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        window.statusBarColor = getColor(R.color.cosmos)
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
                        .inflate(R.layout.cosmos_friend, parent, false)

                    return FriendHolder(itemView, User(), Friends())
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
                        Friends(),


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
                        .inflate(R.layout.cosmos_accept_frriend, parent, false)
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