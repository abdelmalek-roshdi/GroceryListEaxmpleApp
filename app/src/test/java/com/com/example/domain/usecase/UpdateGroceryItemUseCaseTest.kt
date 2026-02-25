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

class UpdateGroceryItemUseCaseTest {

    private val repository = mockk<GroceryRepository>(relaxed = true)
    private val useCase = UpdateGroceryItemUseCase(repository)

    @Test
    fun invoke_validItem_callsRepositoryWithTrimmedName() = runBlocking {
        // Given: repository updateItem succeeds
        coEvery { repository.updateItem(any()) } returns Unit
        val item = GroceryItem(1L, "Eggs", GroceryCategory.Milk, false, 0L)

        // When: updating with surrounding spaces in name
        val result = useCase(item.copy(name = "  Free-range Eggs  "))

        // Then: success and repository receives trimmed name
        assertTrue(result.isSuccess)
        coVerify {
            repository.updateItem(match { updated ->
                updated.id == 1L && updated.name == "Free-range Eggs"
            })
        }
    }

    @Test
    fun invoke_emptyTrimmedName_returnsFailureAndDoesNotCallRepository() = runBlocking {
        // Given: an item with non-empty name
        val item = GroceryItem(2L, "Bread", GroceryCategory.Breads, false, 0L)

        // When: updating with all-whitespace name
        val result = useCase(item.copy(name = "   "))

        // Then: failure with IllegalArgumentException, updateItem not called
        assertTrue(result.isFailure)
        result.fold(
            onSuccess = { throw AssertionError("Expected failure") },
            onFailure = { assertTrue(it is IllegalArgumentException) }
        )
        coVerify(exactly = 0) { repository.updateItem(any()) }
    }

    @Test
    fun invoke_repositoryThrows_returnsFailure() = runBlocking {
        // Given: repository throws on updateItem
        coEvery { repository.updateItem(any()) } throws RuntimeException("DB error")
        val item = GroceryItem(3L, "Milk", GroceryCategory.Milk, false, 0L)

        // When: updating the item
        val result = useCase(item)

        // Then: result is failure
        assertTrue(result.isFailure)
    }
}
