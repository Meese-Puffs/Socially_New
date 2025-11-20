package com.Ahmad_Kamran.i230622

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// NOTE: This adapter assumes you have a layout file named 'user_item.xml' in your res/layout
// containing a profileImageView, usernameTextView, and fullNameTextView.
class UserAdapter(
    private var users: List<User>,
    private val clickListener: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // You need to define R.layout.user_item and the IDs within it.
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // These IDs must exist in R.layout.user_item
        val profileImage: ImageView = view.findViewById(R.id.profileImageView)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val fullName: TextView = view.findViewById(R.id.fullNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.username.text = "@${user.username}"
        holder.fullName.text = user.fullName
        // In a real app, use a library like Glide or Picasso to load user.profileImage URL into holder.profileImage

        holder.itemView.setOnClickListener {
            clickListener(user)
        }
    }

    override fun getItemCount() = users.size

    // Efficient way to update the results list
    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}