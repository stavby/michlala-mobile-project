package com.example.honeycanyoubuythis.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.honeycanyoubuythis.database.groceryList.GroceryList
import com.example.honeycanyoubuythis.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groceryLists = listOf(
            GroceryList(1, "Weekly Groceries", 10),
            GroceryList(2, "Party Supplies", 5),
            GroceryList(3, "Baking Essentials", 8),
            GroceryList(4, "Snacks", 12),
            GroceryList(5, "Beverages", 6),
            GroceryList(6, "Dairy Products", 7),
        )

        val adapter = GroceryListAdapter(groceryLists)
        binding.groceryListsRecyclerView.adapter = adapter
        binding.groceryListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}