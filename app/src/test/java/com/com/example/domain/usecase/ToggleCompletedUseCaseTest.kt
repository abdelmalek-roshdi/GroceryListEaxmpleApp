package com.com.example.domain.usecase

import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.repository.GroceryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleCompletedUseCaseTest {

    private val repository = mockk<GroceryRepository>(relaxed = true)
    private val useCase = ToggleCompletedUseCase(repository)

    @Test
    fun invoke_incompleteItem_callsRepositoryWithCompletedItem() = runBlocking {
        // Given: repository updateItem succeeds
        coEvery { repository.updateItem(any()) } returns Unit
        val item = GroceryItem(1L, "Bread", GroceryCategory.Breads, isCompleted = false, 0L)

        // When: toggling completion
        val result = useCase(item)

        // Then: success and repository receives item with isCompleted = true
        assertTrue(result.isSuccess)
        coVerify {
            repository.updateItem(match { updated ->
                updated.id == 1L && updated.isCompleted && updated.name == "Bread"
            })
        }
    }

    @Test
    fun invoke_completedItem_callsRepositoryWithIncompleteItem() = runBlocking {
        // Given: repository updateItem succeeds
        coEvery { repository.updateItem(any()) } returns Unit
        val item = GroceryItem(2L, "Milk", GroceryCategory.Milk, isCompleted = true, 0L)

        // When: toggling completion
        val result = useCase(item)

        // Then: success and repository receives item with isCompleted = false
        assertTrue(result.isSuccess)
        coVerify {
            repository.updateItem(match { updated ->
                updated.id == 2L && !updated.isCompleted
            })
        }
    }

    @Test
    fun invoke_repositoryThrows_returnsFailure() = runBlocking {
        // Given: repository throws on updateItem
        coEvery { repository.updateItem(any()) } throws RuntimeException("DB error")
        val item = GroceryItem(3L, "Eggs", GroceryCategory.Milk, false, 0L)

        // When: toggling completion
        val result = useCase(item)

        // Then: result is failure
        assertTrue(result.isFailure)
    }
}
