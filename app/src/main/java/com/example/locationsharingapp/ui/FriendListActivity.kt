package com.example.locationsharingapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationsharingapp.R
import com.example.locationsharingapp.adapter.FriendAdapter
import com.example.locationsharingapp.databinding.ActivityFriendListBinding
import com.example.locationsharingapp.model.AppUser
import com.example.locationsharingapp.viewmodel.AuthViewModel
import com.example.locationsharingapp.viewmodel.FirestoreViewModel
import com.example.locationsharingapp.viewmodel.LocationViewModel
import com.google.firebase.auth.FirebaseAuth

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userAdapter: FriendAdapter

    private val authViewModel: AuthViewModel by viewModels()
    private val fireStoreViewModel: FirestoreViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) getLocation()
        else Toast.makeText(this, "Location permission required!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        setupDrawer()
        setupRecyclerView()
        checkLocationPermission()
    }

    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> startActivity(Intent(this, MyProfileActivity::class.java))
                R.id.menu_map -> startActivity(Intent(this, GoogleMapActivity::class.java))
                R.id.menu_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupRecyclerView() {
        userAdapter = FriendAdapter(emptyList())
        binding.rvFriendList.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@FriendListActivity)
        }

        fireStoreViewModel.getAllUsers { users: List<AppUser> ->
            userAdapter.updateData(users)
        }
    }

    private fun checkLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getLocation() {
        locationViewModel.getLastLocation { latitude, longitude ->
            val userId = authViewModel.getCurrentUserId()
            if (!userId.isNullOrEmpty() && latitude != null && longitude != null) {
                fireStoreViewModel.updateUserLocation(userId, latitude, longitude)
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }
}
