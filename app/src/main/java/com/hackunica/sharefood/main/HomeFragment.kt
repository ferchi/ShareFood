package com.hackunica.sharefood.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.hackunica.sharefood.R
import com.hackunica.sharefood.databinding.FragmentHomeBinding
import com.hackunica.sharefood.models.Donacion
import com.hackunica.sharefood.models.DonationAdapter


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbref : DatabaseReference
    private lateinit var donationsRecyclerView: RecyclerView
    private lateinit var donationsArrayList : ArrayList<Donacion>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        donationsRecyclerView = view.findViewById(R.id.rv_home)
        donationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        donationsRecyclerView.setHasFixedSize(true)

        donationsArrayList = arrayListOf<Donacion>()
        getUserData()
    }

    private fun getUserData(){
        dbref = FirebaseDatabase.getInstance().getReference("Donations")
        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    donationsArrayList.clear()
                    for(donationSnapshot in snapshot.children){
                        val donation = donationSnapshot.getValue(Donacion::class.java)
                        donationsArrayList.add(donation!!)
                    }

                    donationsRecyclerView.adapter = DonationAdapter(donationsArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}