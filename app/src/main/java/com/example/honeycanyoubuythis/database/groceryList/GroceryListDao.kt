package com.example.honeycanyoubuythis.database.groceryList

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryListDao {
    @Query("SELECT * FROM grocery_list_table")
    fun getAllGroceryLists(): Flow<List<LocalGroceryList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groceryLists: List<LocalGroceryList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryList(groceryLists: LocalGroceryList)
}