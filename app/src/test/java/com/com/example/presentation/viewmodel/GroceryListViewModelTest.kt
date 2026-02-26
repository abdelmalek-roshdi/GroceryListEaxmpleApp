package com.com.example.presentation.viewmodel

import app.cash.turbine.test
import com.com.example.domain.model.AddItemResult
import com.com.example.domain.model.GroceryCategory
import com.com.example.domain.model.GroceryItem
import com.com.example.domain.usecase.AddGroceryItemUseCase
import com.com.example.domain.usecase.DeleteGroceryItemUseCase
import com.com.example.domain.usecase.GetGroceryItemsUseCase
import com.com.example.domain.usecase.ToggleCompletedUseCase
import com.com.example.domain.usecase.UpdateGroceryItemUseCase
import com.com.example.presentation.model.SortOption
import com.com.example.presentation.model.StatusFilter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryListViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addItemClearsInputAndShowsSnackbar() = runTest {
        // Given: a ViewModel with empty list and an Add use case that succeeds
        val itemsFlow = MutableStateFlow<List<GroceryItem>>(emptyList())
        val getItems = mockk<GetGroceryItemsUseCase>()
        val addUseCase = mockk<AddGroceryItemUseCase>()
        val updateUseCase = mockk<UpdateGroceryItemUseCase>(relaxed = true)
        val deleteUseCase = mockk<DeleteGroceryItemUseCase>(relaxed = true)
        val toggleUseCase = mockk<ToggleCompletedUseCase>(relaxed = true)

        every { getItems.invoke() } returns itemsFlow
        coEvery { addUseCase.invoke(any(), any()) } returns AddItemResult.Success

        val viewModel = GroceryListViewModel(
            getItems = getItems,
            addItem = addUseCase,
            updateItem = updateUseCase,
            deleteItem = deleteUseCase,
            toggleCompleted = toggleUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial loading state
            awaitItem() // state after initial items collection

            // When: user types a name and taps Add
            viewModel.onNameChanged("Milk")
            val afterName = awaitItem()
            Assert.assertEquals("Milk", afterName.nameInput)

            viewModel.onAddItemClicked()
            advanceUntilIdle()

            // Then: input is cleared and success snackbar is shown
            val afterAdd = awaitItem()
            Assert.assertEquals("", afterAdd.nameInput)
            Assert.assertEquals("Item added", afterAdd.snackbarMessage)
        }
    }

    @Test
    fun addItemValidationErrorShowsErrorSnackbarAndKeepsInput() = runTest {
        // Given: a ViewModel and an Add use case that returns a validation error
        val itemsFlow = MutableStateFlow<List<GroceryItem>>(emptyList())
        val getItems = mockk<GetGroceryItemsUseCase>()
        val addUseCase = mockk<AddGroceryItemUseCase>()
        val updateUseCase = mockk<UpdateGroceryItemUseCase>(relaxed = true)
        val deleteUseCase = mockk<DeleteGroceryItemUseCase>(relaxed = true)
        val toggleUseCase = mockk<ToggleCompletedUseCase>(relaxed = true)

        every { getItems.invoke() } returns itemsFlow
        coEvery { addUseCase.invoke(any(), any()) } returns
                AddItemResult.ValidationError("Item name cannot be empty.")

        val viewModel = GroceryListViewModel(
            getItems = getItems,
            addItem = addUseCase,
            updateItem = updateUseCase,
            deleteItem = deleteUseCase,
            toggleCompleted = toggleUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial
            awaitItem() // after items collection

            // When: user enters whitespace-only text and taps Add
            viewModel.onNameChanged("   ")
            val afterName = awaitItem()
            Assert.assertEquals("   ", afterName.nameInput)

            viewModel.onAddItemClicked()
            advanceUntilIdle()

            // Then: input remains and error snackbar is shown
            val afterAdd = awaitItem()
            Assert.assertEquals("   ", afterAdd.nameInput)
            Assert.assertEquals("Item name cannot be empty.", afterAdd.snackbarMessage)
        }
    }

    @Test
    fun filtersAndSortingAppliedToItems() = runTest {
        // Given: a ViewModel with two fruit items in the repository
        val items = listOf(
            GroceryItem(1, "Bananas", GroceryCategory.Fruits, false, 1),
            GroceryItem(2, "Apples", GroceryCategory.Fruits, false, 2)
        )
        val itemsFlow = MutableStateFlow(items)
        val getItems = mockk<GetGroceryItemsUseCase>()
        val addUseCase = mockk<AddGroceryItemUseCase>(relaxed = true)
        val updateUseCase = mockk<UpdateGroceryItemUseCase>(relaxed = true)
        val deleteUseCase = mockk<DeleteGroceryItemUseCase>(relaxed = true)
        val toggleUseCase = mockk<ToggleCompletedUseCase>(relaxed = true)

        every { getItems.invoke() } returns itemsFlow

        val viewModel = GroceryListViewModel(
            getItems = getItems,
            addItem = addUseCase,
            updateItem = updateUseCase,
            deleteItem = deleteUseCase,
            toggleCompleted = toggleUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial loading state
            val initialAfterCollect = awaitItem()
            // default sort is Newest (createdAt desc): Apples(2) then Bananas(1)
            Assert.assertEquals(
                listOf("Apples", "Bananas"),
                initialAfterCollect.items.map { it.name })

            // When: user changes sorting to Alphabetical
            viewModel.onSortOptionSelected(SortOption.Alphabetical)
            advanceUntilIdle()

            // Then: items remain in correct alphabetical order
            val afterSort = awaitItem()
            Assert.assertEquals(listOf("Apples", "Bananas"), afterSort.items.map { it.name })
        }
    }

    @Test
    fun statusFilterCompletedShowsOnlyCompletedItems() = runTest {
        // Given: a ViewModel with one completed and one active item
        val items = listOf(
            GroceryItem(1, "Milk", GroceryCategory.Milk, isCompleted = true, createdAt = 1),
            GroceryItem(
                2,
                "Carrots",
                GroceryCategory.Vegetables,
                isCompleted = false,
                createdAt = 2
            )
        )
        val itemsFlow = MutableStateFlow(items)
        val getItems = mockk<GetGroceryItemsUseCase>()
        val addUseCase = mockk<AddGroceryItemUseCase>(relaxed = true)
        val updateUseCase = mockk<UpdateGroceryItemUseCase>(relaxed = true)
        val deleteUseCase = mockk<DeleteGroceryItemUseCase>(relaxed = true)
        val toggleUseCase = mockk<ToggleCompletedUseCase>(relaxed = true)

        every { getItems.invoke() } returns itemsFlow

        val viewModel = GroceryListViewModel(
            getItems = getItems,
            addItem = addUseCase,
            updateItem = updateUseCase,
            deleteItem = deleteUseCase,
            toggleCompleted = toggleUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial
            val initial = awaitItem()
            Assert.assertEquals(2, initial.items.size)

            // When: user selects the Completed status filter
            viewModel.onStatusFilterSelected(StatusFilter.Completed)
            advanceUntilIdle()
            awaitItem() // consume filter-only update; combine will emit filtered list next
            // Then: only completed items are visible
            val completedState = awaitItem()
            Assert.assertEquals(listOf("Milk"), completedState.items.map { it.name })
        }
    }

    @Test
    fun categoryFilterFruitsShowsOnlyFruitItems() = runTest {
        // Given: a ViewModel with milk and fruit items
        val items = listOf(
            GroceryItem(1, "Milk", GroceryCategory.Milk, isCompleted = false, createdAt = 1),
            GroceryItem(2, "Bananas", GroceryCategory.Fruits, isCompleted = false, createdAt = 2),
            GroceryItem(3, "Apples", GroceryCategory.Fruits, isCompleted = true, createdAt = 3)
        )
        val itemsFlow = MutableStateFlow(items)
        val getItems = mockk<GetGroceryItemsUseCase>()
        val addUseCase = mockk<AddGroceryItemUseCase>(relaxed = true)
        val updateUseCase = mockk<UpdateGroceryItemUseCase>(relaxed = true)
        val deleteUseCase = mockk<DeleteGroceryItemUseCase>(relaxed = true)
        val toggleUseCase = mockk<ToggleCompletedUseCase>(relaxed = true)

        every { getItems.invoke() } returns itemsFlow

        val viewModel = GroceryListViewModel(
            getItems = getItems,
            addItem = addUseCase,
            updateItem = updateUseCase,
            deleteItem = deleteUseCase,
            toggleCompleted = toggleUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial
            awaitItem() // after items collection

            // When: user filters by the Fruits category
            viewModel.onCategoryFilterSelected(GroceryCategory.Fruits)
            advanceUntilIdle()
            awaitItem() // consume filter-only update; combine will emit filtered list next
            val fruitsState = awaitItem()
            // Default sort is by createdAt desc: Apples (3) then Bananas (2)
            // Then: only fruit items are shown in newest-first order
            Assert.assertEquals(listOf("Apples", "Bananas"), fruitsState.items.map { it.name })
        }
    }

    @Test
    fun editItemFlowUpdatesViaUseCaseAndShowsSnackbar() = runTest {
        // Given: a ViewModel with a single item and an Update use case that succeeds
        val item = GroceryItem(
            id = 1,
            name = "Milk",
            category = GroceryCategory.Milk,
            isCompleted = false,
            createdAt = 1
        )
        val itemsFlow = MutableStateFlow(listOf(item))
        val getItems = mockk<GetGroceryItemsUseCase>()
        val addUseCase = mockk<AddGroceryItemUseCase>(relaxed = true)
        val updateUseCase = mockk<UpdateGroceryItemUseCase>()
        val deleteUseCase = mockk<DeleteGroceryItemUseCase>(relaxed = true)
        val toggleUseCase = mockk<ToggleCompletedUseCase>(relaxed = true)

        every { getItems.invoke() } returns itemsFlow
        coEvery { updateUseCase.invoke(any()) } returns Result.success(Unit)

        val viewModel = GroceryListViewModel(
            getItems = getItems,
            addItem = addUseCase,
            updateItem = updateUseCase,
            deleteItem = deleteUseCase,
            toggleCompleted = toggleUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial
            awaitItem() // after items collection

            // When: user opens edit, changes the name and saves
            viewModel.onEditItemRequested(item.id)
            val editingState = awaitItem()
            Assert.assertNotNull(editingState.editingItem)
            Assert.assertEquals("Milk", editingState.editingItem?.name)

            viewModel.onEditConfirmed("Skimmed Milk", GroceryCategory.Milk)
            advanceUntilIdle()

            // Then: dialog closes and success snackbar is shown
            val afterEdit = awaitItem()
            Assert.assertNull(afterEdit.editingItem)
            Assert.assertEquals("Item updated", afterEdit.snackbarMessage)
        }

        coVerify {
            updateUseCase.invoke(
                withArg { updated ->
                    Assert.assertEquals(1, updated.id)
                    Assert.assertEquals("Skimmed Milk", updated.name)
                    Assert.assertEquals(GroceryCategory.Milk, updated.category)
                }
            )
        }
    }

    @Test
    fun toggleCompletedInvokesUseCaseWithCorrectItem() = runTest {
        // Given: a ViewModel with a single incomplete item
        val item = GroceryItem(
            id = 10,
            name = "Bread",
            category = GroceryCategory.Breads,
            isCompleted = false,
            createdAt = 1
        )
        val itemsFlow = MutableStateFlow(listOf(item))
        val getItems = mockk<GetGroceryItemsUseCase>()
        val addUseCase = mockk<AddGroceryItemUseCase>(relaxed = true)
        val updateUseCase = mockk<UpdateGroceryItemUseCase>(relaxed = true)
        val deleteUseCase = mockk<DeleteGroceryItemUseCase>(relaxed = true)
        val toggleUseCase = mockk<ToggleCompletedUseCase>()

        every { getItems.invoke() } returns itemsFlow
        coEvery { toggleUseCase.invoke(any()) } returns Result.success(Unit)

        val viewModel = GroceryListViewModel(
            getItems = getItems,
            addItem = addUseCase,
            updateItem = updateUseCase,
            deleteItem = deleteUseCase,
            toggleCompleted = toggleUseCase
        )

        // drain initial emissions so ViewModel has collected items and latestItems stateIn is updated
        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            advanceUntilIdle()

            // When: user toggles completion for that item
            viewModel.onToggleCompletedClicked(item.id)
            advanceUntilIdle()
            // consume snackbar emission so Turbine does not see unconsumed events
            awaitItem()
        }

        // Then: the ToggleCompleted use case is called with that item
        coVerify {
            toggleUseCase.invoke(
                withArg { toggled ->
                    Assert.assertEquals(10, toggled.id)
                    Assert.assertEquals(
                        false,
                        toggled.isCompleted
                    ) // original value passed to use-case
                }
            )
        }
    }
}