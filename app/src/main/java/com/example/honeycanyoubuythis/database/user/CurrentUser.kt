package com.example.honeycanyoubuythis.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user")
data class CurrentUser(
    @PrimaryKey val id: Int = 1,
    val email: String,
    val displayName: String
)