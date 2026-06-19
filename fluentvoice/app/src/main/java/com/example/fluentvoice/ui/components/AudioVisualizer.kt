package com.example.fluentvoice.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun AudioVisualizer(
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 30
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
    
    // We animate a float between 0f and 1f to trigger periodic height recalculations
    val animationTrigger by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heightTrigger"
    )

    // Store state of the heights locally
    val heights = remember { mutableStateListOf<Float>().apply { 
        addAll(List(barCount) { 4f }) 
    }}

    // When trigger changes and recording is active, generate randomized height levels
    LaunchedEffect(animationTrigger) {
        if (isRecording) {
            for (i in 0 until barCount) {
                heights[i] = Random.nextFloat() * 44f + 6f
            }
        } else {
            for (i in 0 until barCount) {
                // Return to small baseline heights when idle
                heights[i] = 4f
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEachIndexed { index, height ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1B2B5E), // Navy
                                Color(0xFFC9A84C)  // Gold
                            )
                        ),
                        alpha = 0.6f + (index % 5) * 0.08f
                    )
            )
        }
    }
}
