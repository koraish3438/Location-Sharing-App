package com.example.locationsharingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.locationsharingapp.model.AppUser
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun saveUser(userId: String, displayName: String, email: String) {
        val user = hashMapOf(
            "userId" to userId,
            "displayName" to displayName,
            "userEmail" to email,
            "latitude" to null,
            "longitude" to null
        )
        usersCollection.document(userId).set(user)
            .addOnSuccessListener { }
            .addOnFailureListener { e -> }
    }

    fun getAllUsers(callback: (List<AppUser>) -> Unit) {
        usersCollection.get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<AppUser>()
                for (document in result) {
                    val userId = document.id
                    val displayName = document.getString("displayName") ?: ""
                    val email = document.getString("userEmail") ?: ""
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    userList.add(AppUser(userId, email, displayName, latitude, longitude))
                }
                callback(userList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun updateUser(userId: String, displayName: String) {
        val userMap = hashMapOf(
            "displayName" to displayName
        )
        usersCollection.document(userId).update(userMap as Map<String, Any>)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    fun updateUserLocation(userId: String, latitude: Double, longitude: Double) {
        if(userId.isEmpty()) return
        val userMap = hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
        usersCollection.document(userId).update(userMap as Map<String, Any>)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    fun getUser(userId: String, callback: (AppUser?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(AppUser::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
