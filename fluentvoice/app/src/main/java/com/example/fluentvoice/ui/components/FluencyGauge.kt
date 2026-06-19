package com.example.fluentvoice.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FluencyGauge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    // Get colors and severity label based on score
    val (color, severityText) = when {
        score >= 70 -> Color(0xFF10B981) to "Mild"
        score >= 40 -> Color(0xFFF59E0B) to "Moderate"
        else -> Color(0xFFEF4444) to "Severe"
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val strokeWidth = 12.dp.toPx()
            val arcSize = this.size

            // Draw background track (240-degree arc from 150 to 390 degrees)
            drawArc(
                color = Color(0xFFDAE3F2),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw filled track matching the score proportion
            val sweepAngle = (score / 100f) * 240f
            drawArc(
                color = color,
                startAngle = 150f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner Texts (Score, /100, and Severity)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "$score",
                    fontSize = (size.value * 0.28).sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1B2B5E)
                )
                Text(
                    text = "/100",
                    fontSize = (size.value * 0.11).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(bottom = (size.value * 0.03).dp)
                )
            }
            Text(
                text = severityText,
                fontSize = (size.value * 0.11).sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}
