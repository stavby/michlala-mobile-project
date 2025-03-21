package com.example.honeycanyoubuythis.home.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.databinding.HomeFragmentBinding

import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroceryListsAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        val appDatabase = AppDatabase.getInstance(requireContext())
        val groceryListDao = appDatabase.groceryListDao()
        val groceryListRepository = GroceryListRepository(groceryListDao)
        val factory = HomeViewModelFactory(groceryListRepository)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GroceryListsAdapter(emptyList())

        with(binding) {
            groceryListsRecyclerView.adapter = adapter
            groceryListsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            fabAddGroceryList.setOnClickListener {
                showAddGroceryListDialog()
            }
        }

        observeGroceryLists()
    }

    private fun observeGroceryLists() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.groceryLists.collect { groceryLists ->
                    adapter.updateData(groceryLists)
                }
            }
        }
    }

    private fun showAddGroceryListDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Grocery List")

        val input = EditText(requireContext())
        input.hint = "Grocery List Name"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val listName = input.text.toString().trim()
            if (listName.isNotEmpty()) {
                homeViewModel.addGroceryList(listName)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}