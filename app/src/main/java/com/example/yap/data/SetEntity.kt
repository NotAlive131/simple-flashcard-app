package com.example.yap.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "SetTable")
data class SetEntity(
    val name: String,
    val description: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)
