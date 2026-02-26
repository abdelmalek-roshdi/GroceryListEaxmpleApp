package com.com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.com.example.domain.model.AddItemResult
import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.model.SortOption
import com.com.example.domain.model.StatusFilter
import com.com.example.domain.usecase.AddGroceryItemUseCase
import com.com.example.domain.usecase.DeleteGroceryItemUseCase
import com.com.example.domain.usecase.FilterAndSortGroceryItemsUseCase
import com.com.example.domain.usecase.GetGroceryItemsUseCase
import com.com.example.domain.usecase.ToggleCompletedUseCase
import com.com.example.domain.usecase.UpdateGroceryItemUseCase
import com.com.example.presentation.model.GroceryItemUiModel
import com.com.example.presentation.viewstate.GroceryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceryListViewModel @Inject constructor(
    private val getItems: GetGroceryItemsUseCase,
    private val addItem: AddGroceryItemUseCase,
    private val updateItem: UpdateGroceryItemUseCase,
    private val deleteItem: DeleteGroceryItemUseCase,
    private val toggleCompleted: ToggleCompletedUseCase,
    private val filterAndSortItems: FilterAndSortGroceryItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroceryUiState(isLoading = true))
    val uiState: StateFlow<GroceryUiState> = _uiState

    private val _latestItems = MutableStateFlow<List<GroceryItem>>(emptyList())

    init {
        observeItemsAndFilterState()
    }

    fun onEvent(event: GroceryEvent) {
        when (event) {
            is GroceryEvent.NameChanged -> onNameChanged(event.value)
            is GroceryEvent.CategorySelected -> onCategorySelected(event.category)
            is GroceryEvent.StatusFilterSelected -> onStatusFilterSelected(event.filter)
            is GroceryEvent.CategoryFilterSelected -> onCategoryFilterSelected(event.category)
            is GroceryEvent.SortOptionSelected -> onSortOptionSelected(event.option)
            GroceryEvent.AddItemClicked -> onAddItemClicked()
            is GroceryEvent.ToggleCompletedClicked -> onToggleCompletedClicked(event.itemId)
            is GroceryEvent.DeleteItemClicked -> onDeleteItemClicked(event.itemId)
            is GroceryEvent.EditItemRequested -> onEditItemRequested(event.itemId)
            GroceryEvent.EditDismissed -> onEditDismissed()
            is GroceryEvent.EditConfirmed -> onEditConfirmed(event.name, event.category)
        }
    }

    private fun observeItemsAndFilterState() {
        viewModelScope.launch {
            combine(
                getItems(),
                _uiState
            ) { items, state ->
                val filteredAndSorted = filterAndSortItems(
                    items = items,
                    statusFilter = state.statusFilter,
                    categoryFilter = state.categoryFilter,
                    sortOption = state.sortOption
                )

                Pair(
                    items,
                    state.copy(
                        isLoading = false,
                        items = filteredAndSorted.map { it.toUi() }
                    )
                )
            }.collect { (items, newState) ->
                _latestItems.value = items
                val current = _uiState.value
                if (newState.items != current.items || newState.isLoading != current.isLoading) {
                    _uiState.value = newState
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
        _uiState.update { it.copy(statusFilter = filter) }
    }

    fun onCategoryFilterSelected(category: GroceryCategory?) {
        _uiState.update { it.copy(categoryFilter = category) }
    }

    fun onSortOptionSelected(option: SortOption) {
        _uiState.update { it.copy(sortOption = option) }
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
        val item = _latestItems.value.find { it.id == itemId } ?: return
        viewModelScope.launch {
            val result = toggleCompleted(item)
            showSnackbarForResult(result)
        }
    }

    fun onDeleteItemClicked(itemId: Long) {
        val item = _latestItems.value.find { it.id == itemId } ?: return
        viewModelScope.launch {
            val result = deleteItem(item)
            showSnackbarForResult(result, "delete")
        }
    }

    fun onEditItemRequested(itemId: Long) {
        val item = _latestItems.value.find { it.id == itemId } ?: return
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
            showSnackbarForResult(result)
        }
    }

    private fun showSnackbarForResult(result: Result<Unit>, text: String? = null) {
        _uiState.update { state ->
            if (result.isSuccess) {
                state.copy(
                    editingItem = null,
                    nameInput = "",
                    snackbarMessage = text?.let { "item ".plus(it.plus("d")) } ?: "Item updated"
                )
            } else {
                state.copy(snackbarMessage = "Could not".plus(text ?: "update").plus(" item"))
            }
        }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
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

