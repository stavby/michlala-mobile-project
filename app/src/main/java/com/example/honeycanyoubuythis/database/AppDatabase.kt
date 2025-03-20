package com.example.honeycanyoubuythis.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.honeycanyoubuythis.database.groceryList.GroceryListDao
import com.example.honeycanyoubuythis.database.groceryList.LocalGroceryList
import com.example.honeycanyoubuythis.database.user.CurrentUser
import com.example.honeycanyoubuythis.database.user.CurrentUserDao

@Database(entities = [CurrentUser::class, LocalGroceryList::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currentUserDao(): CurrentUserDao
    abstract fun groceryListDao(): GroceryListDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}