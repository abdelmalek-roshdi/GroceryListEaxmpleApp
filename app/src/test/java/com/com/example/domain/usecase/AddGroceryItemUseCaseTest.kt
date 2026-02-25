package com.com.example.domain.usecase

import com.com.example.domain.model.AddItemResult
import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.repository.GroceryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddGroceryItemUseCaseTest {

    private val repository = mockk<GroceryRepository>(relaxed = true)
    private val useCase = AddGroceryItemUseCase(repository)

    @Test
    fun invoke_validName_callsRepositoryAndReturnsSuccess() = runBlocking {
        // Given: repository addItem succeeds (no throw)
        coEvery { repository.addItem(any()) } returns Unit

        // When: adding a valid item
        val result = useCase("Apples", GroceryCategory.Fruits)

        // Then: result is success and repository was called with item having trimmed name
        assertTrue(result is AddItemResult.Success)
        coVerify {
            repository.addItem(match { item ->
                item.name == "Apples" &&
                        item.category == GroceryCategory.Fruits &&
                        !item.isCompleted
            })
        }
    }

    @Test
    fun invoke_trimmedName_storesTrimmedName() = runBlocking {
        // Given: repository addItem succeeds
        coEvery { repository.addItem(any()) } returns Unit

        // When: adding an item with surrounding spaces
        val result = useCase("  Milk  ", GroceryCategory.Milk)

        // Then: repository receives item with trimmed name
        assertTrue(result is AddItemResult.Success)
        coVerify {
            repository.addItem(match { it.name == "Milk" })
        }
    }

    @Test
    fun invoke_emptyName_returnsValidationErrorAndDoesNotCallRepository() = runBlocking {
        // Given: use case with mocked repository
        // When: invoking with blank/empty name
        val result = useCase("   ", GroceryCategory.Milk)

        // Then: validation error and addItem was not called
        assertTrue(result is AddItemResult.ValidationError)
        assertEquals(
            "Item name cannot be empty.",
            (result as AddItemResult.ValidationError).message
        )
        coVerify(exactly = 0) { repository.addItem(any()) }
    }

    @Test
    fun invoke_repositoryThrows_returnsFailure() = runBlocking {
        // Given: repository throws on addItem
        coEvery { repository.addItem(any()) } throws RuntimeException("DB error")

        // When: adding a valid item
        val result = useCase("Bread", GroceryCategory.Breads)

        // Then: result is Failure with the throwable
        assertTrue(result is AddItemResult.Failure)
        (result as AddItemResult.Failure).let {
            assertTrue(it.throwable is RuntimeException)
            assertEquals("DB error", it.throwable.message)
        }
    }
}
