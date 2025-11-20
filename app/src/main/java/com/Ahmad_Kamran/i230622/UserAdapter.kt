package com.Ahmad_Kamran.i230622

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(private val users: List<SeventhActivity.User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.userProfileImage)
        val username: TextView = itemView.findViewById(R.id.usernameTextView)
        val fullName: TextView = itemView.findViewById(R.id.fullNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = user.username
        holder.fullName.text = user.fullName

        // Load profile image using Glide or use placeholder
        Glide.with(holder.itemView.context)
            .load(user.profileImage)
            .placeholder(R.drawable.face1)
            .circleCrop()
            .into(holder.profileImage)
    }

    override fun getItemCount(): Int = users.size
}
