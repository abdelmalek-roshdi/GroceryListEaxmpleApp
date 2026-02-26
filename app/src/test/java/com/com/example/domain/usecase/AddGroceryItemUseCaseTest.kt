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
        coEvery { repository.addItem(any()) } returns Unit

        val result = useCase("Apples", GroceryCategory.Fruits)

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
        coEvery { repository.addItem(any()) } returns Unit

        val result = useCase("  Milk  ", GroceryCategory.Milk)

        assertTrue(result is AddItemResult.Success)
        coVerify {
            repository.addItem(match { it.name == "Milk" })
        }
    }

    @Test
    fun invoke_emptyName_returnsValidationErrorAndDoesNotCallRepository() = runBlocking {
        val result = useCase("   ", GroceryCategory.Milk)

        assertTrue(result is AddItemResult.ValidationError)
        assertEquals(
            "Item name cannot be empty.",
            (result as AddItemResult.ValidationError).message
        )
        coVerify(exactly = 0) { repository.addItem(any()) }
    }

    @Test
    fun invoke_repositoryThrows_returnsFailure() = runBlocking {
        coEvery { repository.addItem(any()) } throws RuntimeException("DB error")

        val result = useCase("Bread", GroceryCategory.Breads)

        assertTrue(result is AddItemResult.Failure)
        (result as AddItemResult.Failure).let {
            assertTrue(it.throwable is RuntimeException)
            assertEquals("DB error", it.throwable.message)
        }
    }
}
