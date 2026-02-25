package com.com.example.presentation.model

import com.com.example.domain.model.GroceryCategory

data class GroceryItemUiModel(
    val id: Long,
    val name: String,
    val category: GroceryCategory,
    val isCompleted: Boolean,
    val createdAt: Long
)