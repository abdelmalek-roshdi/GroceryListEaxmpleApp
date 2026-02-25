package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import javax.inject.Inject

class DeleteGroceryItemUseCase @Inject constructor(
    private val repository: GroceryRepository
) {
    suspend operator fun invoke(item: GroceryItem): Result<Unit> =
        runCatching { repository.deleteItem(item) }
}

