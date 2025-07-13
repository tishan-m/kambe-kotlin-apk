package com.woody.kambe.component

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

@Composable
fun SalesChartView(onBarClick: (Float) -> Unit) {
    val salesData = listOf(
        BarEntry(1f, 500f),  // Day 1 Income
        BarEntry(2f, 700f),  // Day 2 Income
        BarEntry(3f, 900f)   // Day 3 Income
    )
    val profitData = listOf(
        BarEntry(1f, 200f),  // Day 1 Profit
        BarEntry(2f, 300f),  // Day 2 Profit
        BarEntry(3f, 400f)   // Day 3 Profit
    )

    BarChartView(salesData, profitData, onBarClick)
}

@Composable
fun BarChartView(salesData: List<BarEntry>, profitData: List<BarEntry>, onBarClick: (Float) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            BarChart(context).apply {
                val salesSet = BarDataSet(salesData, "Sales").apply {
                    color = Color.BLUE
                    valueTextSize = 12f
                }
                val profitSet = BarDataSet(profitData, "Profit").apply {
                    color = Color.GREEN
                    valueTextSize = 12f
                }

                this.data = BarData(salesSet, profitSet)

                // Set the chart's value selected listener
                this.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        // Ensure both e (Entry) and h (Highlight) are not null
                        e?.let {
                            // You can use e.x for the day and e.y for the value (sales/profit)
                            onBarClick(it.x)  // Trigger onBarClick with the x value (day)
                        }
                    }

                    override fun onNothingSelected() {
                        // Handle when nothing is selected (optional)
                    }
                })

                this.invalidate()  // Redraw the chart
            }
        }
    )
}