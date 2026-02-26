package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.model.SortOption
import com.com.example.domain.model.StatusFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterAndSortGroceryItemsUseCaseTest {

    private val useCase = FilterAndSortGroceryItemsUseCase()

    private val baseItems = listOf(
        GroceryItem(
            id = 1,
            name = "Bananas",
            category = GroceryCategory.Fruits,
            isCompleted = false,
            createdAt = 10L
        ),
        GroceryItem(
            id = 2,
            name = "Apples",
            category = GroceryCategory.Fruits,
            isCompleted = true,
            createdAt = 20L
        ),
        GroceryItem(
            id = 3,
            name = "Milk",
            category = GroceryCategory.Milk,
            isCompleted = false,
            createdAt = 30L
        )
    )

    @Test
    fun `status All returns all items sorted by createdAt desc`() {
        val result = useCase(
            items = baseItems,
            statusFilter = StatusFilter.All,
            categoryFilter = null,
            sortOption = SortOption.CreatedAt
        )

        assertEquals(listOf(3L, 2L, 1L), result.map { it.id })
    }

    @Test
    fun `status Active filters out completed items`() {
        val result = useCase(
            items = baseItems,
            statusFilter = StatusFilter.Active,
            categoryFilter = null,
            sortOption = SortOption.CreatedAt
        )

        // Only items 1 and 3 are active, sorted by createdAt desc
        assertEquals(listOf(3L, 1L), result.map { it.id })
    }

    @Test
    fun `status Completed returns only completed items`() {
        val result = useCase(
            items = baseItems,
            statusFilter = StatusFilter.Completed,
            categoryFilter = null,
            sortOption = SortOption.CreatedAt
        )

        assertEquals(listOf(2L), result.map { it.id })
    }

    @Test
    fun `category filter narrows items to given category`() {
        val result = useCase(
            items = baseItems,
            statusFilter = StatusFilter.All,
            categoryFilter = GroceryCategory.Fruits,
            sortOption = SortOption.CreatedAt
        )

        // Only fruit items 1 and 2, sorted by createdAt desc
        assertEquals(listOf(2L, 1L), result.map { it.id })
    }

    @Test
    fun `alphabetical sort orders items by name case insensitive`() {
        val result = useCase(
            items = baseItems,
            statusFilter = StatusFilter.All,
            categoryFilter = null,
            sortOption = SortOption.Alphabetical
        )

        assertEquals(listOf("Apples", "Bananas", "Milk"), result.map { it.name })
    }

    @Test
    fun `status and category filters can be combined`() {
        val items = baseItems + GroceryItem(
            id = 4,
            name = "Oranges",
            category = GroceryCategory.Fruits,
            isCompleted = false,
            createdAt = 40L
        )

        val result = useCase(
            items = items,
            statusFilter = StatusFilter.Active,
            categoryFilter = GroceryCategory.Fruits,
            sortOption = SortOption.CreatedAt
        )

        // Active fruits are items 1 and 4, sorted by createdAt desc
        assertEquals(listOf(4L, 1L), result.map { it.id })
    }
}

