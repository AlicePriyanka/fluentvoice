package com.example.fluentvoice.ui.screens.patient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluentvoice.data.model.TreatmentPlan
import com.example.fluentvoice.data.repository.FluentVoiceRepository

@Composable
fun PatientTreatmentScreen() {
    val user by FluentVoiceRepository.currentUser.collectAsState()
    var plan by remember { mutableStateOf<TreatmentPlan?>(null) }

    LaunchedEffect(user?.id) {
        user?.id?.let { uid ->
            plan = FluentVoiceRepository.getTreatmentPlan(uid)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9EFF9)) // BgColor
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "My Treatment Plan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B2B5E),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        val planLocal = plan
        if (planLocal == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No treatment plan assigned yet.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            }
        } else {
            // Section 1: Active Goals
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFF1B2B5E))
                        Text(text = "Active Goals", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E))
                    }

                    if (planLocal.goals.isEmpty()) {
                        Text(text = "No goals set.", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            planLocal.goals.forEach { goal ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp).padding(top = 1.dp)
                                    )
                                    Text(
                                        text = goal,
                                        fontSize = 12.sp,
                                        color = Color(0xFF374151),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Practice Exercises
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8D3E8))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, tint = Color(0xFF1B2B5E))
                        Text(text = "Practice Exercises", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2B5E))
                    }

                    if (planLocal.exercises.isEmpty()) {
                        Text(text = "No exercises assigned.", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            planLocal.exercises.forEachIndexed { i, exercise ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFDAE3F2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${i + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B2B5E)
                                        )
                                    }
                                    Text(
                                        text = exercise,
                                        fontSize = 12.sp,
                                        color = Color(0xFF374151),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Therapist Remarks
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC9A84C).copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, Color(0xFFC9A84C).copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "THERAPIST REMARKS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC9A84C)
                    )
                    Text(
                        text = planLocal.remarks,
                        fontSize = 12.sp,
                        color = Color(0xFF1B2B5E),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
