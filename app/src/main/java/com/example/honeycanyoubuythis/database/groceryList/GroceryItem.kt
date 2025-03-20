package com.example.honeycanyoubuythis.database.groceryList

data class GroceryItem(
    val name: String = "",
    val amount: Int = 0,
    var isChecked: Boolean = false,
)