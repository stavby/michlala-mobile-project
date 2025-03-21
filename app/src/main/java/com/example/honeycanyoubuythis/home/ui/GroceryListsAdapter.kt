package com.example.honeycanyoubuythis.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.databinding.GroceryListBinding
import com.example.honeycanyoubuythis.model.GroceryList

class GroceryListsAdapter(private var groceryLists: List<GroceryList>) :
    RecyclerView.Adapter<GroceryListsAdapter.GroceryListViewHolder>() {
    class GroceryListViewHolder(private val binding: GroceryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(groceryList: GroceryList) {
            with(binding){
                groceryListTitle.text = groceryList.title
                groceryListItemCount.text =
                    root.context.getString(
                        R.string.item_count,
                        groceryList.itemCount
                    )
                root.setOnClickListener {
                   navigateToGroceryList(groceryList)
                }
            }
        }

        private fun navigateToGroceryList(groceryList: GroceryList) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToGroceryListFragment(groceryList)
            itemView.findNavController().navigate(action)
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

    fun updateData(newGroceryLists: List<GroceryList>) {
        groceryLists = newGroceryLists
        notifyDataSetChanged()
    }


}