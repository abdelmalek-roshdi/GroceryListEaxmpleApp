package com.com.example.presentation.viewstate

import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.SortOption
import com.com.example.domain.model.StatusFilter
import com.com.example.presentation.model.GroceryItemUiModel

data class GroceryUiState(
    val items: List<GroceryItemUiModel> = emptyList(),
    val nameInput: String = "",
    val selectedCategory: GroceryCategory = GroceryCategory.Milk,
    val statusFilter: StatusFilter = StatusFilter.All,
    val categoryFilter: GroceryCategory? = null,
    val sortOption: SortOption = SortOption.CreatedAt,
    val isLoading: Boolean = false,
    val editingItem: GroceryItemUiModel? = null,
    val snackbarMessage: String? = null
)