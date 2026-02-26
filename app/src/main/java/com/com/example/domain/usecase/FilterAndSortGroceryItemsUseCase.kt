package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.model.SortOption
import com.com.example.domain.model.StatusFilter
import javax.inject.Inject

class FilterAndSortGroceryItemsUseCase @Inject constructor() {

    operator fun invoke(
        items: List<GroceryItem>,
        statusFilter: StatusFilter,
        categoryFilter: GroceryCategory?,
        sortOption: SortOption
    ): List<GroceryItem> {
        var filtered = items
        when (statusFilter) {
            StatusFilter.All -> Unit
            StatusFilter.Active -> filtered = filtered.filter { !it.isCompleted }
            StatusFilter.Completed -> filtered = filtered.filter { it.isCompleted }
        }

        categoryFilter?.let { category ->
            filtered = filtered.filter { it.category == category }
        }

        val sorted = when (sortOption) {
            SortOption.CreatedAt -> filtered.sortedByDescending { it.createdAt }
            SortOption.Alphabetical -> filtered.sortedBy { it.name.lowercase() }
        }

        return sorted
    }
}

