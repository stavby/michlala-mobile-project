package com.example.honeycanyoubuythis.model

import com.example.honeycanyoubuythis.database.groceryList.GroceryItem
import com.google.firebase.firestore.DocumentId
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroceryList(
    @DocumentId val id: String = "",
    val title: String = "",
    val itemCount: Int = 0,
    val items: List<GroceryItem> = emptyList()
) : Parcelable