package com.Ahmad_Kamran.i230622

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for displaying a list of User objects in a RecyclerView.
 * This adapter expects a layout file named 'item_user.xml'
 * containing R.id.profileImageView, R.id.usernameTextView, and R.id.fullNameTextView.
 */
class UserAdapter(
    private var users: List<User>,
    private val clickListener: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // These IDs must exist in your R.layout.item_user (or equivalent layout file)
        val profileImage: ImageView = view.findViewById(R.id.profileImageView)
        val username: TextView = view.findViewById(R.id.usernameTextView)
        val fullName: TextView = view.findViewById(R.id.fullNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Inflate the layout for a single user item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false) // Assumes item_user.xml is the layout file
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        // Construct the full name using the new firstName and lastName fields
        val userFullName = "${user.firstName} ${user.lastName}"

        holder.username.text = "@${user.username}"
        holder.fullName.text = userFullName // Display the constructed full name

        // TODO: In a real app, use a library like Glide or Picasso to load user.profileImage URL into holder.profileImage
        // For now, you might set a default placeholder image:
        // holder.profileImage.setImageResource(R.drawable.default_profile_icon)

        holder.itemView.setOnClickListener {
            clickListener(user)
        }
    }

    override fun getItemCount() = users.size

    /**
     * Updates the data set and notifies the RecyclerView of the change.
     */
    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}