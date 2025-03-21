package com.example.honeycanyoubuythis.grocerylist.ui

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.databinding.GroceryListFragmentBinding
import com.example.honeycanyoubuythis.grocerylist.viewmodel.GroceryListViewModel
import com.example.honeycanyoubuythis.grocerylist.viewmodel.GroceryListViewModelFactory
import com.example.honeycanyoubuythis.model.GroceryList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.text.isNotEmpty
import kotlin.text.toIntOrNull
import kotlin.text.trim

class GroceryListFragment : Fragment() {
    private var _binding: GroceryListFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: GroceryListFragmentArgs by navArgs()

    private lateinit var adapter: GroceryItemAdapter
    private lateinit var groceryListViewModel: GroceryListViewModel
    private lateinit var groceryList: GroceryList

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroceryListFragmentBinding.inflate(inflater, container, false)
        val appDatabase = AppDatabase.getInstance(requireContext())
        val groceryListDao = appDatabase.groceryListDao()
        val groceryListRepository = GroceryListRepository(groceryListDao)
        groceryList = args.groceryList
        val factory = GroceryListViewModelFactory(groceryListRepository, groceryList.id)
        groceryListViewModel = ViewModelProvider(this, factory)[GroceryListViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GroceryItemAdapter()

        with(binding) {
            groceryListRecyclerView.adapter = adapter
            groceryListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            fabAddItem.setOnClickListener {
                showAddItemDialog()
            }
        }

        observeGroceryItems()
    }

    private fun observeGroceryItems() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                groceryListViewModel.groceryList.collectLatest { uiState ->
                    adapter.submitList(uiState.items)
                }
            }
        }
    }

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Item")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.add_item_dialog, null)
        builder.setView(view)

        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val amountInput = view.findViewById<EditText>(R.id.amountInput)

        builder.setPositiveButton("Add") { dialog, _ ->
            val itemName = nameInput.text.toString().trim()
            val itemAmount = amountInput.text.toString().toIntOrNull() ?: 1

            if (itemName.isNotEmpty()) {
                groceryListViewModel.addGroceryItem(itemName, itemAmount)
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