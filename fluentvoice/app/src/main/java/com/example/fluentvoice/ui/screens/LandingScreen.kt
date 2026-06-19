package com.example.fluentvoice.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.ui.components.FluencyGauge

@Composable
fun LandingScreen(
    onNavigateToLogin: (role: String?) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .verticalScroll(scrollState)
    ) {
        // Navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White.copy(alpha = 0.92f))
                .border(1.dp, Color(0xFFC8D3E8), RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF1B2B5E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "FluentVoice",
                    color = Color(0xFF1B2B5E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Button(
                onClick = { onNavigateToLogin(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E)),
                shape = RoundedCornerShape(50.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Get started",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Hero section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live AI Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0x1FC9A84C)) // GoldDim
                    .border(1.dp, Color(0xFFC9A84C).copy(alpha = 0.25f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF10B981))
                )
                Text(
                    text = "Whisper AI · Live analysis",
                    color = Color(0xFFC9A84C),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Understand your speech.",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1B2B5E),
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
            Text(
                text = "Own your voice.",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFC9A84C),
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Clinical-grade fluency analysis for patients and speech therapists. Powered by AI.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNavigateToLogin("patient") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2B5E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "I'm a Patient",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = { onNavigateToLogin("therapist") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDAE3F2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "I'm a Therapist",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2B5E)
                    )
                }
            }
        }

        // Mock Card report preview (matches web UI right hero block)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC8D3E8))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x141B2B5E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color(0xFF1B2B5E),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "Analysis Report",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B2B5E),
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✓ Complete",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Severity Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF59E0B).copy(alpha = 0.08f))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "⚠️", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "Moderate Stuttering",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Therapy is recommended",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gauge & Mini Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        FluencyGauge(score = 74, size = 110.dp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MetricRow(label = "Speech Rate", value = "132 wpm")
                        MetricRow(label = "Disfluencies", value = "5 events")
                        MetricRow(label = "Pauses", value = "4 total")
                    }
                }
            }
        }

        // Bento Grid display in column layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Everything you need.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B2B5E)
            )

            // Feature 1: Whisper-backed AI Analysis (Navy block)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2B5E))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color(0xFFC9A84C),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Whisper-backed AI Analysis",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Detects blocks, repetitions, prolongations, and pauses with clinical precision. Sub-30 second turnaround.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Feature 2: Privacy first
            BentoFeatureRow(
                icon = Icons.Default.Security,
                title = "Privacy First",
                desc = "Your recordings stay yours. No third-party data sharing, ever.",
                iconColor = Color(0xFF10B981)
            )

            // Feature 3: Clinical reports
            BentoFeatureRow(
                icon = Icons.Default.Assessment,
                title = "Clinical Reports",
                desc = "Fluency score, speech rate, timelines, and therapist remarks in one screen.",
                iconColor = Color(0xFFF59E0B)
            )

            // Feature 4: Progress tracking
            BentoFeatureRow(
                icon = Icons.Default.TrendingUp,
                title = "Progress Tracking",
                desc = "Week-by-week fluency level charts. See how your speech rate improves.",
                iconColor = Color(0xFFEC4899)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF9CA3AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color(0xFF1B2B5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BentoFeatureRow(
    icon: ImageVector,
    title: String,
    desc: String,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFC8D3E8))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2B5E),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
