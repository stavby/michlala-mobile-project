package com.example.honeycanyoubuythis.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrentUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: CurrentUser)

    @Query("SELECT * FROM current_user WHERE id = 1")
    suspend fun getCurrentUser(): CurrentUser?

    @Query("DELETE FROM current_user")
    suspend fun deleteCurrentUser()
}