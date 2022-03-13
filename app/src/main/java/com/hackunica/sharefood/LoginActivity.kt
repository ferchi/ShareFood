package com.hackunica.sharefood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hackunica.sharefood.databinding.ActivityLoginBinding
import com.hackunica.sharefood.databinding.ActivityMainBinding
import android.content.Intent




class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }
}