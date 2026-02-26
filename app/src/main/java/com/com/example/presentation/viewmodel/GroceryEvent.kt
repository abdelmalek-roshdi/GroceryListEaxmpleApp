package com.com.example.presentation.viewmodel

import com.com.example.domain.model.GroceryCategory
import com.com.example.presentation.model.SortOption
import com.com.example.presentation.model.StatusFilter

sealed class GroceryEvent {
    data class NameChanged(val value: String) : GroceryEvent()
    data class CategorySelected(val category: GroceryCategory) : GroceryEvent()
    data class StatusFilterSelected(val filter: StatusFilter) : GroceryEvent()
    data class CategoryFilterSelected(val category: GroceryCategory?) : GroceryEvent()
    data class SortOptionSelected(val option: SortOption) : GroceryEvent()
    object AddItemClicked : GroceryEvent()
    data class ToggleCompletedClicked(val itemId: Long) : GroceryEvent()
    data class DeleteItemClicked(val itemId: Long) : GroceryEvent()
    data class EditItemRequested(val itemId: Long) : GroceryEvent()
    object EditDismissed : GroceryEvent()
    data class EditConfirmed(val name: String, val category: GroceryCategory) : GroceryEvent()
}

