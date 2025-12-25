package com.example.locationsharingapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingapp.databinding.ActivitySignUpBinding
import com.example.locationsharingapp.viewmodel.AuthViewModel
import com.example.locationsharingapp.viewmodel.FirestoreViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var fireStoreViewModel: FirestoreViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        fireStoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmailSignUp.text.toString()
            val password = binding.etPasswordSignUp.text.toString()
            val displayName =
                binding.etFirstName.text.toString() + " " + binding.etLastName.text.toString()

            authViewModel.signUp(
                email,
                password,
                onSuccess = {
                    fireStoreViewModel.saveUser(
                        authViewModel.getCurrentUserId(),
                        displayName,
                        email
                    )
                    startActivity(Intent(this, FriendListActivity::class.java))
                    finish()
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.tvGoToSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        if(authViewModel.getCurrentUserId().isNotEmpty()) {
            startActivity(Intent(this, FriendListActivity::class.java))
            finish()
        }
    }
}
