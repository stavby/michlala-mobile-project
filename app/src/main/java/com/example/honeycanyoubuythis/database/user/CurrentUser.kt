package com.example.honeycanyoubuythis.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "current_user")
@TypeConverters(ProfilePictureTypeConverter::class)
data class CurrentUser(
    @PrimaryKey val id: Int = 1,
    val email: String,
    val displayName: String,
    var profilePicture: ByteArray? = null
)