package com.com.example.domain.repository

import com.com.example.domain.model.GroceryItem
import kotlinx.coroutines.flow.Flow

interface GroceryRepository {
    fun getItems(): Flow<List<GroceryItem>>
    suspend fun addItem(item: GroceryItem)
    suspend fun updateItem(item: GroceryItem)
    suspend fun deleteItem(item: GroceryItem)
}

