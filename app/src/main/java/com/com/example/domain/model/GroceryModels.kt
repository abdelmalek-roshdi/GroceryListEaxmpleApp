package com.com.example.domain.model

enum class GroceryCategory(val displayName: String) {
    Milk("Milk"),
    Vegetables("Vegetables"),
    Fruits("Fruits"),
    Breads("Breads"),
    Meats("Meats")
}

data class GroceryItem(
    val id: Long,
    val name: String,
    val category: GroceryCategory,
    val isCompleted: Boolean,
    val createdAt: Long
)

