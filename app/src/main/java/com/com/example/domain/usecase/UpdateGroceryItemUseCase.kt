package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import javax.inject.Inject

class UpdateGroceryItemUseCase @Inject constructor(
    private val repository: GroceryRepository
) {
    suspend operator fun invoke(item: GroceryItem): Result<Unit> {
        val trimmed = item.name.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Item name cannot be empty."))
        }
        val updated = item.copy(name = trimmed)
        return runCatching { repository.updateItem(updated) }
    }
}

