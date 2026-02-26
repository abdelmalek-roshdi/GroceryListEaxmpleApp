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

class DeleteGroceryItemUseCaseTest {

    private val repository = mockk<GroceryRepository>(relaxed = true)
    private val useCase = DeleteGroceryItemUseCase(repository)

    @Test
    fun invoke_callsRepositoryDeleteWithSameItem() = runBlocking {
        coEvery { repository.deleteItem(any()) } returns Unit
        val item = GroceryItem(1L, "Tomatoes", GroceryCategory.Vegetables, false, 0L)

        val result = useCase(item)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deleteItem(item) }
    }

    @Test
    fun invoke_repositoryThrows_returnsFailure() = runBlocking {
        coEvery { repository.deleteItem(any()) } throws RuntimeException("DB error")
        val item = GroceryItem(2L, "Milk", GroceryCategory.Milk, false, 0L)

        val result = useCase(item)

        assertTrue(result.isFailure)
    }
}
