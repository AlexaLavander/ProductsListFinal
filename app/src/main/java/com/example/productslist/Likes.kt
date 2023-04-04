package com.example.productslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.CosmosTheme.CosmosNotificationsHolder
import com.example.productslist.CosmosTheme.NatificationModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class Likes: AppCompatActivity() {
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<NatificationModel, LikessHolder>
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.likes_leaves_page)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        recyclerView = findViewById(R.id.recyclerView_notifications)

        showData()
        firebaseRecyclerAdapter.startListening()
        firebaseRecyclerAdapter.notifyDataSetChanged()

    }

    private fun showData() {
        val options =  FirebaseRecyclerOptions.Builder<NatificationModel>()
            .setQuery(databaseReferenceLikes.child(UID), NatificationModel::class.java).build()

        firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<NatificationModel, LikessHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): LikessHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.likes_leaves, parent, false)

                return LikessHolder(itemView, NatificationModel())
            }

            override fun onBindViewHolder(
                holder: LikessHolder,
                position: Int,
                model: NatificationModel
            ) {
                holder.setDetails(
                    model.username,
                    model.imageUser,
                    model.imageReceipt
                )
            }

        }
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = firebaseRecyclerAdapter
    }
}