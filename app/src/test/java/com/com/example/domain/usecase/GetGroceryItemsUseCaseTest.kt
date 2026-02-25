package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetGroceryItemsUseCaseTest {

    private val repository = mockk<GroceryRepository>()
    private val useCase = GetGroceryItemsUseCase(repository)

    @Test
    fun invoke_emitsItemsFromRepository() = runBlocking {
        // Given: repository returns a flow of two items
        val items = listOf(
            GroceryItem(1L, "Milk", GroceryCategory.Milk, false, 0L),
            GroceryItem(2L, "Bread", GroceryCategory.Breads, false, 1L)
        )
        every { repository.getItems() } returns flowOf(items)

        // When: use case is invoked and first emission is collected
        val result = useCase().first()

        // Then: the same list is emitted and getItems was called
        assertEquals(items, result)
        assertEquals(listOf("Milk", "Bread"), result.map { it.name })
        verify(exactly = 1) { repository.getItems() }
    }

    @Test
    fun invoke_emitsEmptyListWhenRepositoryHasNoItems() = runBlocking {
        // Given: repository returns an empty flow
        every { repository.getItems() } returns flowOf(emptyList())

        // When: use case is invoked
        val result = useCase().first()

        // Then: empty list is emitted
        assertEquals(emptyList<GroceryItem>(), result)
        verify(exactly = 1) { repository.getItems() }
    }
}
