package com.example.productslist.CosmosTheme

import android.app.NotificationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.R
import com.example.productslist.UID
import com.example.productslist.databaseReferenceLikes
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import io.reactivex.rxjava3.core.Notification

class CosmosLikes: AppCompatActivity() {

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<NatificationModel, CosmosNotificationsHolder>
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.likes)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        window.statusBarColor = getColor(R.color.cosmos)
        recyclerView = findViewById(R.id.recyclerView_notifications)

        showData()
        firebaseRecyclerAdapter.startListening()

        firebaseRecyclerAdapter.notifyDataSetChanged()

    }

    private fun showData() {
        val options =  FirebaseRecyclerOptions.Builder<NatificationModel>()
            .setQuery(databaseReferenceLikes.child(UID), NatificationModel::class.java).build()

        firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<NatificationModel, CosmosNotificationsHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): CosmosNotificationsHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notifications, parent, false)

                return CosmosNotificationsHolder(itemView, NatificationModel())
            }

            override fun onBindViewHolder(
                holder: CosmosNotificationsHolder,
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
