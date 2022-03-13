package com.hackunica.sharefood

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hackunica.sharefood.databinding.ActivityCreateBinding
import com.hackunica.sharefood.models.Donacion
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateBinding
    private lateinit var database: DatabaseReference
    private lateinit var donationId : String
    private var imageUrl = ""


    // Variables para obtener y cambiar de las imagenes de perfil
    val TAKE_IMG_CODE = 1046
    lateinit var vista: View
    lateinit var storageChild: String
    lateinit var databaseChild: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        binding = ActivityCreateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = Firebase.database.reference
        donationId = database.push().key.toString()

        binding.btnDonationPost.setOnClickListener {
            val address = binding.etDonationAddress.text.toString()
            val info = binding.etDonationInfo.text.toString()

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val date = current.format(formatter)

            postDonation(donationId, address, info, "Disponible", date)
        }

        binding.cardIvCreateDonation.setOnClickListener {
            changeImg()
        }
    }

    fun postDonation(
        id: String,
        address: String,
        info: String,
        status: String,
        date: String
    ){
        val donation = Donacion(id,address, info, status, date, imageUrl)
        database.child("Donations").child(id).setValue(donation)
        Toast.makeText(this, "Tu donativo ha sido publicado! ", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun changeImg(): Boolean {

        vista = binding.ivCreateDonation
        databaseChild = "imgProfile"
        storageChild = "profileImages"

        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        if (intent.resolveActivity(baseContext.packageManager) != null) {
            startActivityForResult(intent, TAKE_IMG_CODE)
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMG_CODE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(baseContext.contentResolver, data?.data)

                    binding.ivCreateDonation.setImageBitmap(bitmap)

                    handleUpload(bitmap)
                }
            }
        }
    }

    private fun handleUpload(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val uid: String = donationId
        val ref: StorageReference = FirebaseStorage.getInstance().reference
            .child(storageChild)
            .child("$uid.jpeg")

        ref.putBytes(baos.toByteArray())
            .addOnSuccessListener {
                getDownloadUrl(ref)
            }
            .addOnFailureListener() {
                Log.e("Errorimg", "onFailure", it.cause)
            }
    }

    private fun getDownloadUrl(ref: StorageReference) {
        ref.downloadUrl.addOnSuccessListener {
            setUserProfileUrl(it)
        }
    }

    private fun setUserProfileUrl(uri: Uri) {
        imageUrl = uri.toString()
        database.child("Donations").child(donationId).child("image").setValue(uri.toString())
        loadImg()
    }

    private fun loadImg() {

        database.child("Donations").child(donationId).child("image").get().addOnSuccessListener {
            val urlImg = it.value.toString()
            print("UrlImg: $urlImg")

            try {
                Glide.with(this)
                    .load(urlImg)
                    .transform(CenterCrop())
                    .into(binding.ivCreateDonation)
                //Picasso.get().load(urlImg).into(binding.rivImageProfile)

            } catch (e: Exception) {

            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }

}