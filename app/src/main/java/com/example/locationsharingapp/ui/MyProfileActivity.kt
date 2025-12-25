package com.example.locationsharingapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingapp.databinding.ActivityMyProfileBinding
import com.example.locationsharingapp.viewmodel.AuthViewModel
import com.example.locationsharingapp.viewmodel.FirestoreViewModel
import com.google.firebase.auth.FirebaseAuth

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var fireStoreViewModel: FirestoreViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        fireStoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        binding.btnUpdateProfile.setOnClickListener {
            val newName = binding.etProfileName.text.toString()
            updateProfile(newName)
        }

        binding.ivHome.setOnClickListener {
            startActivity(Intent(this, FriendListActivity::class.java))
            finish()
        }

        binding.ivLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        loadUserInfo()
    }

    private fun updateProfile(newName: String) {
        val currentUser = authViewModel.getCurrentUserId()
        if (currentUser.isNotEmpty()) {
            fireStoreViewModel.updateUser(currentUser, newName)
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserInfo() {
        val currentUser = authViewModel.getCurrentUserId()
        if (currentUser.isNotEmpty()) {
            binding.etProfileEmail.setText(firebaseAuth.currentUser?.email)
            fireStoreViewModel.getUser(currentUser) { user ->
                if (user != null) {
                    binding.etProfileName.setText(user.displayName)
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }
}
