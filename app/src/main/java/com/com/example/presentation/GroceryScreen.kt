package com.com.example.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.com.example.presentation.components.AddItemCard
import com.com.example.presentation.components.EditItemDialog
import com.com.example.presentation.components.EmptyState
import com.com.example.presentation.components.FiltersRow
import com.com.example.presentation.components.GroceryListItem
import com.com.example.presentation.viewstate.GroceryUiState
import com.com.example.presentation.viewmodel.GroceryEvent

@Composable
fun GroceryScreen(
    state: GroceryUiState,
    paddingValues: PaddingValues,
    onEvent: (GroceryEvent) -> Unit
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
                onNameChanged = { onEvent(GroceryEvent.NameChanged(it)) },
                onCategorySelected = { onEvent(GroceryEvent.CategorySelected(it)) },
                onAddItemClicked = { onEvent(GroceryEvent.AddItemClicked) }
            )

            Spacer(Modifier.height(8.dp))

            FiltersRow(
                statusFilter = state.statusFilter,
                categoryFilter = state.categoryFilter,
                sortOption = state.sortOption,
                onStatusFilterSelected = { onEvent(GroceryEvent.StatusFilterSelected(it)) },
                onCategoryFilterSelected = { onEvent(GroceryEvent.CategoryFilterSelected(it)) },
                onSortOptionSelected = { onEvent(GroceryEvent.SortOptionSelected(it)) }
            )

            if (state.items.isEmpty()) {
                EmptyState()
            } else {
                val listState = rememberLazyListState()
                LaunchedEffect(state.statusFilter, state.categoryFilter, state.sortOption) {
                    listState.animateScrollToItem(0)
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        GroceryListItem(
                            item = item,
                            onToggleCompleted = { onEvent(GroceryEvent.ToggleCompletedClicked(item.id)) },
                            onDelete = { onEvent(GroceryEvent.DeleteItemClicked(item.id)) },
                            onEdit = { onEvent(GroceryEvent.EditItemRequested(item.id)) }
                        )
                    }
                }
            }
        }
    }

    state.editingItem?.let { editing ->
        EditItemDialog(
            name = state.nameInput,
            selectedCategory = state.selectedCategory,
            onNameChanged = { onEvent(GroceryEvent.NameChanged(it)) },
            onCategorySelected = { onEvent(GroceryEvent.CategorySelected(it)) },
            onDismiss = { onEvent(GroceryEvent.EditDismissed) },
            onConfirm = {
                onEvent(GroceryEvent.EditConfirmed(state.nameInput, state.selectedCategory))
            }
        )
    }
}

