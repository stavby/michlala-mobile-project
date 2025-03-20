package com.example.honeycanyoubuythis.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.database.groceryList.GroceryList
import com.example.honeycanyoubuythis.databinding.GroceryListBinding

class GroceryListAdapter(private val groceryLists: List<GroceryList>) :
    RecyclerView.Adapter<GroceryListAdapter.GroceryListViewHolder>() {
    class GroceryListViewHolder(private val binding: GroceryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(groceryList: GroceryList) {
            binding.groceryListTitle.text = groceryList.title
            binding.groceryListItemCount.text =
                binding.root.context.getString(
                    R.string.item_count,
                    groceryList.itemCount
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryListViewHolder {
        val binding =
            GroceryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroceryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceryListViewHolder, position: Int) {
        val groceryList = groceryLists[position]
        holder.bind(groceryList)
    }

    override fun getItemCount(): Int = groceryLists.size
}