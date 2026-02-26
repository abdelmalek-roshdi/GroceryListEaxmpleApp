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
        coEvery { repository.updateItem(any()) } returns Unit
        val item = GroceryItem(1L, "Eggs", GroceryCategory.Milk, false, 0L)

        val result = useCase(item.copy(name = "  Free-range Eggs  "))

        assertTrue(result.isSuccess)
        coVerify {
            repository.updateItem(match { updated ->
                updated.id == 1L && updated.name == "Free-range Eggs"
            })
        }
    }

    @Test
    fun invoke_emptyTrimmedName_returnsFailureAndDoesNotCallRepository() = runBlocking {
        val item = GroceryItem(2L, "Bread", GroceryCategory.Breads, false, 0L)

        val result = useCase(item.copy(name = "   "))

        assertTrue(result.isFailure)
        result.fold(
            onSuccess = { throw AssertionError("Expected failure") },
            onFailure = { assertTrue(it is IllegalArgumentException) }
        )
        coVerify(exactly = 0) { repository.updateItem(any()) }
    }

    @Test
    fun invoke_repositoryThrows_returnsFailure() = runBlocking {
        coEvery { repository.updateItem(any()) } throws RuntimeException("DB error")
        val item = GroceryItem(3L, "Milk", GroceryCategory.Milk, false, 0L)

        val result = useCase(item)

        assertTrue(result.isFailure)
    }
}
