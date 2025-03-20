package com.example.honeycanyoubuythis.database.groceryList

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GroceryListTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromGroceryItemList(value: List<GroceryItem>?): String? {
        return if (value == null) {
            null
        } else {
            val type = object : TypeToken<List<GroceryItem>>() {}.type
            gson.toJson(value, type)
        }
    }

    @TypeConverter
    fun toGroceryItemList(value: String?): List<GroceryItem>? {
        return if (value == null) {
            null
        } else {
            val type = object : TypeToken<List<GroceryItem>>() {}.type
            gson.fromJson(value, type)
        }
    }
}