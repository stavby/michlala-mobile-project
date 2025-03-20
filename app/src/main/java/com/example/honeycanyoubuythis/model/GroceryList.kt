package com.example.honeycanyoubuythis.model

import com.google.firebase.firestore.DocumentId

data class GroceryList(
    @DocumentId val id: String = "",
    val title: String = "",
    val itemCount: Int = 0,
)