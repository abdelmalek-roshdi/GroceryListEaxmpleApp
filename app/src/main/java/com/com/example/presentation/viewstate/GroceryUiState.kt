package com.com.example.presentation.viewstate

import com.com.example.domain.model.GroceryCategory
import com.com.example.presentation.model.GroceryItemUiModel
import com.com.example.presentation.model.SortOption
import com.com.example.presentation.model.StatusFilter

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