package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroceryItemsUseCase @Inject constructor(
    private val repository: GroceryRepository
) {
    operator fun invoke(): Flow<List<GroceryItem>> = repository.getItems()
}

