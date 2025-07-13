package com.woody.kambe.ui.screens

import android.graphics.Color
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.woody.kambe.component.OrderListView
import com.woody.kambe.component.SalesChartView

@Composable
fun DashboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedDay by remember { mutableStateOf<Float?>(null) }

    Scaffold(
        topBar = { AppBar(selectedTab) { selectedTab = it } }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> OrderListView(selectedDay)
                1 -> SalesChartView { selectedDay = it }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    TopAppBar(
        title = { Text("Dashboard") },
        actions = {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tab for OrderListView
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                ) {
                    Text(text = "Orders", modifier = Modifier.padding(16.dp))
                }
                // Tab for SalesChartView
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                ) {
                    Text(text = "Sales", modifier = Modifier.padding(16.dp))
                }
            }
        }
    )
}