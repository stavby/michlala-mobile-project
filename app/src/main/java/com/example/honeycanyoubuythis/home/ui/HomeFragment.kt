package com.example.honeycanyoubuythis.home.ui

import android.app.AlertDialog
import android.util.Log
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
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.database.groceryList.GroceryListRepository
import com.example.honeycanyoubuythis.databinding.HomeFragmentBinding
import com.example.honeycanyoubuythis.model.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

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
        fetchWeather()
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

    private fun fetchWeather() {
        lifecycleScope.launch {
            try {
                val weatherData = withContext(Dispatchers.IO) {
                    getWeather()
                }
                updateWeatherUI(weatherData)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error fetching weather", e)
            }
        }
    }

    private fun updateWeatherUI(weatherResponse: WeatherResponse?) {
        if (weatherResponse != null) {
            val tempC = weatherResponse.current.tempC
            val conditionText = weatherResponse.current.condition.text

            val goodWeatherText = getString(R.string.good_weather_text)
            val badWeatherText = getString(R.string.bad_weather_text)

            val weatherString = "The weather is $tempC°C, $conditionText"

            val updatedWeatherString = if (tempC < 10 || conditionText.contains("rain", ignoreCase = true)) {
                "$badWeatherText\n$weatherString"
            } else {
                "$goodWeatherText\n$weatherString"
            }

            binding.WeatherText.text = updatedWeatherString
        } else {
            Log.e("HomeFragment", "Weather data is null")
        }
    }

    private fun getWeather(): WeatherResponse? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(getString(R.string.weather_api_url))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Network Error: ${response.code}")

            val responseBody = response.body?.string() ?: return null
            val gson = Gson()
            return gson.fromJson(responseBody, WeatherResponse::class.java)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}