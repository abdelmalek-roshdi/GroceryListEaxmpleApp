package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.model.ItemSortOption
import com.com.example.domain.model.ItemStatusFilter
import javax.inject.Inject

class FilterAndSortGroceryItemsUseCase @Inject constructor() {

    operator fun invoke(
        items: List<GroceryItem>,
        statusFilter: ItemStatusFilter,
        categoryFilter: GroceryCategory?,
        sortOption: ItemSortOption
    ): List<GroceryItem> {
        var filtered = items
        when (statusFilter) {
            ItemStatusFilter.All -> Unit
            ItemStatusFilter.Active -> filtered = filtered.filter { !it.isCompleted }
            ItemStatusFilter.Completed -> filtered = filtered.filter { it.isCompleted }
        }

        categoryFilter?.let { category ->
            filtered = filtered.filter { it.category == category }
        }

        val sorted = when (sortOption) {
            ItemSortOption.CreatedAt -> filtered.sortedByDescending { it.createdAt }
            ItemSortOption.Alphabetical -> filtered.sortedBy { it.name.lowercase() }
        }

        return sorted
    }
}

