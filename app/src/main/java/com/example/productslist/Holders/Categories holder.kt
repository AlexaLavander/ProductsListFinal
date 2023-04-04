package com.example.productslist.Holders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.Models.CategoryModel
import com.example.productslist.Models.ProductModel
import com.example.productslist.R
import com.example.productslist.UID
import com.example.productslist.databaseReferenceLists
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

open class CategoriesHolder(itemView: View, val categoryModel: CategoryModel) :
    RecyclerView.ViewHolder(itemView) {
    val linearLayoutManager = LinearLayoutManager(itemView.context)

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ProductModel, ProductHolder>
    val recyclerView = itemView.findViewById<RecyclerView>(R.id.products)


    @SuppressLint("MissingInflatedId")
    fun setDetails(ctx: Context?, category: String, extra: String) {
        itemView.findViewById<TextView>(R.id.category).text = category
        val inflater = (itemView.context as Activity).getLayoutInflater()


        itemView.setOnClickListener {
            if (itemView.findViewById<RecyclerView>(R.id.products).visibility == GONE)
                itemView.findViewById<RecyclerView>(R.id.products).visibility = VISIBLE
            else {
                itemView.findViewById<RecyclerView>(R.id.products).visibility = GONE
            }
        }

        deleteList(category, extra)

        itemView.setOnLongClickListener() { view: View? ->
            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(itemView.context)
            val dialoglayout = inflater.inflate(R.layout.delete_category, null)
            alertDialog.setView(dialoglayout)
            alertDialog.setCancelable(false)
            val show = alertDialog.show()
            val delete = dialoglayout.findViewById<Button>(R.id.delete).setOnClickListener {
                databaseReferenceLists.child(UID).child(extra).child(category).removeValue()
                databaseReferenceLists.child(UID).child(extra).child("categories").child(category).removeValue()
                show.dismiss()
            }
            val leave = dialoglayout.findViewById<Button>(R.id.leave).setOnClickListener {
                show.dismiss()
            }
            true
        }
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        val options = FirebaseRecyclerOptions.Builder<ProductModel>()
            .setQuery(
                databaseReferenceLists.child(UID).child(extra).child(category),
                ProductModel::class.java
            )
            .build()

        showData(extra, options, category)
        firebaseRecyclerAdapter.notifyDataSetChanged()
    }

    private fun deleteList(category: String, extra: String) {
        databaseReferenceLists.child(extra).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseReferenceLists.child(UID).child(extra).child(category)
                    .get().addOnSuccessListener {
                        if (it.value.toString() == "null") {
                            databaseReferenceLists.child(UID).child(extra).child("categories")
                                .child(category)
                                .removeValue()
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    @SuppressLint("NotifyDataSetChanged")
    fun showData(extra: String, options: FirebaseRecyclerOptions<ProductModel>, category: String) {

        firebaseRecyclerAdapter =
            object :
                FirebaseRecyclerAdapter<ProductModel, ProductHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ProductHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.product, parent, false)

                    return ProductHolder(itemView, ProductModel())
                }

                override fun onBindViewHolder(
                    holder: ProductHolder,
                    position: Int,
                    model: ProductModel
                ) {
                    holder.setDetails(
                        itemView.context,
                        model.name,
                        extra,
                        category
                    )

                }

            }
        recyclerView.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
        recyclerView.layoutManager = linearLayoutManager
        firebaseRecyclerAdapter.notifyDataSetChanged()

    }

    private var mClickListener: ClickListener? = null
    fun setOnClickListener(clickListener: ClickListener) {
        mClickListener = clickListener
    }

    init {


        itemView.setOnClickListener() { view: View? ->
            mClickListener?.onItemClick(
                view, absoluteAdapterPosition
            )
        }

    }

    interface ClickListener {
        fun onItemClick(view: View?, position: Int) {

        }

        fun onItemLongClick(view: View?, position: Int) {
        }
    }


}
