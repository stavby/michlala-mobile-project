package com.example.honeycanyoubuythis.grocerylist.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.database.groceryList.GroceryItem
import com.example.honeycanyoubuythis.databinding.GroceryItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class GroceryItemAdapter(private val listener: GroceryItemListener) :
    ListAdapter<GroceryItem, GroceryItemAdapter.GroceryItemViewHolder>(GroceryItemDiffCallback()) {

    class GroceryItemViewHolder(private val binding: GroceryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroceryItem, listener: GroceryItemListener) {
            binding.apply {
                itemName.text = item.name
                itemCount.text = item.amount.toString()
                itemCheckBox.isChecked = item.isChecked
                itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    listener.onItemCheckChanged(item, isChecked)
                }

                if (item.userId.isNotEmpty()){
                    try{
                        val db = FirebaseFirestore.getInstance()
                        db.collection("user").document(item.userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val imageUrl = document.getString("profilePictureUrl")
                                    if (imageUrl != null) {
                                        Glide.with(itemView.context)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_person_baseline)
                                            .error(R.drawable.ic_person_baseline)
                                            .circleCrop()
                                            .into(userProfileImageView)
                                    } else {
                                        userProfileImageView.setImageResource(R.drawable.ic_person_baseline)
                                    }
                                }
                            }
                    } catch (e: Exception){
                        Log.e("GroceryItemAdapter", "Error loading user profile picture", e)
                    }
                } else {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_person_baseline)
                        .placeholder(R.drawable.ic_person_baseline)
                        .error(R.drawable.ic_person_baseline)
                        .circleCrop()
                        .into(userProfileImageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemViewHolder {
        val binding =
            GroceryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroceryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceryItemViewHolder, position: Int) {
        val groceryItem = getItem(position)
        holder.bind(groceryItem, listener)
    }

    class GroceryItemDiffCallback : DiffUtil.ItemCallback<GroceryItem>() {
        override fun areItemsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
            return oldItem == newItem
        }
    }
}

interface GroceryItemListener {
    fun onItemCheckChanged(item: GroceryItem, isChecked: Boolean)
}