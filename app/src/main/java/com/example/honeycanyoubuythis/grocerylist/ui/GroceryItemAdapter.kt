package com.example.honeycanyoubuythis.grocerylist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.honeycanyoubuythis.database.groceryList.GroceryItem
import com.example.honeycanyoubuythis.databinding.GroceryItemBinding

class GroceryItemAdapter :
    ListAdapter<GroceryItem, GroceryItemAdapter.GroceryItemViewHolder>(GroceryItemDiffCallback()) {

    class GroceryItemViewHolder(private val binding: GroceryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(groceryItem: GroceryItem) {
            binding.itemName.text = groceryItem.name
            binding.itemCount.text = groceryItem.amount.toString()
            if (groceryItem.isChecked) {
                binding.itemCheckBox.isChecked = true
            } else {
                binding.itemCheckBox.isChecked = false
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
        holder.bind(groceryItem)
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