package com.hackunica.sharefood.models

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.animation.animpresseffect.PressEffectCardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hackunica.sharefood.R

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.hackunica.sharefood.InfoActivity


class DonationAdapter(private val donationList : ArrayList<Donacion>) : RecyclerView.Adapter <DonationAdapter.DonationViewHolder>(){
    lateinit var postId : String
    private lateinit var database: DatabaseReference
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_food_layout, parent, false)
        context = parent.context

        return DonationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val  currentItem = donationList[position]
        database = Firebase.database.reference

        holder.address.text = currentItem.address
        holder.info.text = currentItem.info
        holder.status.text = currentItem.status
        holder.date.text = currentItem.date
        postId = currentItem.id.toString()

        loadImg(holder.image)

        holder.cardInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity::class.java)
            intent.putExtra("address", currentItem.address)
            intent.putExtra("info", currentItem.info)
            intent.putExtra("status", currentItem.status)
            intent.putExtra("date", currentItem.date)
            intent.putExtra("id", postId)
            context.startActivity(intent);
        }
    }

    override fun getItemCount(): Int {
        return donationList.size
    }
    class DonationViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val address : TextView = itemView.findViewById(R.id.tv_address)
        val image : ImageView = itemView.findViewById(R.id.iv_item_food)
        val info : TextView =  itemView.findViewById(R.id.tv_info)
        val status : TextView = itemView.findViewById(R.id.tv_status)
        val date : TextView = itemView.findViewById(R.id.tv_date)
        val cardInfo: PressEffectCardView = itemView.findViewById(R.id.card_view_item)
    }

    private fun loadImg(view: ImageView) {

        database.child("Donations").child(postId).child("image").get().addOnSuccessListener {
            val urlImg = it.value.toString()
            print("UrlImg: $urlImg")

            try {
                Glide.with(context)
                    .load(urlImg)
                    .transform(CenterCrop())
                    .into(view)

            } catch (e: Exception) {

            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }
}