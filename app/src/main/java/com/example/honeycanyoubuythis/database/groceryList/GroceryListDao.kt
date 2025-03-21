package com.example.honeycanyoubuythis.database.groceryList

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(GroceryListTypeConverters::class)
interface GroceryListDao {
    @Query("SELECT * FROM grocery_list")
    fun getGroceryLists(): Flow<List<LocalGroceryList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllGroceryLists(groceryLists: List<LocalGroceryList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroceryList(groceryLists: LocalGroceryList)

    @Transaction
    @Query("SELECT * FROM grocery_list WHERE id = :groceryListId")
    suspend fun getGroceryListWithItems(groceryListId: String): LocalGroceryList?

    @Transaction
    @Query("SELECT * FROM grocery_list WHERE id = :groceryListId")
    fun getFlowGroceryListWithItems(groceryListId: String): Flow<LocalGroceryList>

    @Transaction
    suspend fun addItemToGroceryList(groceryListId: String, groceryItem: GroceryItem) {
        val groceryList = getGroceryListWithItems(groceryListId)
        groceryList?.let {
            val updatedGroceryItems = it.groceryItems.toMutableList()
            updatedGroceryItems.add(groceryItem)
            addGroceryList(it.copy(groceryItems = updatedGroceryItems))
        }
    }

    @Transaction
    suspend fun removeItemFromGroceryList(groceryListId: String, groceryItem: GroceryItem) {
        val groceryList = getGroceryListWithItems(groceryListId)
        groceryList?.let {
            val updatedGroceryItems = it.groceryItems.toMutableList()
            updatedGroceryItems.remove(groceryItem)
            addGroceryList(it.copy(groceryItems = updatedGroceryItems))
        }
    }

    @Transaction
    suspend fun updateItemInGroceryList(groceryListId: String, groceryItem: GroceryItem) {
        val groceryList = getGroceryListWithItems(groceryListId)
        groceryList?.let {
            val updatedGroceryItems = it.groceryItems.toMutableList()
            val index = updatedGroceryItems.indexOfFirst { it.id == groceryItem.id }
            if (index != -1) {
                updatedGroceryItems[index] = groceryItem
                addGroceryList(it.copy(groceryItems = updatedGroceryItems))
            }
        }
    }
}