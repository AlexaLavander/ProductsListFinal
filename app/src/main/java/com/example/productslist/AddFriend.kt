package com.example.productslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.Holders.AddFriendsHolder
import com.example.productslist.Holders.FriendHolder
import com.example.productslist.Models.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class AddFriend : AppCompatActivity() {
    var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<User, AddFriendsHolder>? = null
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView = findViewById(R.id.recyclerView)
        showData()
        firebaseRecyclerAdapter!!.startListening()
    }

    private fun showData() {
        val options = FirebaseRecyclerOptions.Builder<User>().setQuery(
            databaseReferenceUsers, User::class.java
        ).build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<User, AddFriendsHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): AddFriendsHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.add_friend, parent, false)

                    return AddFriendsHolder(itemView, User())
                }

                override fun onBindViewHolder(
                    holder: AddFriendsHolder,
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
        recyclerView.adapter = firebaseRecyclerAdapter
        recyclerView.layoutManager = linearLayoutManager
    }
}