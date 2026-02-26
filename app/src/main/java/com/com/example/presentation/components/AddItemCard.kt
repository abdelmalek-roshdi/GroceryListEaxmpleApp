package com.com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.com.example.domain.model.GroceryCategory

@Composable
fun AddItemCard(
    nameInput: String,
    selectedCategory: GroceryCategory,
    onNameChanged: (String) -> Unit,
    onCategorySelected: (GroceryCategory) -> Unit,
    onAddItemClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Add New Item",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Item Name",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            OutlinedTextField(
                value = nameInput,
                onValueChange = onNameChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                placeholder = { Text("Enter grocery item...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Category",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GroceryCategory.values().forEach { category ->
                    CategoryChip(
                        category = category,
                        selected = category == selectedCategory,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddItemClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Add Item")
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: GroceryCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = when (category) {
        GroceryCategory.Milk -> Color(0xFFE3F2FD)
        GroceryCategory.Vegetables -> Color(0xFFE8F5E9)
        GroceryCategory.Fruits -> Color(0xFFFFF3E0)
        GroceryCategory.Breads -> Color(0xFFFFF8E1)
        GroceryCategory.Meats -> Color(0xFFFFEBEE)
    }
    val accent = when (category) {
        GroceryCategory.Milk -> Color(0xFF2196F3)
        GroceryCategory.Vegetables -> Color(0xFF4CAF50)
        GroceryCategory.Fruits -> Color(0xFFFF9800)
        GroceryCategory.Breads -> Color(0xFFFFC107)
        GroceryCategory.Meats -> Color(0xFFF44336)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) accent else background)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = if (selected) 0.3f else 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.displayName.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else accent
        )
    }
}

