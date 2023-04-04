package com.example.productslist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productslist.CosmosTheme.NatificationModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.hdodenhof.circleimageview.CircleImageView

class LikessHolder(itemVIew: View, notificationModel: NatificationModel) :
    RecyclerView.ViewHolder(itemVIew) {

    fun setDetails(username: String, imageUser: String, imageReceipt: String) {
        itemView.findViewById<TextView>(R.id.text).text =
            "Пользователю " + username + " понравился ваш рецепт"

        val userImage = itemView.findViewById<CircleImageView>(R.id.userImage)
        Picasso.get().load(imageUser).placeholder(R.drawable.baseline_account_circle_24_green)
            .into(userImage)

        val receiptImage = itemView.findViewById<ImageView>(R.id.receiptView)
        Picasso.get().load(imageReceipt).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                receiptImage.setBackground(BitmapDrawable(itemView.context.getResources(), bitmap))

            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        })
    }
}
