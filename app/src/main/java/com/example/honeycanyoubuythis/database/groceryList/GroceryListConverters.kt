package com.example.honeycanyoubuythis.database.groceryList

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class GroceryListTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromGroceryItemList(value: List<GroceryItem>): String {
        val type = object : TypeToken<List<GroceryItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGroceryItemList(value: String): List<GroceryItem> {
        val type = object : TypeToken<List<GroceryItem>>() {}.type
        return gson.fromJson(value, type)
    }
}