package com.example.productslist.Holders

import android.annotation.SuppressLint
import android.net.Uri
import android.opengl.Visibility
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.*
import com.example.productslist.Models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

open class AddFriendsHolder(itemView: View, user: User) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    fun setDetails(profileImage: String, name: String, theme: String, id: String) {

        if (id == UID){
            itemView.visibility = View.GONE
            itemView.setLayoutParams(
                RecyclerView.LayoutParams(
                    0, 0
                )
            )
        }
        val profileImageUser = itemView.findViewById<CircleImageView>(R.id.image)
        Picasso.get().load(Uri.parse(profileImage))
            .placeholder(R.drawable.baseline_account_circle_24_cosmos).into(profileImageUser)
        itemView.findViewById<TextView>(R.id.username).text = name
        databaseReferenceUsers.child(UID).child("username").get().addOnSuccessListener {

            databaseReferenceFriends.child(it.value.toString()).child(name)
                .get().addOnSuccessListener {
                    if (it.value.toString() == "null") {
                        itemView.findViewById<Button>(R.id.send_request).setOnClickListener {
                            databaseReferenceUsers.child(UID).child("username").get()
                                .addOnSuccessListener {


                                    val dateMap: MutableMap<String, Any> = LinkedHashMap()

                                    dateMap["username"] = name
                                    dateMap["theme"] = theme
                                    dateMap["image"] = profileImage
                                    dateMap["id"] = UID
                                    databaseReferenceRequestsFriend.child(name).child(UID)
                                        .updateChildren(dateMap)
                                }

                        }
                    } else{
                        itemView.findViewById<Button>(R.id.send_request).setText("Friend")
                    }
                }
        }
    }
}