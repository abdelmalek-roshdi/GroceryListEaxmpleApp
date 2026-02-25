package com.com.example.data.repository

import com.com.example.data.local.GroceryDao
import com.com.example.data.local.GroceryItemEntity
import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class GroceryRepositoryImpl(
    private val dao: GroceryDao
) : GroceryRepository {

    override fun getItems(): Flow<List<GroceryItem>> =
        dao.observeItems().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun addItem(item: GroceryItem) {
        dao.upsert(item.toEntity())
    }

    override suspend fun updateItem(item: GroceryItem) {
        dao.upsert(item.toEntity())
    }

    override suspend fun deleteItem(item: GroceryItem) {
        dao.delete(item.toEntity())
    }

    private fun GroceryItemEntity.toDomain(): GroceryItem =
        GroceryItem(
            id = id,
            name = name,
            category = runCatching {
                GroceryCategory.valueOf(
                    category
                )
            }
                .getOrDefault(GroceryCategory.Milk),
            isCompleted = isCompleted,
            createdAt = createdAt
        )

    private fun GroceryItem.toEntity(): GroceryItemEntity =
        GroceryItemEntity(
            id = id,
            name = name,
            category = category.name,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
}

