package com.com.example.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.com.example.domain.model.GroceryCategory
import com.com.example.presentation.components.AddItemCard
import com.com.example.presentation.components.EditItemDialog
import com.com.example.presentation.components.EmptyState
import com.com.example.presentation.components.FiltersRow
import com.com.example.presentation.components.GroceryListItem
import com.com.example.presentation.model.SortOption
import com.com.example.presentation.model.StatusFilter
import com.com.example.presentation.viewstate.GroceryUiState

@Composable
fun GroceryScreen(
    state: GroceryUiState,
    paddingValues: PaddingValues,
    onNameChanged: (String) -> Unit,
    onCategorySelected: (GroceryCategory) -> Unit,
    onAddItemClicked: () -> Unit,
    onStatusFilterSelected: (StatusFilter) -> Unit,
    onCategoryFilterSelected: (GroceryCategory?) -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    onToggleCompletedClicked: (Long) -> Unit,
    onDeleteItemClicked: (Long) -> Unit,
    onEditItemRequested: (Long) -> Unit,
    onEditDismissed: () -> Unit,
    onEditConfirmed: (String, GroceryCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            AddItemCard(
                nameInput = state.nameInput,
                selectedCategory = state.selectedCategory,
                onNameChanged = onNameChanged,
                onCategorySelected = onCategorySelected,
                onAddItemClicked = onAddItemClicked
            )

            Spacer(Modifier.height(8.dp))

            FiltersRow(
                statusFilter = state.statusFilter,
                categoryFilter = state.categoryFilter,
                sortOption = state.sortOption,
                onStatusFilterSelected = onStatusFilterSelected,
                onCategoryFilterSelected = onCategoryFilterSelected,
                onSortOptionSelected = onSortOptionSelected
            )

            if (state.items.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        GroceryListItem(
                            item = item,
                            onToggleCompleted = { onToggleCompletedClicked(item.id) },
                            onDelete = { onDeleteItemClicked(item.id) },
                            onEdit = { onEditItemRequested(item.id) }
                        )
                    }
                }
            }
        }
    }

    state.editingItem?.let { editing ->
        EditItemDialog(
            item = editing,
            onDismiss = onEditDismissed,
            onConfirm = onEditConfirmed
        )
    }
}

