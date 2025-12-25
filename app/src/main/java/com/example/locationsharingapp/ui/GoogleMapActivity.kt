package com.example.locationsharingapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingapp.R
import com.example.locationsharingapp.databinding.ActivityGoogleMapBinding
import com.example.locationsharingapp.model.AppUser
import com.example.locationsharingapp.viewmodel.FirestoreViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityGoogleMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fireStoreViewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        fireStoreViewModel.getAllUsers { userList ->
            val validUsers: List<AppUser> = userList.filter { it.latitude != null && it.longitude != null }

            for (user in validUsers) {
                val latLng = LatLng(user.latitude!!, user.longitude!!)
                val markerOption = MarkerOptions()
                    .position(latLng)
                    .title("${user.displayName}\n${user.userEmail}")
                googleMap.addMarker(markerOption)
            }

            if (validUsers.isNotEmpty()) {
                val firstUser = validUsers[0]
                val firstLatLng = LatLng(firstUser.latitude!!, firstUser.longitude!!)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 10f))
            }
        }
    }
}
