package com.example.locationsharingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsharingapp.databinding.ItemFriendBinding
import com.example.locationsharingapp.model.AppUser

class FriendAdapter(private var userList: List<AppUser>): RecyclerView.Adapter<FriendAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemFriendBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.binding.tvName.text = currentUser.displayName
        holder.binding.tvEmail.text = currentUser.userEmail
        holder.binding.tvLocation.text =
            if (currentUser.latitude != null && currentUser.longitude != null) {
                "Lat: ${currentUser.latitude}, Lng: ${currentUser.longitude}"
            } else {
                "Location not available"
            }
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newList: List<AppUser>) {
        userList = newList
        notifyDataSetChanged()
    }
}