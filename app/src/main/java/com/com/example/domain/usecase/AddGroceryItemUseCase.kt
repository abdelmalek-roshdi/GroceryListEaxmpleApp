package com.com.example.domain.usecase

import com.com.example.domain.model.AddItemResult
import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import javax.inject.Inject


class AddGroceryItemUseCase @Inject constructor(
    private val repository: GroceryRepository
) {
    suspend operator fun invoke(
        name: String,
        category: GroceryCategory
    ): AddItemResult {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            return AddItemResult.ValidationError(AddItemResult.ERROR_EMPTY_NAME)
        }
        val item = GroceryItem(
            id = System.currentTimeMillis(),
            name = trimmed,
            category = category,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )
        return runCatching { repository.addItem(item) }
            .fold(
                onSuccess = { AddItemResult.Success },
                onFailure = { AddItemResult.Failure(it) }
            )
    }
}

