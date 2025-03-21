package com.example.honeycanyoubuythis.database.groceryList

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroceryItem(
    @PrimaryKey var id: String = "",
    val name: String = "",
    val amount: Int = 0,
    var isChecked: Boolean = false,
    val userId: String = ""
): Parcelable