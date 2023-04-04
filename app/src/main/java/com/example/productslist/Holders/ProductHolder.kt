package com.example.productslist.Holders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.Models.ProductModel
import com.example.productslist.R
import com.example.productslist.UID
import com.example.productslist.databaseReferenceLists

open class ProductHolder(itemView: View, val productModel: ProductModel = ProductModel()) :
    RecyclerView.ViewHolder(itemView) {


    @SuppressLint("MissingInflatedId")
    fun setDetails(ctx: Context?, name: String, extra: String, category: String) {

        itemView.findViewById<TextView>(R.id.productName).text = name


        itemView.setOnLongClickListener() { view: View? ->
            val inflater = (itemView.context as Activity).getLayoutInflater()
            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(itemView.context)
            val dialoglayout = inflater.inflate(R.layout.delete_alert, null)
            alertDialog.setView(dialoglayout)
            alertDialog.setCancelable(false)
            val show = alertDialog.show()
            val delete = dialoglayout.findViewById<Button>(R.id.delete).setOnClickListener {
                databaseReferenceLists.child(UID).child(extra).child(category).child(name)
                    .removeValue()

                show.dismiss()
            }
            val leave = dialoglayout.findViewById<Button>(R.id.leave).setOnClickListener {
                show.dismiss()
            }
            true
        }

        databaseReferenceLists.child(UID).child(extra).child(category).child(name).child("checkBox")
            .get()
            .addOnSuccessListener {
                if (it.value.toString() == "gotIt") {
                    itemView.findViewById<CheckBox>(R.id.check).isChecked = true
                }
            }
        itemView.findViewById<CheckBox>(R.id.check).setOnClickListener {
            databaseReferenceLists.child(UID).child(extra).child(category).child(name)
                .child("checkBox").get().addOnSuccessListener {
                    if (it.value.toString() == "gotIt") {
                        databaseReferenceLists.get().addOnSuccessListener {
                            for (ids in it.children) {
                                itemView.findViewById<CheckBox>(R.id.check).isChecked = true
                                databaseReferenceLists.child(ids.key.toString()).child(extra)
                                    .child(category)
                                    .child(name)
                                    .child("checkBox").removeValue()
                            }

                        }
                    } else {
                        databaseReferenceLists.get().addOnSuccessListener {
                            for (ids in it.children) {
                                databaseReferenceLists.child(ids.key.toString()).child(extra)
                                    .child(category)
                                    .child(name)
                                    .child("checkBox").setValue("gotIt")
                                itemView.findViewById<CheckBox>(R.id.check).isChecked = false

                            }
                        }
                    }
                }
        }

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