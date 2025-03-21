package com.example.honeycanyoubuythis.database.groceryList

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroceryItem(
    val name: String = "",
    val amount: Int = 0,
    var isChecked: Boolean = false,
): Parcelable