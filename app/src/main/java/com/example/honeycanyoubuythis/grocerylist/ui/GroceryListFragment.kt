package com.example.honeycanyoubuythis.grocerylist.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.databinding.GroceryListFragmentBinding
import com.example.honeycanyoubuythis.databinding.HomeFragmentBinding
import com.example.honeycanyoubuythis.home.ui.GroceryListViewModel
import com.example.honeycanyoubuythis.home.ui.GroceryListViewModelFactory
import com.example.honeycanyoubuythis.model.GroceryList
import kotlinx.coroutines.launch

class GroceryListFragment(
    private val groceryList: GroceryList,
) : Fragment() {
    private var _binding: GroceryListFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroceryListAdapter
    private lateinit var groceryListViewModel: GroceryListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroceryListFragmentBinding.inflate(inflater, container, false)
        val appDatabase = AppDatabase.getInstance(requireContext())
        val groceryListDao = appDatabase.groceryListDao()
        val groceryListRepository = GroceryListRepository(groceryListDao)
        val factory = GroceryListViewModelFactory(groceryListRepository, groceryList)
        groceryListViewModel = ViewModelProvider(this, factory)[GroceryListViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GroceryListAdapter(groceryList)

        with(binding) {
            groceryListRecyclerView.adapter = adapter
            groceryListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            fabAddItem.setOnClickListener {
                showAddItemDialog()
            }
        }

        observeItems()
    }

    private fun observeItems() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                groceryListViewModel.groceryList.collect { groceryList ->
                    adapter.updateData(groceryList)
                }
            }
        }
    }

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add New Item")

        val nameInput = EditText(requireContext()).apply {
            hint = "Item Name"
            inputType = InputType.TYPE_CLASS_TEXT
        }
        builder.setView(nameInput)

        val amountInput = EditText(requireContext()).apply {
            hint = "Item Amount"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        builder.setView(amountInput)

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

private fun Bundle.putParcelable(s: String, groceryList: GroceryList) {

}
