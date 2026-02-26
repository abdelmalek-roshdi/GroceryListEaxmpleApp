package com.com.example.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.SortOption
import com.com.example.domain.model.StatusFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersRow(
    statusFilter: StatusFilter,
    categoryFilter: GroceryCategory?,
    sortOption: SortOption,
    onStatusFilterSelected: (StatusFilter) -> Unit,
    onCategoryFilterSelected: (GroceryCategory?) -> Unit,
    onSortOptionSelected: (SortOption) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == StatusFilter.All,
                onClick = { onStatusFilterSelected(StatusFilter.All) },
                label = { Text("All") }
            )
            FilterChip(
                selected = statusFilter == StatusFilter.Active,
                onClick = { onStatusFilterSelected(StatusFilter.Active) },
                label = { Text("Active") }
            )
            FilterChip(
                selected = statusFilter == StatusFilter.Completed,
                onClick = { onStatusFilterSelected(StatusFilter.Completed) },
                label = { Text("Completed") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = categoryFilter == null,
                onClick = { onCategoryFilterSelected(null) },
                label = { Text("All categories") }
            )
            GroceryCategory.values().forEach { category ->
                FilterChip(
                    selected = categoryFilter == category,
                    onClick = { onCategoryFilterSelected(category) },
                    label = { Text(category.displayName) }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = sortOption == SortOption.CreatedAt,
                onClick = { onSortOptionSelected(SortOption.CreatedAt) },
                label = { Text("Newest") }
            )
            FilterChip(
                selected = sortOption == SortOption.Alphabetical,
                onClick = { onSortOptionSelected(SortOption.Alphabetical) },
                label = { Text("A → Z") }
            )
        }
    }
}

