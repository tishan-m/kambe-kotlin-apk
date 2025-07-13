package com.woody.kambe.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Order(val title: String, val details: String)

@Composable
fun OrderListView(selectedDay: Float?) {
    val ordersByDay = mapOf(
        1f to listOf(Order("Order #101", "2x Product A, 1x Product B")),
        2f to listOf(Order("Order #102", "1x Product C, 3x Product D")),
        3f to listOf(Order("Order #103", "5x Product E, 2x Product F"))
    )

    val orders = selectedDay?.let { ordersByDay[it] } ?: emptyList()

    LazyColumn {
        items(orders) { order ->
            ExpandableCard(order)
        }
    }
}

@Composable
fun ExpandableCard(order: Order) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(order.title, fontWeight = FontWeight.Bold)
            if (expanded) {
                Text(order.details)
            }
        }
    }
}
