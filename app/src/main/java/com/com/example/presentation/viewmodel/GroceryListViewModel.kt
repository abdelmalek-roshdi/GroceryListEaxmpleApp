package com.com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.com.example.domain.model.AddItemResult
import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.usecase.AddGroceryItemUseCase
import com.com.example.domain.usecase.DeleteGroceryItemUseCase
import com.com.example.domain.usecase.GetGroceryItemsUseCase
import com.com.example.domain.usecase.ToggleCompletedUseCase
import com.com.example.domain.usecase.UpdateGroceryItemUseCase
import com.com.example.presentation.model.GroceryItemUiModel
import com.com.example.presentation.model.SortOption
import com.com.example.presentation.model.StatusFilter
import com.com.example.presentation.viewstate.GroceryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceryListViewModel @Inject constructor(
    private val getItems: GetGroceryItemsUseCase,
    private val addItem: AddGroceryItemUseCase,
    private val updateItem: UpdateGroceryItemUseCase,
    private val deleteItem: DeleteGroceryItemUseCase,
    private val toggleCompleted: ToggleCompletedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroceryUiState(isLoading = true))
    val uiState: StateFlow<GroceryUiState> = _uiState

    private var latestItems: List<GroceryItem> = emptyList()

    init {
        observeItems()
    }

    private fun observeItems() {
        viewModelScope.launch {
            getItems().collect { items ->
                latestItems = items
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        items = applyFiltersAndSort(items, state)
                    )
                }
            }
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(nameInput = value) }
    }

    fun onCategorySelected(category: GroceryCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onStatusFilterSelected(filter: StatusFilter) {
        _uiState.update { state ->
            val newState = state.copy(statusFilter = filter)
            newState.copy(items = applyFiltersAndSort(latestItems, newState))
        }
    }

    fun onCategoryFilterSelected(category: GroceryCategory?) {
        _uiState.update { state ->
            val newState = state.copy(categoryFilter = category)
            newState.copy(items = applyFiltersAndSort(latestItems, newState))
        }
    }

    fun onSortOptionSelected(option: SortOption) {
        _uiState.update { state ->
            val newState = state.copy(sortOption = option)
            newState.copy(items = applyFiltersAndSort(latestItems, newState))
        }
    }

    fun onAddItemClicked() {
        val current = _uiState.value
        viewModelScope.launch {
            when (val result = addItem(current.nameInput, current.selectedCategory)) {
                is AddItemResult.Success -> {
                    _uiState.update {
                        it.copy(
                            nameInput = "",
                            snackbarMessage = "Item added"
                        )
                    }
                }

                is AddItemResult.ValidationError -> {
                    _uiState.update { it.copy(snackbarMessage = result.message) }
                }

                is AddItemResult.Failure -> {
                    _uiState.update { it.copy(snackbarMessage = "Could not add item") }
                }
            }
        }
    }

    fun onToggleCompletedClicked(itemId: Long) {
        val item = latestItems.find { it.id == itemId } ?: return
        viewModelScope.launch {
            toggleCompleted(item)
        }
    }

    fun onDeleteItemClicked(itemId: Long) {
        val item = latestItems.find { it.id == itemId } ?: return
        viewModelScope.launch {
            deleteItem(item)
        }
    }

    fun onEditItemRequested(itemId: Long) {
        val item = latestItems.find { it.id == itemId } ?: return
        _uiState.update { state ->
            state.copy(
                editingItem = item.toUi(),
                nameInput = item.name,
                selectedCategory = item.category
            )
        }
    }

    fun onEditDismissed() {
        _uiState.update { it.copy(editingItem = null, nameInput = "") }
    }

    fun onEditConfirmed(newName: String, newCategory: GroceryCategory) {
        val editing = _uiState.value.editingItem ?: return
        viewModelScope.launch {
            val domainItem = GroceryItem(
                id = editing.id,
                name = newName,
                category = newCategory,
                isCompleted = editing.isCompleted,
                createdAt = editing.createdAt
            )
            val result = updateItem(domainItem)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        editingItem = null,
                        nameInput = "",
                        snackbarMessage = "Item updated"
                    )
                } else {
                    it.copy(snackbarMessage = "Could not update item")
                }
            }
        }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun applyFiltersAndSort(
        items: List<GroceryItem>,
        state: GroceryUiState
    ): List<GroceryItemUiModel> {
        var filtered = items
        when (state.statusFilter) {
            StatusFilter.All -> Unit
            StatusFilter.Active -> filtered = filtered.filter { !it.isCompleted }
            StatusFilter.Completed -> filtered = filtered.filter { it.isCompleted }
        }
        state.categoryFilter?.let { category ->
            filtered = filtered.filter { it.category == category }
        }
        val sorted = when (state.sortOption) {
            SortOption.CreatedAt -> filtered.sortedByDescending { it.createdAt }
            SortOption.Alphabetical -> filtered.sortedBy { it.name.lowercase() }
        }
        return sorted.map { it.toUi() }
    }

    private fun GroceryItem.toUi(): GroceryItemUiModel =
        GroceryItemUiModel(
            id = id,
            name = name,
            category = category,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
}

