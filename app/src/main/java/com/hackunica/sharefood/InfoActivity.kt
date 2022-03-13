package com.hackunica.sharefood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hackunica.sharefood.databinding.ActivityCreateBinding
import com.hackunica.sharefood.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityInfoBinding

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        database = Firebase.database.reference

        binding = ActivityInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent;
        val address = intent.getStringExtra("address")
        val info = intent.getStringExtra("info")
        val status = intent.getStringExtra("status")
        val date = intent.getStringExtra("date")
        val postId = intent.getStringExtra("id")

        binding.tvInfoAddress.text = address
        binding.tvInfo.text = info
        binding.status.text = status
        binding.tvDate.text = date

        loadImg(binding.ivInfo, postId!!)
    }

    private fun loadImg(view: ImageView, postId:String) {

        database.child("Donations").child(postId).child("image").get().addOnSuccessListener {
            val urlImg = it.value.toString()
            print("UrlImg: $urlImg")

            try {
                Glide.with(this)
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