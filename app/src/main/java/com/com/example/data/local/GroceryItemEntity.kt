package com.com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItemEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val category: String,
    val isCompleted: Boolean,
    val createdAt: Long
)

