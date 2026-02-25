package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import javax.inject.Inject

class ToggleCompletedUseCase @Inject constructor(
    private val repository: GroceryRepository
) {
    suspend operator fun invoke(item: GroceryItem): Result<Unit> {
        val toggled = item.copy(isCompleted = !item.isCompleted)
        return runCatching { repository.updateItem(toggled) }
    }
}

