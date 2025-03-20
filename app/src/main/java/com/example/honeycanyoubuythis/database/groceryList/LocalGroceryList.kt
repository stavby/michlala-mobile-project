package com.example.honeycanyoubuythis.database.groceryList

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "grocery_list_table")
@TypeConverters(GroceryListTypeConverters::class)
data class LocalGroceryList(
    @PrimaryKey val id: String,
    val title: String,
    val groceryItems: List<GroceryItem>
)