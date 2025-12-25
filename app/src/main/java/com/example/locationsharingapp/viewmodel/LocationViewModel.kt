package com.example.locationsharingapp.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient

class LocationViewModel(application: Application): AndroidViewModel(application) {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun initClient(client: FusedLocationProviderClient) {
        fusedLocationClient = client
    }
    
    fun getLastLocation(callback: (Double?, Double?) -> Unit) {
        try {
            fusedLocationClient?.let { client ->
                val context = getApplication<Application>().applicationContext
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    callback(null, null)
                    return
                }

                client.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val lastLocation = task.result
                        callback(lastLocation.latitude, lastLocation.longitude)
                    } else {
                        callback(null, null)
                    }
                }
            } ?: callback(null, null)
        } catch (e: SecurityException) {
            callback(null, null)
        }
    }

}
